package ar.com.greenbundle.haylugar.service;

import ar.com.greenbundle.haylugar.dto.UserDto;
import ar.com.greenbundle.haylugar.dto.UserProfileDto;
import ar.com.greenbundle.haylugar.exceptions.LoginPasswordException;
import ar.com.greenbundle.haylugar.util.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static ar.com.greenbundle.haylugar.pojo.constants.UserRole.ROLE_USER;
import static ar.com.greenbundle.haylugar.pojo.constants.UserState.PENDING;

@Service
public class AuthService {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    public Mono<String> loginUser(String email, String password) {
        return userService.findUserByEmail(email)
                .map(user -> {
                    if(!passwordEncoder.matches(password, user.getPassword()))
                        return Mono.error(new LoginPasswordException("Invalid password"));

                    Map<String, Object> claims = Map.of("roles", user.getRoles());

                    return jwtUtil.generateToken(user.getId(), claims);
                })
                .cast(String.class);
    }

    public Mono<String> signUpUser(String email, String password, UserProfileDto profile) {

        String securedPassword = passwordEncoder.encode(password);

        UserDto user = UserDto.builder()
                .email(email)
                .password(securedPassword)
                .profile(profile)
                .roles(List.of(ROLE_USER))
                .state(PENDING)
                .build();

        return userService.registerUser(user);
    }
}
