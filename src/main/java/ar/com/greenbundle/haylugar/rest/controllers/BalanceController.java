package ar.com.greenbundle.haylugar.rest.controllers;

import ar.com.greenbundle.haylugar.rest.requests.AddMinutesRequest;
import ar.com.greenbundle.haylugar.rest.responses.AddMinutesResponse;
import ar.com.greenbundle.haylugar.rest.responses.ApiResponse;
import ar.com.greenbundle.haylugar.rest.responses.BalanceResponse;
import ar.com.greenbundle.haylugar.rest.responses.GetUserBalanceResponse;
import ar.com.greenbundle.haylugar.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
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
    public Mono<ResponseEntity<ApiResponse>> addMinutesToBalance(@AuthenticationPrincipal UserDetails principal,
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

    @GetMapping(value = "/balance/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ApiResponse>> getBalance(@AuthenticationPrincipal UserDetails principal) {
        return balanceService.getUserBalance(principal.getUsername())
                .map(balance -> BalanceResponse.builder()
                        .id(balance.getId())
                        .totalAmount(balance.getTotalAmount())
                        .availableMinutes(balance.getAvailableMinutes())
                        .amountAvailableToWithdraw(balance.getAmountAvailableToWithdraw())
                        .amountPendingToWithdraw(balance.getAmountPendingToWithdraw())
                        .build())
                .map(response -> GetUserBalanceResponse.builder()
                        .success(true)
                        .message("OK")
                        .balanceResponse(response)
                        .build())
                .map(ResponseEntity::ok);
    }
}
