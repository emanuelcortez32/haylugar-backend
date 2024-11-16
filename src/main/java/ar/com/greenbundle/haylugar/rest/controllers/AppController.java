package ar.com.greenbundle.haylugar.rest.controllers;

import ar.com.greenbundle.haylugar.entities.app.ZoneEntity;
import ar.com.greenbundle.haylugar.pojo.Location;
import ar.com.greenbundle.haylugar.rest.responses.ApiResponse;
import ar.com.greenbundle.haylugar.rest.responses.ZoneResponse;
import ar.com.greenbundle.haylugar.rest.responses.ZonesResponse;
import ar.com.greenbundle.haylugar.service.app.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@RestController
@RequestMapping("/api")
public class AppController {
    @Autowired
    private ZoneService zoneService;

    @GetMapping(value = "/app/config/zones", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ApiResponse>> getAllZones() {
        return zoneService.getAllZones()
                .collectList()
                .map(zones -> ResponseEntity.ok(ZonesResponse.builder()
                                .success(true)
                                .message("OK")
                                .zones(zones.stream().map(mapZoneToResponse).toList())
                        .build()));
    }

    private final Function<ZoneEntity, ZoneResponse> mapZoneToResponse = zone -> {
        List<Location> coordinates = zone.getCoordinates().stream()
                .map(coordinate -> Location.builder()
                        .longitude(coordinate.getX())
                        .latitude(coordinate.getY())
                        .build())
                .toList();


        return ZoneResponse.builder()
                .id(zone.getId())
                .name(zone.getName())
                .description(zone.getDescription())
                .coordinates(coordinates)
                .build();
    };
}
