package ar.com.greenbundle.haylugar.rest.controllers;

import ar.com.greenbundle.haylugar.rest.requests.CreateUserRequest;
import ar.com.greenbundle.haylugar.rest.requests.UserLoginRequest;
import ar.com.greenbundle.haylugar.rest.responses.CreateUserResponse;
import ar.com.greenbundle.haylugar.rest.responses.UserLoginResponse;
import ar.com.greenbundle.haylugar.service.AuthService;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static ar.com.greenbundle.haylugar.rest.endpoints.ControllerEndpoints.AuthEndpoints.USER_LOGIN;
import static ar.com.greenbundle.haylugar.rest.endpoints.ControllerEndpoints.AuthEndpoints.USER_SIGNUP;

@RestController
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private Validator validator;

    @PostMapping(value = USER_LOGIN, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<UserLoginResponse>> userLogin(@RequestBody UserLoginRequest request) {

        request.validate();

        return authService.loginUser(request.getEmail(), request.getPassword())
                .map(token -> ResponseEntity.ok(UserLoginResponse.builder()
                        .success(true)
                        .message("OK")
                        .token(token)
                        .build()
                ));
    }

    @PostMapping(value = USER_SIGNUP, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CreateUserResponse>> createUser(@RequestBody CreateUserRequest request) {

        request.validate();

        return authService.signUpUser(request.getEmail(), request.getPassword(), request.getProfile())
                .map(user -> new ResponseEntity<>(CreateUserResponse.builder()
                        .id(user.getId())
                        .success(true)
                        .message("OK")
                        .build(), HttpStatus.CREATED));
    }
}
