package ar.com.greenbundle.haylugar.rest.filters;

import ar.com.greenbundle.haylugar.util.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtRequestFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        if(path.contains("auth"))
            return chain.filter(exchange);

        final String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if(StringUtils.isNullOrEmpty(authorizationHeader))
            return Mono.error(new AuthenticationCredentialsNotFoundException("Auth token not found"));

        if(!authorizationHeader.startsWith("Bearer "))
            return Mono.error(new BadCredentialsException("Auth token is not valid"));

        String jwt = authorizationHeader.substring(7);

        exchange.getAttributes().put("token", jwt);

        return chain.filter(exchange);
    }
}
