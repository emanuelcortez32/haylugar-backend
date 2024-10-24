package ar.com.greenbundle.haylugar.repositories;

import ar.com.greenbundle.haylugar.entities.UserPaymentProfileEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import static ar.com.greenbundle.haylugar.repositories.queries.Queries.UserPaymentProfiles.SELECT_PAYMENT_PROFILE_BY_USER_ID;
@Repository
public interface UserPaymentProfileRepository extends ReactiveCrudRepository<UserPaymentProfileEntity, String> {
    @Query(SELECT_PAYMENT_PROFILE_BY_USER_ID)
    Mono<UserPaymentProfileEntity> findByUserId(String userId);
}
