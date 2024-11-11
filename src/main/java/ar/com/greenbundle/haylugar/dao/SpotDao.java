package ar.com.greenbundle.haylugar.dao;

import ar.com.greenbundle.haylugar.dto.SpotDto;
import ar.com.greenbundle.haylugar.entities.SpotEntity;
import ar.com.greenbundle.haylugar.repositories.SpotRepository;
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
                .map(spot -> SpotDto.builderFromEntity(spot).build());
    }

    public Flux<SpotDto> getSpotsByUser(String userId) {
        return spotRepository.findSpotsByLandLordUserId(userId)
                .filter(spot -> !spot.isDeleted())
                .map(spot -> SpotDto.builderFromEntity(spot).build());
    }

    public Flux<SpotDto> findSpotsByLocationAndRadio(double longitude, double latitude, int radio) {
        return spotRepository.findSpotsByCoordinatesAndRadioInMeters(longitude, latitude, radio)
                .filter(spot -> !spot.isDeleted())
                .map(spot -> SpotDto.builderFromEntity(spot).build());
    }

    public Mono<SpotDto> saveSpot(SpotDto spotDto) {
        return spotRepository.save(SpotDto.mapToEntity(spotDto))
                .map(spot -> SpotDto.builderFromEntity(spot).build());
    }

    public Mono<Void> deleteSpot(SpotDto spotDto) {
        SpotEntity entity = SpotDto.mapToEntity(spotDto);
        entity.setDeleted(true);

        return spotRepository.save(entity)
                .then(Mono.empty());
    }
}
