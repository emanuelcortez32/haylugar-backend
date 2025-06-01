package ar.com.velascosoft.haylugar.service;

import ar.com.velascosoft.haylugar.dao.SpotDao;
import ar.com.velascosoft.haylugar.dto.SpotDto;
import ar.com.velascosoft.haylugar.dto.UserDto;
import ar.com.velascosoft.haylugar.exceptions.CreateBookingException;
import ar.com.velascosoft.haylugar.exceptions.CreateSpotException;
import ar.com.velascosoft.haylugar.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.lang.reflect.InvocationTargetException;

import static ar.com.velascosoft.haylugar.pojo.constants.SpotState.AVAILABLE;
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

        return locationService.getAssignedZoneFromCoordinates(spotDto.getLocation().getX(), spotDto.getLocation().getY())
                .switchIfEmpty(Mono.error(new CreateBookingException("Spot is not in allowed zone")))
                .flatMap(zone -> {
                    spotDto.setZone(zone.getDescription());
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
                });

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
