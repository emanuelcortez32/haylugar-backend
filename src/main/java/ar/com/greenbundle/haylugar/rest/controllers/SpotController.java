package ar.com.greenbundle.haylugar.rest.controllers;

import ar.com.greenbundle.haylugar.dto.SpotDto;
import ar.com.greenbundle.haylugar.pojo.Location;
import ar.com.greenbundle.haylugar.rest.requests.CreateSpotRequest;
import ar.com.greenbundle.haylugar.rest.responses.ApiResponse;
import ar.com.greenbundle.haylugar.rest.responses.CreateSpotResponse;
import ar.com.greenbundle.haylugar.rest.responses.GetUserSpotResponse;
import ar.com.greenbundle.haylugar.rest.responses.GetUserSpotsResponse;
import ar.com.greenbundle.haylugar.rest.responses.SpotResponse;
import ar.com.greenbundle.haylugar.service.SpotService;
import ar.com.greenbundle.haylugar.util.StringUtils;
import io.r2dbc.postgresql.codec.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static ar.com.greenbundle.haylugar.rest.endpoints.ControllerEndpoints.MeEndpoints.SpotEndpoints.GET_SPOTS;
import static ar.com.greenbundle.haylugar.rest.endpoints.ControllerEndpoints.MeEndpoints.SpotEndpoints.POST_SPOT;


@RestController
@RequestMapping("/api")
public class SpotController {
    @Autowired
    private SpotService spotService;
    @GetMapping(value = GET_SPOTS, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ApiResponse>> getSpots(@AuthenticationPrincipal UserDetails principal,
                                                      @RequestParam(required = false) String spotId) {

        if(!StringUtils.isNullOrEmpty(spotId))
            return spotService.findSpotByUser(principal.getUsername(), spotId)
                    .map(mapSpotToResponse)
                    .map(spotResponse -> ResponseEntity.ok(GetUserSpotResponse.builder()
                            .message("OK")
                            .success(true)
                            .spotResponse(spotResponse)
                            .build()));

        return spotService.findSpotsByUser(principal.getUsername())
                .collectList()
                .map(spots -> ResponseEntity.ok(GetUserSpotsResponse.builder()
                                .success(true)
                                .message("OK")
                                .spots(spots.stream().map(mapSpotToResponse).toList())
                        .build()));
    }

    @PostMapping(value = POST_SPOT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ApiResponse>> createSpot(@AuthenticationPrincipal UserDetails principal,
                                                               @RequestBody CreateSpotRequest request) {

        request.validate();

        SpotDto spot = SpotDto.builder()
                .type(request.getType())
                .capacity(request.getCapacity())
                .location(Point.of(request.getLocation().getLongitude(), request.getLocation().getLatitude()))
                .pricePerMinute(request.getPricePerMinute())
                .description(request.getDescription())
                .build();


        return spotService.createSpotForUser(principal.getUsername(), spot)
                .map(spotId -> new ResponseEntity<>(CreateSpotResponse.builder()
                        .id(spotId)
                        .message("OK")
                        .success(true)
                        .build(), HttpStatus.CREATED));
    }

    private final Function<SpotDto, SpotResponse> mapSpotToResponse = spot -> SpotResponse.builder()
            .id(spot.getId())
            .type(spot.getType())
            .spotState(spot.getState())
            .photos(spot.getPhotos())
            .pricePerMinute(spot.getPricePerMinute())
            .description(spot.getDescription())
            .location(Location.builder()
                    .longitude(spot.getLocation().getX())
                    .latitude(spot.getLocation().getY())
                    .type("Point")
                    .build())
            .address(spot.getAddress())
            .build();
}
