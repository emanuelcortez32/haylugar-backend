package ar.com.velascosoft.haylugar.dao.app;

import ar.com.velascosoft.haylugar.entities.app.ZoneEntity;
import ar.com.velascosoft.haylugar.repositories.app.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class ZoneDao {

    @Autowired
    private ZoneRepository zoneRepository;

    public Flux<ZoneEntity> getAppZones() {
        return zoneRepository.findAll();
    }
}
