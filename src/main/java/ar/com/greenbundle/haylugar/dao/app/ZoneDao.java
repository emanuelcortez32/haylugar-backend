package ar.com.greenbundle.haylugar.dao.app;

import ar.com.greenbundle.haylugar.entities.app.ZoneEntity;
import ar.com.greenbundle.haylugar.repositories.app.ZoneRepository;
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
