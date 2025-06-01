package ar.com.velascosoft.haylugar.repositories;

import ar.com.velascosoft.haylugar.entities.UserBalanceEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import static ar.com.velascosoft.haylugar.repositories.queries.Queries.UserBalances.SELECT_USER_BALANCE_BY_USER_ID;

@Repository
public interface UserBalanceRepository extends ReactiveCrudRepository<UserBalanceEntity, String> {
    @Query(SELECT_USER_BALANCE_BY_USER_ID)
    Mono<UserBalanceEntity> findByUserId(String userId);
}
