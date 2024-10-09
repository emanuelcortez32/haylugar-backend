package ar.com.greenbundle.haylugar.rest.controllers;

import ar.com.greenbundle.haylugar.rest.responses.GetUserResponse;
import ar.com.greenbundle.haylugar.service.UserService;
import ar.com.greenbundle.haylugar.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static ar.com.greenbundle.haylugar.rest.endpoints.ControllerEndpoints.UserEndpoints.GET_USER;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping(value = GET_USER, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<GetUserResponse>> getUser(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authToken) {
        return userService.getUserByEmail(JwtUtil.extractSubjectFromAuthHeader(authToken))
                .map(user -> ResponseEntity.ok(GetUserResponse.builder()
                        .enabled(user.getState().enabled)
                        .profile(user.getProfile())
                        .success(true)
                        .message(HttpStatus.OK.getReasonPhrase())
                        .build()));
    }
}
