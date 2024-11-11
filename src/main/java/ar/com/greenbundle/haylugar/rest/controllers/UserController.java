package ar.com.greenbundle.haylugar.rest.controllers;

import ar.com.greenbundle.haylugar.rest.responses.ApiResponse;
import ar.com.greenbundle.haylugar.rest.responses.GetUserResponse;
import ar.com.greenbundle.haylugar.rest.responses.UserProfileResponse;
import ar.com.greenbundle.haylugar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static ar.com.greenbundle.haylugar.rest.endpoints.ControllerEndpoints.MeEndpoints.UserEndpoints.GET_USER;


@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping(value = GET_USER, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ApiResponse>> getUser(@AuthenticationPrincipal UserDetails principal) {
        return userService.findUser(principal.getUsername())
                .map(user -> ResponseEntity.ok(GetUserResponse.builder()
                        .enabled(user.getState().enabled)
                        .profile(UserProfileResponse.builder()
                                .name(user.getProfile().getName())
                                .surname(user.getProfile().getLastName())
                                .dni(user.getProfile().getDni())
                                .birthDate(user.getProfile().getBirthDate())
                                .gender(user.getProfile().getGender())
                                .nationality(user.getProfile().getNationality())
                                .build())
                        .success(true)
                        .message(HttpStatus.OK.getReasonPhrase())
                        .build()));
    }
}
