package ar.com.greenbundle.haylugar.repositories;

import ar.com.greenbundle.haylugar.entities.BookingItem;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface BookingRepository extends ReactiveMongoRepository<BookingItem, String> {
    @Query("{spotOwnerId:'?0'}")
    Flux<BookingItem> findBookingsBySpotOwnerId(String spotId);

    @Query("{clientUserId:'?0'}")
    Flux<BookingItem> findBookingsByClientUserId(String clientUserId);

    @Query("{spotId:'?0'}")
    Flux<BookingItem> findBookingsBySpotId(String spotId);
}
