package ar.com.greenbundle.haylugar.service;

import ar.com.greenbundle.haylugar.dao.SpotDao;
import ar.com.greenbundle.haylugar.dto.SpotDto;
import ar.com.greenbundle.haylugar.dto.UserDto;
import ar.com.greenbundle.haylugar.exceptions.CreateSpotException;
import ar.com.greenbundle.haylugar.exceptions.ResourceNotFoundException;
import ar.com.greenbundle.haylugar.pojo.constants.AllowedZone;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import static ar.com.greenbundle.haylugar.pojo.constants.SpotState.AVAILABLE;
@Slf4j
@Service
public class SpotService {
    @Autowired
    private BeanUtilsBean utilsBean;
    @Autowired
    private SpotDao spotDao;
    @Autowired
    private LocationService locationService;

    public Flux<SpotDto> findSpotsByUser(String userId) {
        return spotDao.getSpotsByUser(userId);
    }

    public Mono<SpotDto> findSpotByUser(String userId, String spotId) {
        return spotDao.getSpot(spotId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Spot not found")))
                .flatMap(spot -> {
                    if(!spot.getLandLord().getId().equals(userId))
                        return Mono.error(new ResourceNotFoundException("Spot not found"));

                    return Mono.just(spot);
                });
    }

    public Mono<SpotDto> findSpot(String spotId) {
        return spotDao.getSpot(spotId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Spot not found")));
    }

    public Flux<SpotDto> findSpotByLocationAndRadio(double longitude, double latitude, int radio) {
        return spotDao.findSpotsByLocationAndRadio(longitude, latitude, radio);
    }

    public Mono<String> createSpotForUser(String userId, SpotDto spotDto) {
        spotDto.setLandLord(UserDto.builder().id(userId).build());
        spotDto.setState(AVAILABLE);


        return saveOrUpdateSpot(spotDto);
    }

    public Mono<String> updateSpot(SpotDto spotDto) {

        return spotDao.getSpot(spotDto.getId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Spot Not Found")))
                .flatMap(spot -> {
                    if(!spot.getLandLord().getId().equals(spotDto.getLandLord().getId()))
                        return Mono.error(new ResourceNotFoundException("Spot Not Found"));

                    try {
                        utilsBean.copyProperties(spotDto, spot);

                        return saveOrUpdateSpot(spotDto);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        return Mono.error(new CreateSpotException(e.getMessage()));
                    }
                });
    }

    private Mono<String> saveOrUpdateSpot(SpotDto spotDto) {
        float MINIMUM_PRICE_PER_MINUTE = 15f;

        if(spotDto.getPricePerMinute() < MINIMUM_PRICE_PER_MINUTE)
            return Mono.error(new CreateSpotException("Spot price per minute must be equal or greater than minimum price"));

        Optional<AllowedZone> zone = locationService.getAssignedZoneFromCoordinates(spotDto.getLocation().getX(),
                spotDto.getLocation().getY());

        if(zone.isEmpty()) {
            return Mono.error(new CreateSpotException("Spot is not in allowed zone"));
        } else {
            spotDto.setZone(zone.get().desc);
        }

        return spotDao.saveSpot(spotDto)
                .doOnNext(savedSpot -> locationService.getAddressFromCoordinate(savedSpot.getLocation().getX(), savedSpot.getLocation().getY())
                        .subscribeOn(Schedulers.boundedElastic())
                        .doOnError(throwable -> log.warn("The address for the spot[{}] could not be determined : [{}]",
                                savedSpot.getId(), throwable.getMessage()))
                        .subscribe(address -> {
                            savedSpot.setAddress(address);
                            spotDao.saveSpot(savedSpot)
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .subscribe();
                        }, throwable -> {}))
                .map(SpotDto::getId);
    }

    public Mono<Void> deleteSpot(SpotDto spotDto) {
        return spotDao.getSpot(spotDto.getId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Spot Not Found")))
                .flatMap(spot -> {
                    if(!spot.getLandLord().getId().equals(spotDto.getLandLord().getId()))
                        throw new ResourceNotFoundException("Spot Not Found");

                    try {
                        utilsBean.copyProperties(spotDto, spot);
                        return spotDao.deleteSpot(spotDto);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new CreateSpotException(e.getMessage());
                    }
                });
    }
}
