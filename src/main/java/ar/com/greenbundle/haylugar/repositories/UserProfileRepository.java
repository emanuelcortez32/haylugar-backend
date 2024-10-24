package ar.com.greenbundle.haylugar.repositories;

import ar.com.greenbundle.haylugar.entities.UserProfileEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import static ar.com.greenbundle.haylugar.repositories.queries.Queries.UserProfiles.SELECT_USER_PROFILES_BY_USER_ID;
@Repository
public interface UserProfileRepository extends ReactiveCrudRepository<UserProfileEntity, String> {
    @Query(SELECT_USER_PROFILES_BY_USER_ID)
    Mono<UserProfileEntity> findByUserId(String userId);
}
