package ar.com.greenbundle.haylugar.repositories;

import ar.com.greenbundle.haylugar.entities.UserItem;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<UserItem, String> {
    @Query("{email:'?0'}")
    Mono<UserItem> findUserByEmail(String name);
}
