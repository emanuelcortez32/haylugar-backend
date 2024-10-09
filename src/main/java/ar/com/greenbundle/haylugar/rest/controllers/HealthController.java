package ar.com.greenbundle.haylugar.rest.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class HealthController {
    @GetMapping("/healthcheck")
    public Mono<ResponseEntity<Map<String, Object>>> healthcheck() {
        return Mono.just(ResponseEntity.ok(Map.of("status","up")));
    }
}
