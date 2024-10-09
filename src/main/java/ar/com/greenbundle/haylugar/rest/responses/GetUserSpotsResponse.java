package ar.com.greenbundle.haylugar.rest.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetUserSpotsResponse extends GenericResponse {
    @JsonProperty("data")
    private List<SpotResponse> spots;

    @Builder
    public GetUserSpotsResponse(boolean success,
                                String message,
                                List<SpotResponse> spots) {
        super(success, message);
        this.spots = spots;
    }
}
