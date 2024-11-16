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
import ar.com.greenbundle.haylugar.pojo.constants.BookingAction;
import ar.com.greenbundle.haylugar.pojo.constants.BookingState;
import ar.com.greenbundle.haylugar.pojo.constants.PaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ar.com.greenbundle.haylugar.pojo.constants.BookingState.CANCELED;
import static ar.com.greenbundle.haylugar.pojo.constants.BookingState.FINISHED;
import static ar.com.greenbundle.haylugar.pojo.constants.BookingState.IN_PROGRESS;
import static ar.com.greenbundle.haylugar.pojo.constants.BookingUserAs.CLIENT;
import static ar.com.greenbundle.haylugar.pojo.constants.BookingUserAs.HOST;
import static ar.com.greenbundle.haylugar.pojo.constants.PaymentProvider.NOT_DEFINED;
import static ar.com.greenbundle.haylugar.pojo.constants.PaymentStatus.PENDING;
import static ar.com.greenbundle.haylugar.pojo.constants.SpotState.AVAILABLE;
import static ar.com.greenbundle.haylugar.pojo.constants.SpotState.BUSY;

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
    @Autowired
    private LocationService locationService;

    public Flux<BookingDto> findBookingsByUser(String userId) {
        return bookingDao.getBookingsByUser(userId)
                .flatMap(booking -> setUserRoleForBooking(userId, booking))
                .distinct();
    }

    public Mono<BookingDto> findBookingByUser(String userId, String bookingId) {
        return bookingDao.getBooking(bookingId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Booking not found")))
                .flatMap(booking -> validateUserAccessToBooking(userId, booking));
    }

    @Transactional
    public Mono<String> createBooking(String clientUserId, BookingDto bookingDto) {
        Mono<UserDto> userOperation = userService.findUser(clientUserId);
        Mono<SpotDto> spotOperation = spotService.findSpot(bookingDto.getSpot().getId());
        PaymentDto initialPayment = createInitialPayment(bookingDto);

        bookingDto.setState(BookingState.PENDING);

        return userOperation
                .zipWhen(__ -> spotOperation)
                .flatMap(tuple -> handleBookingCreation(tuple.getT1(), tuple.getT2(), bookingDto, initialPayment));
    }

    public Mono<BookingDto> performActionOnBooking(String bookingId, BookingAction action) {
        return switch (action) {
            case START -> startBooking(bookingId);
            case FINISH -> finishBooking(bookingId);
            case CANCEL -> cancelBooking(bookingId);
        };
    }

    @Transactional
    public Mono<BookingDto> startBooking(String bookingId) {
        return bookingDao.getBooking(bookingId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Booking Not Found")))
                .flatMap(booking -> spotService.findSpot(booking.getSpot().getId())
                        .flatMap(spot -> processStartBooking(booking, spot)));
    }

    private Mono<BookingDto> finishBooking(String bookingId) {
        return bookingDao.getBooking(bookingId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Booking Not found")))
                .flatMap(booking -> spotService.findSpot(booking.getSpot().getId())
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Spot not found")))
                        .flatMap(spot -> processFinishBooking(booking, spot)));
    }

    public Mono<BookingDto> cancelBooking(String bookingId) {

        return bookingDao.getBooking(bookingId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Booking Not Found")))
                .flatMap(booking -> spotService.findSpot(booking.getSpot().getId())
                        .flatMap(spot -> processCancelBooking(booking, spot)));
    }

    private Mono<BookingDto> setUserRoleForBooking(String userId, BookingDto booking) {
        if (booking.getClient().getId().equals(userId))
            booking.setBookingUserAs(CLIENT);
        if (booking.getSpotOwner().getId().equals(userId))
            booking.setBookingUserAs(HOST);

        return Mono.just(booking);
    }

    private Mono<BookingDto> validateUserAccessToBooking(String userId, BookingDto booking) {
        if (!booking.getSpotOwner().getId().equals(userId) && !booking.getClient().getId().equals(userId)) {
            return Mono.error(new ResourceNotFoundException("Booking not found"));
        }
        return setUserRoleForBooking(userId, booking);
    }

    private PaymentDto createInitialPayment(BookingDto bookingDto) {
        PaymentDto initialPayment = bookingDto.getPayment();
        initialPayment.setLastStatus(PENDING);
        initialPayment.setProvider(NOT_DEFINED);
        initialPayment.setTransactionDetails(List.of(PaymentTransactionDetail.builder()
                .date(OffsetDateTime.now())
                .status(PENDING)
                .statusDetail("Initial payment creation")
                .build()));
        return initialPayment;
    }

    private Mono<String> handleBookingCreation(UserDto user, SpotDto spot, BookingDto bookingDto, PaymentDto initialPayment) {
        bookingDto.setSpotOwner(spot.getLandLord());
        bookingDto.setClient(user);

        return bookingDao.getBookingsBySpot(spot.getId())
                .collectList()
                .map(bookings -> validateSpotAvailability(bookings, spot, bookingDto))
                .flatMap(__ -> savePaymentAndBooking(initialPayment, bookingDto))
                .map(EntityDto::getId);
    }

    private boolean validateSpotAvailability(List<BookingDto> bookings, SpotDto spot, BookingDto bookingDto) {
        if (!spot.getState().equals(AVAILABLE))
            throw new CreateBookingException("Spot is not available");

        boolean isFull = bookings.stream().filter(b -> !b.getState().equals(FINISHED)
                && !b.getState().equals(CANCELED)).toList().size() >= spot.getCapacity();

        if (isFull)
            throw new CreateBookingException("Spot is full");

        if (bookingDto.getClient().getId().equals(spot.getLandLord().getId()))
            throw new CreateBookingException("Client and landlord are the same user");

        return true;
    }

    private Mono<BookingDto> savePaymentAndBooking(PaymentDto initialPayment, BookingDto bookingDto) {
        return paymentService.savePayment(initialPayment)
                .doOnNext(paymentId -> {
                    initialPayment.setId(paymentId);
                    bookingDto.setPayment(initialPayment);
                })
                .then(bookingDao.saveBooking(bookingDto));
    }

    private Mono<BookingDto> processStartBooking(BookingDto booking, SpotDto spot) {

        if (!booking.getState().equals(BookingState.PENDING))
            return Mono.error(new BookingFinishedException(String.format("Booking is not in pending, current state [%s]",
                    booking.getState())));

        return locationService.getAssignedZoneFromCoordinates(spot.getLocation().getX(), spot.getLocation().getY())
                .switchIfEmpty(Mono.error(new CreateBookingException("Spot is not in allowed zone")))
                .flatMap(___ -> {
                    LocalDateTime startDate = LocalDateTime.now(ZoneOffset.UTC);
                    LocalTime startTime = LocalTime.ofInstant(Instant.now(), ZoneOffset.UTC);

                    booking.setStartDate(startDate);
                    booking.setStartTime(startTime.toString());
                    booking.setState(BookingState.IN_PROGRESS);

                    return bookingDao.saveBooking(booking)
                            .flatMap(savedBooking -> bookingDao.getBookingsBySpot(spot.getId())
                                    .collectList()
                                    .flatMap(bookings -> {
                                        int spotSize = (int) bookings.stream()
                                                .filter(b -> !b.getState().equals(FINISHED) && !b.getState().equals(CANCELED))
                                                .count();

                                        if (spotSize == spot.getCapacity()) {
                                            spot.setState(BUSY);
                                            return spotService.updateSpot(spot).then(Mono.just(savedBooking));
                                        }
                                        return Mono.just(savedBooking);
                                    })
                            );
                });
    }

    private Mono<BookingDto> processFinishBooking(BookingDto booking, SpotDto spot) {
        final int MIN_BOOKING_MINUTES = 30;

        if (!booking.getState().equals(IN_PROGRESS))
            return Mono.error(new BookingFinishedException(String.format("Booking is not in progress, current state [%s]",
                    booking.getState())));

        double totalPrice;

        LocalDateTime startDate = booking.getStartDate();
        LocalDateTime endDate = LocalDateTime.now(ZoneOffset.UTC);

        LocalTime endTime = endDate.toLocalTime();

        long totalMinutes = ChronoUnit.MINUTES.between(startDate, endDate);

        if (totalMinutes >= MIN_BOOKING_MINUTES) {
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
                            .flatMap(savedBooking -> bookingDao.getBookingsBySpot(spot.getId())
                                    .collectList()
                                    .flatMap(bookings -> {
                                        int spotSize = (int) bookings.stream()
                                                .filter(b -> !b.getState().equals(FINISHED) && !b.getState().equals(CANCELED))
                                                .count();

                                        if (spotSize < spot.getCapacity()) {
                                            spot.setState(AVAILABLE);
                                            return spotService.updateSpot(spot).then(Mono.just(savedBooking));
                                        }
                                        return Mono.just(savedBooking);
                                    })
                            );
                });
    }

    private Mono<BookingDto> processCancelBooking(BookingDto booking, SpotDto spot) {
        if (booking.getState().equals(IN_PROGRESS)) {
            return finishBooking(booking.getId());

        } else if (booking.getState().equals(BookingState.PENDING)) {

            booking.setState(CANCELED);

            return bookingDao.saveBooking(booking)
                    .flatMap(savedBooking -> bookingDao.getBookingsBySpot(spot.getId())
                            .collectList()
                            .flatMap(bookings -> {
                                int spotSize = (int) bookings.stream()
                                        .filter(b -> !b.getState().equals(FINISHED) && !b.getState().equals(CANCELED))
                                        .count();

                                if (spotSize < spot.getCapacity()) {
                                    spot.setState(AVAILABLE);
                                    return spotService.updateSpot(spot)
                                            .then(Mono.just(savedBooking));
                                }
                                return Mono.just(savedBooking);
                            }))
                    .flatMap(savedBooking -> paymentService.getPayment(savedBooking.getPayment().getId())
                            .flatMap(payment -> {
                                payment.setLastStatus(PaymentStatus.CANCELED);
                                payment.getTransactionDetails().add(PaymentTransactionDetail.builder()
                                        .date(OffsetDateTime.now())
                                        .status(PaymentStatus.CANCELED)
                                        .statusDetail("Payment is canceled due booking cancellation")
                                        .build());

                                return paymentService.savePayment(payment)
                                        .then(Mono.just(savedBooking));
                            }));
        } else {
            return Mono.error(new BookingFinishedException("Booking is not in correct state"));
        }
    }
}
