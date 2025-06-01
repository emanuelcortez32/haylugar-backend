package ar.com.velascosoft.haylugar.service.app;

import ar.com.velascosoft.haylugar.dao.app.ZoneDao;
import ar.com.velascosoft.haylugar.entities.app.ZoneEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ZoneService {
    @Autowired
    private ZoneDao zoneDao;

    @Cacheable(value = "zones")
    public Flux<ZoneEntity> getAllZones() {
        return zoneDao.getAppZones()
                .filter(ZoneEntity::isEnabled);
    }
}
