package ar.com.greenbundle.haylugar.repositories;

import ar.com.greenbundle.haylugar.entities.UserPaymentCardEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import static ar.com.greenbundle.haylugar.repositories.queries.Queries.UserPaymentCards.SELECT_PAYMENT_CARDS_BY_PAYMENT_PROFILE_ID;

@Repository
public interface UserPaymentCardRepository extends ReactiveCrudRepository<UserPaymentCardEntity, String> {
    @Query(SELECT_PAYMENT_CARDS_BY_PAYMENT_PROFILE_ID)
    Flux<UserPaymentCardEntity> findPaymentCardsByPaymentProfile(String paymentProfileId);
}
