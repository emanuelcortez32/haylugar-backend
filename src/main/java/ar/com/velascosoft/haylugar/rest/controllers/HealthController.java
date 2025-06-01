package ar.com.velascosoft.haylugar.rest.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

import static ar.com.velascosoft.haylugar.rest.endpoints.ControllerEndpoints.GlobalEndpoints.HEALTHCHECK;

@RestController
public class HealthController {
    @GetMapping(value = HEALTHCHECK, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Map<String, Object>>> healthcheck() {
        return Mono.just(ResponseEntity.ok(Map.of("status","up")));
    }
}
