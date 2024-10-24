package ar.com.greenbundle.haylugar.repositories;

import ar.com.greenbundle.haylugar.entities.PaymentEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends ReactiveCrudRepository<PaymentEntity, String> {
}
