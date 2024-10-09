package ar.com.greenbundle.haylugar.rest.controllers;

import ar.com.greenbundle.haylugar.dto.CreateSpotData;
import ar.com.greenbundle.haylugar.dto.Location;
import ar.com.greenbundle.haylugar.rest.requests.CreateSpotRequest;
import ar.com.greenbundle.haylugar.rest.responses.CreateSpotResponse;
import ar.com.greenbundle.haylugar.rest.responses.GetUserSpotsResponse;
import ar.com.greenbundle.haylugar.rest.responses.SpotResponse;
import ar.com.greenbundle.haylugar.service.SpotService;
import ar.com.greenbundle.haylugar.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static ar.com.greenbundle.haylugar.rest.endpoints.ControllerEndpoints.SpotEndpoints.CREATE_SPOT;
import static ar.com.greenbundle.haylugar.rest.endpoints.ControllerEndpoints.SpotEndpoints.GET_SPOT;

@RestController
public class SpotController {
    @Autowired
    private SpotService spotService;
    @GetMapping(value = GET_SPOT, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<GetUserSpotsResponse>> getSpots(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authToken) {
        return spotService.getSpotsByUserEmail(JwtUtil.extractSubjectFromAuthHeader(authToken))
                .collectList()
                .map(spots -> ResponseEntity.ok(GetUserSpotsResponse.builder()
                                .success(true)
                                .message("OK")
                                .spots(spots.stream().map(spot -> SpotResponse.builder()
                                        .id(spot.getId())
                                        .type(spot.getType())
                                        .spotState(spot.getSpotState())
                                        .photos(spot.getPhotos())
                                        .pricePerMinute(spot.getPricePerMinute())
                                        .description(spot.getDescription())
                                        .location(Location.builder()
                                                .latitude(spot.getLocation().getX())
                                                .longitude(spot.getLocation().getY())
                                                .type(spot.getLocation().getType())
                                                .build())
                                        .address(spot.getAddress())
                                        .build()).toList())
                        .build()));
    }

    @PostMapping(value = CREATE_SPOT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CreateSpotResponse>> createSpot(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authToken,
                                                               @RequestBody CreateSpotRequest request) {

        request.validate();

        CreateSpotData spotData = CreateSpotData.builder()
                .type(request.getType())
                .capacity(request.getCapacity())
                .location(request.getLocation())
                .pricePerMinute(request.getPricePerMinute())
                .description(request.getDescription())
                .build();

        return spotService.createSpotAssociatedToUser(JwtUtil.extractSubjectFromAuthHeader(authToken), spotData)
                .map(spot -> new ResponseEntity<>(CreateSpotResponse.builder()
                        .id(spot.getId())
                        .message("OK")
                        .success(true)
                        .build(), HttpStatus.CREATED));
    }
}
