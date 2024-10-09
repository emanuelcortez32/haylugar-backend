package ar.com.greenbundle.haylugar.service;

import ar.com.greenbundle.haylugar.dto.Address;
import ar.com.greenbundle.haylugar.dto.CreateSpotData;
import ar.com.greenbundle.haylugar.dto.Location;
import ar.com.greenbundle.haylugar.dto.UpdateSpotData;
import ar.com.greenbundle.haylugar.entities.SpotItem;
import ar.com.greenbundle.haylugar.entities.UserItem;
import ar.com.greenbundle.haylugar.exceptions.CreateSpotException;
import ar.com.greenbundle.haylugar.exceptions.ResourceNotFoundException;
import ar.com.greenbundle.haylugar.repositories.SpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static ar.com.greenbundle.haylugar.dto.constants.SpotState.FREE;

@Service
public class SpotService {
    @Autowired
    private SpotRepository spotRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private LocationService locationService;

    public Flux<SpotItem> getSpotsByUserEmail(String userEmail) {

        return userService.getUserByEmail(userEmail)
                .flatMapMany(user -> spotRepository.findSpotsByUserId(user.getId()))
                .switchIfEmpty(Flux.empty());
    }

    public Mono<SpotItem> getSpotById(String spotId) {
        return spotRepository.findById(spotId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Spot not found")));
    }

    public Mono<SpotItem> createSpotAssociatedToUser(String userEmail, CreateSpotData spotData) {
        int MINIMUM_PRICE_PER_MINUTE = 15;

        if(spotData.getPricePerMinute() <= MINIMUM_PRICE_PER_MINUTE)
            return Mono.error(new CreateSpotException("Spot price per minute must be equal or greater than minimum price"));

        if(locationService.checkIfPointInsideAllowedArea(spotData.getLocation().getLongitude(),
                spotData.getLocation().getLatitude())) {
            return Mono.error(new CreateSpotException("Spot is not in allowed area"));
        }

        SpotItem spot = SpotItem.builder()
                .type(spotData.getType())
                .capacity(spotData.getCapacity())
                .pricePerMinute(spotData.getPricePerMinute())
                .photos(spotData.getPhotos())
                .description(spotData.getDescription())
                .location(new GeoJsonPoint(spotData.getLocation().getLongitude(), spotData.getLocation().getLatitude()))
                .spotState(FREE)
                .build();

        Mono<UserItem> user = userService.getUserByEmail(userEmail);
        Mono<Address> address = locationService.getAddressFromCoordinate(spot.getLocation().getX(), spot.getLocation().getY());

        return user
                .doOnNext(u -> spot.setLandLordUserId(u.getId()))
                .flatMap(__ -> address)
                .doOnNext(spot::setAddress)
                .then(spotRepository.save(spot));
    }

    public Mono<SpotItem> updateSpotAssociatedToUser(String userId, String spotId, UpdateSpotData spotData) {
        return userService.getUserById(userId)
                .flatMap(user -> spotRepository.findById(spotId)
                        .flatMap(spot -> {
                            if(!spot.getLandLordUserId().equals(user.getId()))
                                return Mono.error(new RuntimeException("Unable to edit spot"));

                            if(spotData.getSpotState() != null)
                                spot.setSpotState(spotData.getSpotState());

                            if(spotData.getDescription() != null)
                                spot.setDescription(spotData.getDescription());

                            if(spotData.getPhotos() != null)
                                spot.setPhotos(spotData.getPhotos());

                            return spotRepository.save(spot);
                        }));
    }
}
