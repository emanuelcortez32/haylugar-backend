package ar.com.velascosoft.haylugar.service;

import ar.com.velascosoft.haylugar.dao.BookingDao;
import ar.com.velascosoft.haylugar.dto.BookingDto;
import ar.com.velascosoft.haylugar.dto.EntityDto;
import ar.com.velascosoft.haylugar.dto.PaymentDto;
import ar.com.velascosoft.haylugar.dto.SpotDto;
import ar.com.velascosoft.haylugar.dto.UserDto;
import ar.com.velascosoft.haylugar.exceptions.BookingFinishedException;
import ar.com.velascosoft.haylugar.exceptions.CreateBookingException;
import ar.com.velascosoft.haylugar.exceptions.ResourceNotFoundException;
import ar.com.velascosoft.haylugar.pojo.PaymentTransactionDetail;
import ar.com.velascosoft.haylugar.pojo.constants.BookingAction;
import ar.com.velascosoft.haylugar.pojo.constants.BookingState;
import ar.com.velascosoft.haylugar.pojo.constants.Currency;
import ar.com.velascosoft.haylugar.pojo.constants.PaymentMethod;
import ar.com.velascosoft.haylugar.pojo.constants.PaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static ar.com.velascosoft.haylugar.pojo.constants.BookingState.CANCELED;
import static ar.com.velascosoft.haylugar.pojo.constants.BookingState.FINISHED;
import static ar.com.velascosoft.haylugar.pojo.constants.BookingState.IN_PROGRESS;
import static ar.com.velascosoft.haylugar.pojo.constants.BookingUserAs.CLIENT;
import static ar.com.velascosoft.haylugar.pojo.constants.BookingUserAs.HOST;
import static ar.com.velascosoft.haylugar.pojo.constants.PaymentProvider.NOT_DEFINED;
import static ar.com.velascosoft.haylugar.pojo.constants.PaymentStatus.PENDING;
import static ar.com.velascosoft.haylugar.pojo.constants.SpotState.AVAILABLE;
import static ar.com.velascosoft.haylugar.pojo.constants.SpotState.BUSY;

@Service
public class BookingService {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private BookingDao bookingDao;
    @Autowired
    private LocationService locationService;
    @Autowired
    private SpotService spotService;

    public Flux<BookingDto> findBookingsByUser(String userId) {
        return bookingDao.getBookingsByUserClient(userId)
                .flatMap(booking -> setUserRoleForBooking(userId, booking))
                .distinct();
    }

    public Mono<BookingDto> findBookingByUser(String userId, String bookingId) {
        return bookingDao.getBooking(bookingId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Booking not found")))
                .flatMap(booking -> validateUserAccessToBooking(userId, booking));
    }

    @Transactional
    public Mono<String> createBooking(String userClientId, String spotId, PaymentMethod paymentMethod, Currency paymentCurrency) {

        BookingDto bookingDto = BookingDto.builder()
                .client(UserDto.builder().id(userClientId).build())
                .spot(SpotDto.builder().id(spotId).build())
                .payment(PaymentDto.builder().method(paymentMethod).currency(paymentCurrency).build())
                .state(BookingState.PENDING)
                .build();

        preparePayment(bookingDto);

        return handleBookingCreation(bookingDto);
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
                .flatMap(this::processStartBooking);
    }

    private Mono<BookingDto> finishBooking(String bookingId) {
        return bookingDao.getBooking(bookingId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Booking Not found")))
                .flatMap(this::processFinishBooking);
    }

    public Mono<BookingDto> cancelBooking(String bookingId) {

        return bookingDao.getBooking(bookingId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Booking Not Found")))
                .flatMap(this::processCancelBooking);
    }

    private Mono<BookingDto> setUserRoleForBooking(String userId, BookingDto booking) {
        if (booking.getClient().getId().equals(userId))
            booking.setBookingUserAs(CLIENT);
        if (booking.getSpot().getLandLord().getId().equals(userId))
            booking.setBookingUserAs(HOST);

        return Mono.just(booking);
    }

    private Mono<BookingDto> validateUserAccessToBooking(String userId, BookingDto booking) {
        if (!booking.getSpot().getLandLord().getId().equals(userId) && !booking.getClient().getId().equals(userId)) {
            return Mono.error(new ResourceNotFoundException("Booking not found"));
        }
        return setUserRoleForBooking(userId, booking);
    }

    private void preparePayment(BookingDto bookingDto) {
        PaymentDto initialPayment = bookingDto.getPayment();
        List<PaymentTransactionDetail> transactionDetails = new ArrayList<>();

        transactionDetails.add(PaymentTransactionDetail.builder()
                .date(OffsetDateTime.now())
                .status(PENDING)
                .statusDetail("Initial payment creation")
                .build());

        initialPayment.setLastStatus(PENDING);
        initialPayment.setProvider(NOT_DEFINED);
        initialPayment.setTransactionDetails(transactionDetails);

        bookingDto.setPayment(initialPayment);
    }

    private Mono<String> handleBookingCreation(BookingDto bookingDto) {

        return bookingDao.getBookingsBySpot(bookingDto.getSpot().getId()).collectList()
                .map(bookings -> {
                    if(bookings.isEmpty()) {
                        return true;
                    } else {
                        return validateSpotAvailability(bookings, bookings.get(0).getSpot(), bookingDto);
                    }
                })
                .flatMap(__ -> spotService.findSpot(bookingDto.getSpot().getId()))
                .map(spot -> {
                    bookingDto.setSpot(spot);
                    return bookingDto;
                })
                .flatMap(__ -> savePaymentAndBooking(bookingDto))
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

    private Mono<BookingDto> savePaymentAndBooking(BookingDto bookingDto) {
        return bookingDao.saveBooking(bookingDto)
                .flatMap(savedBooking -> paymentService.createPaymentSkeleton(savedBooking.getClient(), bookingDto.getPayment())
                        .flatMap(skeletonPayment -> paymentService.savePayment(skeletonPayment))
                        .flatMap(savedPayment -> {
                            savedBooking.setPayment(savedPayment);
                            return bookingDao.saveBooking(savedBooking);
                        }));
    }

    private Mono<BookingDto> processStartBooking(BookingDto booking) {
        SpotDto spot = booking.getSpot();

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
                            .doOnNext(savedBooking -> bookingDao.getBookingsBySpot(savedBooking.getSpot().getId()).collectList()
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .subscribe(bookings -> {
                                        int spotSize = (int) bookings.stream()
                                                .filter(b -> !b.getState().equals(FINISHED) && !b.getState().equals(CANCELED))
                                                .count();

                                        if (spotSize == spot.getCapacity()) {
                                            spot.setState(BUSY);
                                            spotService.updateSpot(spot);
                                        }
                                    }, throwable -> {
                                    })
                            );
                });
    }

    private Mono<BookingDto> processFinishBooking(BookingDto booking) {
        SpotDto spot = booking.getSpot();

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

        return paymentService.processPaymentForBooking(booking, totalPrice)
                .map(processedPayment -> {
                    booking.setPayment(processedPayment);
                    return booking;
                })
                .flatMap(b -> bookingDao.saveBooking(b))
                .doOnNext(savedBooking -> bookingDao.getBookingsBySpot(savedBooking.getSpot().getId())
                        .collectList()
                        .subscribeOn(Schedulers.boundedElastic())
                        .subscribe(bookings -> {
                            int spotSize = (int) bookings.stream()
                                    .filter(b -> !b.getState().equals(FINISHED) && !b.getState().equals(CANCELED))
                                    .count();

                            if (spotSize < spot.getCapacity()) {
                                spot.setState(AVAILABLE);
                                spotService.updateSpot(spot);
                            }
                        }, throwable -> {
                        })
                );
    }

    private Mono<BookingDto> processCancelBooking(BookingDto booking) {
        SpotDto spot = booking.getSpot();

        if (booking.getState().equals(IN_PROGRESS)) {
            return finishBooking(booking.getId());

        } else if (booking.getState().equals(BookingState.PENDING)) {

            booking.setState(CANCELED);

            return bookingDao.saveBooking(booking)
                    .doOnNext(savedBooking -> bookingDao.getBookingsBySpot(spot.getId()).collectList()
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe(bookings -> {
                                int spotSize = (int) bookings.stream()
                                        .filter(b -> !b.getState().equals(FINISHED) && !b.getState().equals(CANCELED))
                                        .count();

                                if (spotSize < spot.getCapacity()) {
                                    spot.setState(AVAILABLE);
                                    spotService.updateSpot(spot);
                                }
                            }, throwable -> {
                            })
                    )
                    .doOnNext(savedBooking -> paymentService.getPayment(savedBooking.getPayment().getId())
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe(payment -> {
                                payment.setLastStatus(PaymentStatus.CANCELED);
                                payment.getTransactionDetails().add(PaymentTransactionDetail.builder()
                                        .date(OffsetDateTime.now())
                                        .status(PaymentStatus.CANCELED)
                                        .statusDetail("Payment is canceled due booking cancellation")
                                        .build());

                                paymentService.savePayment(payment);
                            }, throwable -> {
                            })
                    );
        } else {
            return Mono.error(new BookingFinishedException("Booking is not in correct state"));
        }
    }
}
