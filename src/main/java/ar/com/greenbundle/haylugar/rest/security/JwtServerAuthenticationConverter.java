package ar.com.greenbundle.haylugar.rest.security;

import ar.com.greenbundle.haylugar.pojo.constants.UserRole;
import ar.com.greenbundle.haylugar.util.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {
    private static final String BEARER = "Bearer ";

    @Autowired
    private JwtUtil jwtUtil;
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(header -> header.startsWith(BEARER))
                .map(header -> header.substring(BEARER.length()))
                .map(authToken -> new JwtToken(authToken, createUserDetails(authToken)));
    }

    private UserDetails createUserDetails(String token) {
        String username = jwtUtil.extractSubject(token);

        boolean isNonLocked = true;
        boolean isNonExpired = true;
        boolean isEnabled = true;

        return User.builder()
                .username(username)
                .authorities(createAuthorities(token))
                .password("")
                .accountLocked(!isNonLocked)
                .accountExpired(!isNonExpired)
                .disabled(!isEnabled)
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<SimpleGrantedAuthority> createAuthorities(String token) {
        Stream<UserRole> roles = jwtUtil.extractAllClaims(token)
                .get("roles", List.class)
                .stream()
                .map(role -> UserRole.valueOf((String) role));

        return roles
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
