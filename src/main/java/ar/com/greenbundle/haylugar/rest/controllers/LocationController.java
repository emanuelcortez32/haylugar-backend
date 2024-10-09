package ar.com.greenbundle.haylugar.rest.controllers;

import ar.com.greenbundle.haylugar.rest.responses.GetLocationResponse;
import ar.com.greenbundle.haylugar.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static ar.com.greenbundle.haylugar.rest.endpoints.ControllerEndpoints.LocationEndpoints.GET_LOCATION;

@RestController
public class LocationController {
    @Autowired
    private LocationService locationService;

    @GetMapping(value = GET_LOCATION, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<GetLocationResponse>> getLocation(@RequestParam String longitude,
                                                                 @RequestParam String latitude) {

        return locationService.getAddressFromCoordinate(Double.parseDouble(longitude), Double.parseDouble(latitude))
                .flatMap(address -> {
                    GetLocationResponse locationResponse = GetLocationResponse.builder()
                            .success(true)
                            .message("OK")
                            .address(address)
                            .build();

                    return Mono.just(ResponseEntity.ok(locationResponse));
                });
    }
}
