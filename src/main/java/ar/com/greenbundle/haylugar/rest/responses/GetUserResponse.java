package ar.com.greenbundle.haylugar.rest.responses;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetUserResponse extends GenericResponse {
    @Builder
    public GetUserResponse(boolean success,
                           String message,
                           UserProfileResponse profile,
                           boolean enabled) {
        super(success, message);
        this.data = Map.of(
                "enabled", enabled,
                "profile", profile
        );
    }
}
