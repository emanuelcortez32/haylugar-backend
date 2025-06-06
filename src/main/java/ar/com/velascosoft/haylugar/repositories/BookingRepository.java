package ar.com.velascosoft.haylugar.repositories;

import ar.com.velascosoft.haylugar.entities.BookingEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import static ar.com.velascosoft.haylugar.repositories.queries.Queries.Bookings.SELECT_BOOKINGS_BY_SPOT_ID;
import static ar.com.velascosoft.haylugar.repositories.queries.Queries.Bookings.SELECT_BOOKINGS_BY_USER_ID_CLIENT;

@Repository
public interface BookingRepository extends ReactiveCrudRepository<BookingEntity, String> {
    @Query(SELECT_BOOKINGS_BY_USER_ID_CLIENT)
    Flux<BookingEntity> findBookingsByUserId(String userId);
    @Query(SELECT_BOOKINGS_BY_SPOT_ID)
    Flux<BookingEntity> findBookingsBySpotId(String spotId);
}
