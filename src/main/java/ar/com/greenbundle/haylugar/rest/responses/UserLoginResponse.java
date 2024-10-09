package ar.com.greenbundle.haylugar.rest.responses;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserLoginResponse extends GenericResponse {
    private String token;

    @Builder
    public UserLoginResponse(boolean success,
                             String message,
                             String token) {
        super(success, message);
        this.token = token;
    }
}
