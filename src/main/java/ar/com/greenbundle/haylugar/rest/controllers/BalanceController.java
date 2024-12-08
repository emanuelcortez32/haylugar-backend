package ar.com.greenbundle.haylugar.rest.controllers;

import ar.com.greenbundle.haylugar.rest.requests.AddMinutesRequest;
import ar.com.greenbundle.haylugar.rest.responses.AddMinutesResponse;
import ar.com.greenbundle.haylugar.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class BalanceController {
    @Autowired
    private BalanceService balanceService;

    @PostMapping(value = "/balance/minutes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<AddMinutesResponse>> addMinutesToBalance(@AuthenticationPrincipal UserDetails principal,
                                                                        @RequestBody AddMinutesRequest request) {
        request.validate();

        return balanceService.intentAddMinutesToUser(principal.getUsername(), request.getMinutes())
                .map(payment -> AddMinutesResponse.builder()
                        .message("OK")
                        .success(true)
                        .payment(payment)
                        .build())
                .map(ResponseEntity::ok);
    }
}
