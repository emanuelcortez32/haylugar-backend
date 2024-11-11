package ar.com.greenbundle.haylugar.repositories;

import ar.com.greenbundle.haylugar.entities.UserVehicleEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import static ar.com.greenbundle.haylugar.repositories.queries.Queries.Vehicles.SELECT_VEHICLES_BY_USER_ID;

@Repository
public interface UserVehicleRepository extends ReactiveCrudRepository<UserVehicleEntity, String> {
    @Query(SELECT_VEHICLES_BY_USER_ID)
    Flux<UserVehicleEntity> findVehiclesByUserId(String userId);
}
