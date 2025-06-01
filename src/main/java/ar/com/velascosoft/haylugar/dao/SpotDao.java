package ar.com.velascosoft.haylugar.dao;

import ar.com.velascosoft.haylugar.dto.SpotDto;
import ar.com.velascosoft.haylugar.entities.SpotEntity;
import ar.com.velascosoft.haylugar.repositories.SpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class SpotDao {
    @Autowired
    private UserDao userDao;
    @Autowired
    private SpotRepository spotRepository;

    public Mono<SpotDto> getSpot(String spotId) {
        return spotRepository.findById(spotId)
                .filter(spot -> !spot.isDeleted())
                .flatMap(spot -> userDao.getUser(spot.getLandLordUserId())
                        .map(user -> {
                            SpotDto spotDto = new SpotDto().dtoFromEntity(spot);
                            spotDto.setLandLord(user);
                            return spotDto;
                        }));
    }

    public Flux<SpotDto> getSpotsByUser(String userId) {
        return spotRepository.findSpotsByLandLordUserId(userId)
                .filter(spot -> !spot.isDeleted())
                .flatMap(spot -> userDao.getUser(spot.getLandLordUserId())
                        .map(user -> {
                            SpotDto spotDto = new SpotDto().dtoFromEntity(spot);
                            spotDto.setLandLord(user);
                            return spotDto;
                        }));
    }

    public Flux<SpotDto> findSpotsByLocationAndRadio(double longitude, double latitude, int radio) {
        return spotRepository.findSpotsByCoordinatesAndRadioInMeters(longitude, latitude, radio)
                .filter(spot -> !spot.isDeleted())
                .flatMap(spot -> userDao.getUser(spot.getLandLordUserId())
                        .map(user -> {
                            SpotDto spotDto = new SpotDto().dtoFromEntity(spot);
                            spotDto.setLandLord(user);
                            return spotDto;
                        }));
    }

    public Mono<SpotDto> saveSpot(SpotDto spotDto) {
        return spotRepository.save(new SpotDto().dtoToEntity(spotDto))
                .flatMap(savedSpot -> getSpot(savedSpot.getId()));
    }

    public Mono<Void> deleteSpot(SpotDto spotDto) {
        SpotEntity entity = new SpotDto().dtoToEntity(spotDto);
        entity.setDeleted(true);

        return spotRepository.save(entity)
                .then(Mono.empty());
    }
}
