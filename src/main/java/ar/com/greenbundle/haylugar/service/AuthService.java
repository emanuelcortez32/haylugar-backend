package ar.com.greenbundle.haylugar.service;

import ar.com.greenbundle.haylugar.dto.CreateUserData;
import ar.com.greenbundle.haylugar.dto.SecurePassword;
import ar.com.greenbundle.haylugar.dto.UserProfile;
import ar.com.greenbundle.haylugar.entities.UserItem;
import ar.com.greenbundle.haylugar.exceptions.CreateUserException;
import ar.com.greenbundle.haylugar.util.EncodeUtil;
import ar.com.greenbundle.haylugar.util.JwtUtil;
import ar.com.greenbundle.haylugar.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Service
public class AuthService {
    @Autowired
    private UserService userService;

    public Mono<String> loginUser(String email, String password) {
        return userService.getUserByEmail(email)
                .switchIfEmpty(Mono.error(new BadCredentialsException(HttpStatus.UNAUTHORIZED.getReasonPhrase())))
                .map(user -> {
                    SecurePassword securePassword = new SecurePassword();
                    securePassword.setHash(EncodeUtil.decodeBase64(user.getPasswordHash().getBytes(StandardCharsets.UTF_8)));
                    securePassword.setSalt(EncodeUtil.decodeBase64(user.getPasswordSalt().getBytes(StandardCharsets.UTF_8)));
                    PasswordUtil.verifySecurePassword(securePassword, password);

                    return JwtUtil.generateToken(email);
                });

    }

    public Mono<UserItem> signUpUser(String email, String password, UserProfile userProfile) {
        return userService.getUserByEmail(email)
                .flatMap(__ -> Mono.error(new CreateUserException("Unable to create user, already exists with email")))
                .switchIfEmpty(Mono.defer(() -> {
                    SecurePassword securePassword = PasswordUtil.createSecurePassword(password);

                    CreateUserData userData = CreateUserData.builder()
                            .email(email)
                            .password(securePassword)
                            .profile(userProfile)
                            .build();

                    return userService.createUser(userData);
                }))
                .cast(UserItem.class);
    }
}
