package ar.com.greenbundle.haylugar.rest.responses;

import ar.com.greenbundle.haylugar.dto.UserProfile;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetUserResponse extends GenericResponse {
    @JsonProperty("data")
    private UserProfile profile;
    private boolean enabled;

    @Builder
    public GetUserResponse(boolean success,
                           String message,
                           UserProfile profile,
                           boolean enabled) {
        super(success, message);
        this.profile = profile;
        this.enabled = enabled;
    }
}
