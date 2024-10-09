package ar.com.greenbundle.haylugar.service;

import ar.com.greenbundle.haylugar.dto.CreateBookingData;
import ar.com.greenbundle.haylugar.dto.CreatePaymentData;
import ar.com.greenbundle.haylugar.dto.UpdateSpotData;
import ar.com.greenbundle.haylugar.dto.constants.BookingState;
import ar.com.greenbundle.haylugar.entities.BookingItem;
import ar.com.greenbundle.haylugar.entities.PaymentItem;
import ar.com.greenbundle.haylugar.entities.SpotItem;
import ar.com.greenbundle.haylugar.entities.UserItem;
import ar.com.greenbundle.haylugar.exceptions.BookingFinishedException;
import ar.com.greenbundle.haylugar.exceptions.CreateBookingException;
import ar.com.greenbundle.haylugar.exceptions.ResourceNotFoundException;
import ar.com.greenbundle.haylugar.repositories.BookingRepository;
import ar.com.greenbundle.haylugar.repositories.SpotRepository;
import ar.com.greenbundle.haylugar.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicReference;

import static ar.com.greenbundle.haylugar.dto.constants.BookingState.FINISHED;
import static ar.com.greenbundle.haylugar.dto.constants.BookingState.PENDING;
import static ar.com.greenbundle.haylugar.dto.constants.SpotState.BUSY;
import static ar.com.greenbundle.haylugar.dto.constants.SpotState.FREE;
import static ar.com.greenbundle.haylugar.dto.constants.SpotState.RESERVED;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private SpotService spotService;
    @Autowired
    private UserService userService;
    @Autowired
    private PaymentService paymentService;

    public Flux<BookingItem> getBookingsByUser(String userEmail) {
        return userService.getUserByEmail(userEmail)
                .flatMapMany(u -> Flux.merge(
                        bookingRepository.findBookingsByClientUserId(u.getId()),
                        bookingRepository.findBookingsBySpotOwnerId(u.getId())
                )).distinct();
    }

    public Mono<BookingItem> createBooking(String userEmail, CreateBookingData bookingData) {

        AtomicReference<SpotItem> spotItemAtomicReference = new AtomicReference<>();

        Mono<UserItem> userOperation = userService.getUserByEmail(userEmail);
        Mono<SpotItem> spotOperation = spotService.getSpotById(bookingData.getSpotId());
        Mono<PaymentItem> paymentOperation = paymentService.createPayment(CreatePaymentData.builder()
                        .paymentMethod(bookingData.getPaymentMethod())
                        .currency(bookingData.getPaymentCurrency())
                .build());

        return userOperation
                .zipWhen(u -> spotOperation)
                .doOnNext(tuple -> {
                    SpotItem spot = tuple.getT2();

                    if(!spot.getSpotState().equals(FREE))
                        throw new CreateBookingException("Spot is not free");})
                .flatMap(tuple -> {
                    UserItem user = tuple.getT1();
                    SpotItem spot = tuple.getT2();

                    spotItemAtomicReference.set(spot);

                    return bookingRepository.findBookingsBySpotId(spot.getId()).collectList()
                            .doOnNext(bookings -> {
                                boolean isFull = bookings.stream().filter(b -> !b.getState().equals(FINISHED))
                                        .toList().size() >= spot.getCapacity();

                                if (isFull)
                                     throw new CreateBookingException("Spot is full");

                                //if (user.getId().equals(spot.getLandLordUserId()))
                                //    throw new CreateBookingException("The user client and the landlord user the same");
                                })
                            .flatMap(__ -> {
                                BookingItem booking = BookingItem.builder()
                                        .clientUserId(user.getId())
                                        .spotOwnerId(spot.getLandLordUserId())
                                        .spotId(spot.getId())
                                        .state(PENDING)
                                        .build();

                                return Mono.just(booking);});})
                .flatMap(booking -> paymentOperation.flatMap(payment -> {
                    booking.setPaymentId(payment.getId());

                    return bookingRepository.insert(booking)
                            .flatMap(savedBooking -> {
                                UpdateSpotData updateSpotData = UpdateSpotData.builder()
                                        .spotState(RESERVED)
                                        .build();

                                SpotItem spot = spotItemAtomicReference.get();

                                return spotService.updateSpotAssociatedToUser(spot.getLandLordUserId(), spot.getId(), updateSpotData)
                                        .then(Mono.just(savedBooking));
                            });
                }));
    }

    public Mono<BookingItem> finishBooking(String bookingId) {
        return bookingRepository.findById(bookingId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Booking Not Found")))
                .flatMap(booking -> spotService.getSpotById(booking.getSpotId())
                        .flatMap(spot -> {

                            if (!booking.getState().equals(BookingState.IN_PROGRESS))
                                return Mono.error(new BookingFinishedException("Booking is not in progress"));

                            LocalTime startTime = LocalTime.parse(booking.getStartTime());
                            LocalTime endTime = LocalTime.ofInstant(Instant.now(), ZoneOffset.UTC);

                            long totalMinutes = ChronoUnit.MINUTES.between(startTime, endTime);

                            if (totalMinutes < 30)
                                totalMinutes = 30;

                            double totalPrice = spot.getPricePerMinute() * totalMinutes;


                            booking.setEndTime(endTime.toString());
                            booking.setState(BookingState.FINISHED);
                            booking.setTotalMinutes(totalMinutes);

                            spot.setSpotState(FREE);

                            return bookingRepository.save(booking)
                                    .flatMap(savedBooking -> paymentService.processPaymentForBooking(savedBooking, totalPrice)
                                            .then(Mono.just(savedBooking)));
                        }));
    }

    public Mono<BookingItem> startBooking(String bookingId) {
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

    public Mono<BookingItem> cancelBooking(String bookingId) {
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

                            spot.setSpotState(FREE);

                            return bookingRepository.save(booking);
                        }));
    }
}
