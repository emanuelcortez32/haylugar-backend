package ar.com.greenbundle.haylugar.dao;


import ar.com.greenbundle.haylugar.dto.BookingDto;
import ar.com.greenbundle.haylugar.repositories.BookingRepository;
import ar.com.greenbundle.haylugar.repositories.PaymentRepository;
import ar.com.greenbundle.haylugar.repositories.SpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class BookingDao {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private SpotRepository spotRepository;
    @Autowired
    private UserDao userDao;
    @Autowired
    private PaymentRepository paymentRepository;

    public Mono<BookingDto> getBooking(String bookingId) {
        return bookingRepository.findById(bookingId)
                .map(booking -> BookingDto.builderFromEntity(booking).build());
    }
    public Flux<BookingDto> getBookingsByUser(String userId) {
        return bookingRepository.findBookingsByUserId(userId)
                .map(booking -> BookingDto.builderFromEntity(booking).build());
    }

    public Flux<BookingDto> getBookingsBySpot(String spotId) {
        return bookingRepository.findBookingsBySpotId(spotId)
                .map(booking -> BookingDto.builderFromEntity(booking).build());
    }

    public Mono<BookingDto> saveBooking(BookingDto bookingDto) {
        return bookingRepository.save(BookingDto.mapToEntity(bookingDto))
                .map(booking -> BookingDto.builderFromEntity(booking).build());
    }
}
