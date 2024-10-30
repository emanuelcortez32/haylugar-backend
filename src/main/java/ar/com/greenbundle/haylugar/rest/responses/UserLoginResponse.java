package ar.com.greenbundle.haylugar.rest.responses;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserLoginResponse extends ApiResponse {
    @Builder
    public UserLoginResponse(boolean success,
                             String message,
                             String token) {
        super(success, message);
        this.data = Map.of("token", token);
    }
}
