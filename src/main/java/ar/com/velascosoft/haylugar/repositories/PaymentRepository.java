package ar.com.velascosoft.haylugar.repositories;

import ar.com.velascosoft.haylugar.entities.PaymentEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import static ar.com.velascosoft.haylugar.repositories.queries.Queries.Payments.SELECT_PAYMENT_BY_EXTERNAL_REFERENCE_ID;

@Repository
public interface PaymentRepository extends ReactiveCrudRepository<PaymentEntity, String> {
    @Query(SELECT_PAYMENT_BY_EXTERNAL_REFERENCE_ID)
    Mono<PaymentEntity> findByExternalReferenceId(String externalReferenceId);
}
