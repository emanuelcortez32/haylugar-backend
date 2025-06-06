package ar.com.velascosoft.haylugar.exceptions;

import ar.com.velascosoft.haylugar.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.security.sasl.AuthenticationException;
import java.util.Map;

@Component
@Slf4j
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {
    @Autowired
    private ObjectMapper objectMapper;

    private static final Map<String, HttpStatus> HTTP_STATUS_EXCEPTION_MAP = Map.ofEntries(
            Map.entry(BadCredentialsException.class.getCanonicalName(), HttpStatus.UNAUTHORIZED),
            Map.entry(AuthenticationCredentialsNotFoundException.class.getCanonicalName(), HttpStatus.UNAUTHORIZED),
            Map.entry(AuthenticationException.class.getCanonicalName(), HttpStatus.UNAUTHORIZED),
            Map.entry(LoginPasswordException.class.getCanonicalName(), HttpStatus.UNAUTHORIZED),
            Map.entry(ResourceNotFoundException.class.getCanonicalName(), HttpStatus.NOT_FOUND),
            Map.entry(CreateUserException.class.getCanonicalName(), HttpStatus.CONFLICT),
            Map.entry(CreateSpotException.class.getCanonicalName(), HttpStatus.CONFLICT),
            Map.entry(javax.naming.AuthenticationException.class.getCanonicalName(), HttpStatus.UNAUTHORIZED),
            Map.entry(BookingFinishedException.class.getCanonicalName(), HttpStatus.CONFLICT),
            Map.entry(BadRequestBodyException.class.getCanonicalName(), HttpStatus.BAD_REQUEST),
            Map.entry(PaymentProcessException.class.getCanonicalName(), HttpStatus.PAYMENT_REQUIRED),
            Map.entry(CreateBookingException.class.getCanonicalName(), HttpStatus.CONFLICT),
            Map.entry(SpotCapacityExceeded.class.getCanonicalName(), HttpStatus.CONFLICT),
            Map.entry(DataIntegrityViolationException.class.getCanonicalName(), HttpStatus.BAD_REQUEST),
            Map.entry(ExpiredJwtException.class.getCanonicalName(), HttpStatus.UNAUTHORIZED),
            Map.entry(SignatureException.class.getCanonicalName(), HttpStatus.UNAUTHORIZED),
            Map.entry(FeatureException.class.getCanonicalName(), HttpStatus.CONFLICT),
            Map.entry("io.r2dbc.postgresql.ExceptionFactory.PostgresqlDataIntegrityViolationException", HttpStatus.BAD_REQUEST)
    );

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        String exceptionCauseName = ex.getCause() != null ? ex.getCause().getClass().getCanonicalName() : ex.getClass().getCanonicalName();
        HttpStatus status = HTTP_STATUS_EXCEPTION_MAP.getOrDefault(exceptionCauseName, HttpStatus.INTERNAL_SERVER_ERROR);
        String reason = status.getReasonPhrase();
        String msgError = !StringUtils.isNullOrEmpty(ex.getMessage()) ? ex.getMessage() : "Error";

        log.debug("GlobalExceptionHandler : catch exception [{}] with message [{}], forward HTTP status code [{}]",
                exceptionCauseName, ex.getMessage(), status);

        log.debug("GlobalExceptionHandler : Trace", ex);

        if(status.is5xxServerError())
            log.error("GlobalExceptionHandler : Error !!!", ex);

        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        exchange.getResponse().setStatusCode(status);

        return exchange.getResponse().writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
            try {
                return bufferFactory.wrap(objectMapper.writeValueAsBytes(Map.of(
                        "reason", reason,
                        "success",false,
                        "message", msgError)));
            } catch (Exception e) {
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }
}
