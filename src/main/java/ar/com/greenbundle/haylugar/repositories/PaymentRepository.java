package ar.com.greenbundle.haylugar.repositories;

import ar.com.greenbundle.haylugar.entities.PaymentItem;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PaymentRepository extends ReactiveMongoRepository<PaymentItem, String> {
}
