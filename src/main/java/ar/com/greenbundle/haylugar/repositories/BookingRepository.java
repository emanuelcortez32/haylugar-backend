package ar.com.greenbundle.haylugar.repositories;

import ar.com.greenbundle.haylugar.entities.BookingEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import static ar.com.greenbundle.haylugar.repositories.queries.Queries.Bookings.SELECT_BOOKINGS_BY_SPOT_ID;
import static ar.com.greenbundle.haylugar.repositories.queries.Queries.Bookings.SELECT_BOOKINGS_BY_USER_ID;

@Repository
public interface BookingRepository extends ReactiveCrudRepository<BookingEntity, String> {
    @Query(SELECT_BOOKINGS_BY_USER_ID)
    Flux<BookingEntity> findBookingsByUserId(String userId);
    @Query(SELECT_BOOKINGS_BY_SPOT_ID)
    Flux<BookingEntity> findBookingsBySpotId(String spotId);
}
