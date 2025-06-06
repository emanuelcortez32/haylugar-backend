package ar.com.velascosoft.haylugar.rest.responses;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetUserSpotResponse extends ApiResponse {
    @Builder
    public GetUserSpotResponse(boolean success,
                                String message,
                                SpotResponse spotResponse) {
        super(success, message);
        this.data = Map.of("spot", spotResponse);
    }
}
