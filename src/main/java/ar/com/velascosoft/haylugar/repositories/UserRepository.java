package ar.com.velascosoft.haylugar.repositories;

import ar.com.velascosoft.haylugar.entities.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import static ar.com.velascosoft.haylugar.repositories.queries.Queries.Users.SELECT_USERS_BY_EMAIL;
@Repository
public interface UserRepository extends ReactiveCrudRepository<UserEntity, String> {
    @Query(SELECT_USERS_BY_EMAIL)
    Mono<UserEntity> findByEmail(String email);
}
