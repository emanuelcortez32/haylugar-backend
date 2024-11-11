package ar.com.greenbundle.haylugar.repositories;

import ar.com.greenbundle.haylugar.entities.SpotEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import static ar.com.greenbundle.haylugar.repositories.queries.Queries.Spots.SELECT_SPOTS_BY_COORDINATES_AND_RADIO_IN_METERS;
import static ar.com.greenbundle.haylugar.repositories.queries.Queries.Spots.SELECT_SPOTS_BY_LANDLORD_USER_ID;
@Repository
public interface SpotRepository extends ReactiveCrudRepository<SpotEntity, String> {
    @Query(SELECT_SPOTS_BY_LANDLORD_USER_ID)
    Flux<SpotEntity> findSpotsByLandLordUserId(String landLordUserId);

    @Query(SELECT_SPOTS_BY_COORDINATES_AND_RADIO_IN_METERS)
    Flux<SpotEntity> findSpotsByCoordinatesAndRadioInMeters(double longitude, double latitude, int radio);
}
