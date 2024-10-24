package ar.com.greenbundle.haylugar.service;

import ar.com.greenbundle.haylugar.dao.BookingDao;
import ar.com.greenbundle.haylugar.dto.BookingDto;
import ar.com.greenbundle.haylugar.dto.EntityDto;
import ar.com.greenbundle.haylugar.dto.PaymentDto;
import ar.com.greenbundle.haylugar.dto.SpotDto;
import ar.com.greenbundle.haylugar.dto.UserDto;
import ar.com.greenbundle.haylugar.exceptions.BookingFinishedException;
import ar.com.greenbundle.haylugar.exceptions.CreateBookingException;
import ar.com.greenbundle.haylugar.exceptions.ResourceNotFoundException;
import ar.com.greenbundle.haylugar.pojo.PaymentTransactionDetail;
import ar.com.greenbundle.haylugar.pojo.constants.BookingState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ar.com.greenbundle.haylugar.pojo.constants.BookingState.FINISHED;
import static ar.com.greenbundle.haylugar.pojo.constants.BookingState.IN_PROGRESS;
import static ar.com.greenbundle.haylugar.pojo.constants.BookingUserAs.CLIENT;
import static ar.com.greenbundle.haylugar.pojo.constants.BookingUserAs.HOST;
import static ar.com.greenbundle.haylugar.pojo.constants.PaymentProvider.NOT_DEFINED;
import static ar.com.greenbundle.haylugar.pojo.constants.PaymentStatus.PENDING;
import static ar.com.greenbundle.haylugar.pojo.constants.SpotState.AVAILABLE;

@Service
public class BookingService {
    @Autowired
    private BookingDao bookingDao;
    @Autowired
    private SpotService spotService;
    @Autowired
    private UserService userService;
    @Autowired
    private PaymentService paymentService;

    public Flux<BookingDto> findBookingsByUser(String userId) {
        return bookingDao.getBookingsByUser(userId)
                .flatMap(booking -> {
                    if(booking.getClient().getId().equals(userId))
                        booking.setBookingUserAs(CLIENT);
                    if(booking.getSpotOwner().getId().equals(userId))
                        booking.setBookingUserAs(HOST);

                    return Mono.just(booking);
                })
                .distinct();
    }

    public Mono<BookingDto> findBookingByUser(String userId, String bookingId) {
        return bookingDao.getBooking(bookingId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Booking not found")))
                .flatMap(booking -> {
                    if(!booking.getSpotOwner().getId().equals(userId) && !booking.getClient().getId().equals(userId))
                        return Mono.error(new ResourceNotFoundException("Booking not found"));

                    if(booking.getClient().getId().equals(userId))
                        booking.setBookingUserAs(CLIENT);
                    if(booking.getSpotOwner().getId().equals(userId))
                        booking.setBookingUserAs(HOST);

                    return Mono.just(booking);
                });
    }

    public Mono<BookingDto> performActionOnBooking(String bookingId, String action) {
        return switch (action) {
            case "finish" -> finishBooking(bookingId);
            default -> Mono.error(new BookingFinishedException("Action not allowed"));
        };
    }

    private Mono<BookingDto> finishBooking(String bookingId) {
        final int MIN_BOOKING_MINUTES = 30;

        return bookingDao.getBooking(bookingId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Booking Not found")))
                .flatMap(booking -> userService.findUser(booking.getSpotOwner().getId())
                        .zipWith(userService.findUser(booking.getClient().getId()))
                        .flatMap(tuple -> {
                           booking.setSpotOwner(tuple.getT1());
                           booking.setClient(tuple.getT2());

                           return Mono.just(booking);
                        }))
                .flatMap(booking -> spotService.findSpot(booking.getSpot().getId())
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Spot not found")))
                        .flatMap(spot -> {

                            if(!booking.getState().equals(IN_PROGRESS))
                                return Mono.error(new BookingFinishedException(String.format("Booking is not in progress, current state [%s]",
                                        booking.getState())));

                            double totalPrice;

                            LocalDateTime startDate = booking.getStartDate();
                            LocalDateTime endDate = LocalDateTime.now(ZoneOffset.UTC);

                            LocalTime endTime = endDate.toLocalTime();

                            long totalMinutes = ChronoUnit.MINUTES.between(startDate, endDate);

                            if(totalMinutes >= MIN_BOOKING_MINUTES) {
                                totalPrice = spot.getPricePerMinute() * totalMinutes;
                            } else {
                                totalPrice = spot.getPricePerMinute() * MIN_BOOKING_MINUTES;
                            }

                            booking.setEndDate(endDate);
                            booking.setEndTime(endTime.toString());
                            booking.setTotalMinutes(totalMinutes);
                            booking.setState(FINISHED);

                            spot.setState(AVAILABLE);

                            return paymentService.processPaymentForBooking(booking, totalPrice)
                                    .flatMap(payment -> {
                                        booking.setPayment(payment);

                                        return bookingDao.saveBooking(booking)
                                                .doOnNext(__ -> spotService.updateSpot(spot))
                                                .then(Mono.just(booking));
                                    });
                        }));
    }

    public Mono<String> createBooking(String clientUserId, BookingDto bookingDto) {
        Mono<UserDto> userOperation = userService.findUser(clientUserId);
        Mono<SpotDto> spotOperation = spotService.findSpot(bookingDto.getSpot().getId());

        PaymentDto initialPayment = bookingDto.getPayment();

        initialPayment.setLastStatus(PENDING);
        initialPayment.setProvider(NOT_DEFINED);
        initialPayment.setTransactionDetails(List.of(PaymentTransactionDetail.builder()
                        .date(OffsetDateTime.now())
                        .status(PENDING)
                        .statusDetail("Initial payment creation")
                .build()));

        bookingDto.setState(BookingState.PENDING);

        return userOperation
                .zipWhen(user -> spotOperation)
                .flatMap(tuple -> {
                    UserDto user = tuple.getT1();
                    SpotDto spot = tuple.getT2();

                    bookingDto.setSpotOwner(spot.getLandLord());
                    bookingDto.setClient(user);

                    if(!spot.getState().equals(AVAILABLE))
                        throw new CreateBookingException("Spot is not available");

                    return bookingDao.getBookingsBySpot(spot.getId())
                            .collectList()
                            .map(bookings -> {
                                boolean isFull = bookings.stream().filter(b -> !b.getState().equals(FINISHED))
                                        .toList().size() >= spot.getCapacity();

                                if (isFull)
                                    throw new CreateBookingException("Spot is full");

                                if (user.getId().equals(spot.getLandLord().getId()))
                                    throw new CreateBookingException("The client user and the landlord user is the same");

                                return bookingDto;})
                            .flatMap(bookingToSave -> paymentService.savePayment(initialPayment)
                                    .doOnNext(paymentId -> {
                                        initialPayment.setId(paymentId);
                                        bookingToSave.setPayment(initialPayment);
                                    })
                                    .then(Mono.just(bookingToSave)))
                            .flatMap(bookingToSave -> bookingDao.saveBooking(bookingToSave))
                            .map(EntityDto::getId);
                });
    }

    /*

    public Mono<BookingEntity> startBooking(String bookingId) {
        return bookingRepository.findById(bookingId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Booking Not Found")))
                .flatMap(booking -> spotService.getSpotById(booking.getSpotId())
                        .flatMap(spot -> {

                            if (!booking.getState().equals(PENDING))
                                return Mono.error(new BookingFinishedException("Booking is not pending"));

                            LocalTime startTime = LocalTime.ofInstant(Instant.now(), ZoneOffset.UTC);

                            booking.setStartTime(startTime.toString());
                            booking.setState(BookingState.IN_PROGRESS);

                            return bookingRepository.save(booking)
                                    .flatMap(saveBooking -> {
                                        UpdateSpotData updateSpotData = UpdateSpotData.builder()
                                                .spotState(BUSY)
                                                .build();

                                        return spotService.updateSpotAssociatedToUser(spot.getLandLordUserId(),
                                                spot.getId(), updateSpotData)
                                                .then(Mono.just(saveBooking));
                                    });
                        }));
    }

    public Mono<BookingEntity> cancelBooking(String bookingId) {
        return bookingRepository.findById(bookingId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Booking Not Found")))
                .flatMap(booking -> spotService.getSpotById(booking.getSpotId())
                        .flatMap(spot -> {

                            if (!booking.getState().equals(PENDING))
                                return Mono.error(new BookingFinishedException("Booking is not pending"));

                            LocalTime startTime = LocalTime.ofInstant(Instant.now(), ZoneOffset.UTC);
                            LocalTime endTime = startTime.plusMinutes(30);

                            booking.setStartTime(startTime.toString());
                            booking.setEndTime(endTime.toString());
                            booking.setState(BookingState.CANCELED);

                            spot.setSpotState(AVAILABLE);

                            return bookingRepository.save(booking);
                        }));
    }

     */
}
