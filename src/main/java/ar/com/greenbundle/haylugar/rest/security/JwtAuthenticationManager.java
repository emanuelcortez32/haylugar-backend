package ar.com.greenbundle.haylugar.rest.security;

import ar.com.greenbundle.haylugar.util.JwtUtil;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .map(auth -> JwtUtil.extractAllClaims(auth.getCredentials().toString()))
                .log()
                .onErrorResume(e -> Mono.error(new AuthenticationException(e.getMessage())))
                .map(claims -> new UsernamePasswordAuthenticationToken(
                        claims.getSubject(),
                        null,
                        Stream.of("read", "write")
                                .map(SimpleGrantedAuthority::new).collect(Collectors.toList())));
    }
}
