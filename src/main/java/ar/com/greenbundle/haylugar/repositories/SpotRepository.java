package ar.com.greenbundle.haylugar.repositories;

import ar.com.greenbundle.haylugar.entities.SpotItem;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpotRepository extends ReactiveMongoRepository<SpotItem, String> {
    @Query("{landLordUserId:'?0'}")
    Mono<SpotItem> findSpotByUserId(String userId);

    @Query("{landLordUserId:'?0'}")
    Flux<SpotItem> findSpotsByUserId(String userId);
}
