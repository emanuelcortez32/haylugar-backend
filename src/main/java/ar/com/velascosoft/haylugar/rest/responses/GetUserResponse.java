package ar.com.velascosoft.haylugar.rest.responses;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetUserResponse extends ApiResponse {
    @Builder
    public GetUserResponse(boolean success,
                           String message,
                           String email,
                           UserProfileResponse profile,
                           boolean enabled) {
        super(success, message);
        this.data = Map.of(
                "enabled", enabled,
                "email", email,
                "profile", profile
        );
    }
}
