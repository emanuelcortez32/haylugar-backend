package ar.com.greenbundle.haylugar.dao;


import ar.com.greenbundle.haylugar.dto.BookingDto;
import ar.com.greenbundle.haylugar.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class BookingDao {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private SpotDao spotDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private PaymentDao paymentDao;

    public Mono<BookingDto> getBooking(String bookingId) {
        return bookingRepository.findById(bookingId)
                .map(booking -> new BookingDto().dtoFromEntity(booking))
                .flatMap(booking -> userDao.getUser(booking.getClient().getId())
                        .map(client -> {
                            booking.setClient(client);

                            return booking;
                        }))
                .flatMap(booking -> spotDao.getSpot(booking.getSpot().getId())
                        .map(spot -> {
                            booking.setSpot(spot);

                            return booking;
                        }))
                .flatMap(booking -> {
                    if(booking.getPayment() != null && booking.getPayment().getId() != null) {
                        return paymentDao.getPayment(booking.getPayment().getId())
                                .map(payment -> {
                                    booking.setPayment(payment);

                                    return booking;
                                });
                    }

                    return Mono.just(booking);
                });
    }
    public Flux<BookingDto> getBookingsByUserClient(String userId) {
        return bookingRepository.findBookingsByUserId(userId)
                .map(booking -> new BookingDto().dtoFromEntity(booking))
                .flatMap(booking -> userDao.getUser(booking.getClient().getId())
                        .map(client -> {
                            booking.setClient(client);

                            return booking;
                        }))
                .flatMap(booking -> spotDao.getSpot(booking.getSpot().getId())
                        .map(spot -> {
                            booking.setSpot(spot);

                            return booking;
                        }))
                .flatMap(booking -> paymentDao.getPayment(booking.getPayment().getId())
                        .map(payment -> {
                            booking.setPayment(payment);

                            return booking;
                        }));
    }

    public Flux<BookingDto> getBookingsBySpot(String spotId) {
        return bookingRepository.findBookingsBySpotId(spotId)
                .map(booking -> new BookingDto().dtoFromEntity(booking))
                .flatMap(booking -> userDao.getUser(booking.getClient().getId())
                        .map(client -> {
                            booking.setClient(client);

                            return booking;
                        }))
                .flatMap(booking -> spotDao.getSpot(booking.getSpot().getId())
                        .map(spot -> {
                            booking.setSpot(spot);

                            return booking;
                        }))
                .flatMap(booking -> paymentDao.getPayment(booking.getPayment().getId())
                        .map(payment -> {
                            booking.setPayment(payment);

                            return booking;
                        }));
    }

    public Mono<BookingDto> saveBooking(BookingDto bookingDto) {
        return bookingRepository.save(new BookingDto().dtoToEntity(bookingDto))
                .flatMap(savedBooking -> getBooking(savedBooking.getId()));
    }
}
