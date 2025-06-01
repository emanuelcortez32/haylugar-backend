package ar.com.velascosoft.haylugar.repositories.app;

import ar.com.velascosoft.haylugar.entities.app.ZoneEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneRepository extends ReactiveCrudRepository<ZoneEntity, String> {
}
