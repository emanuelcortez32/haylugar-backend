package ar.com.greenbundle.haylugar.rest.responses;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetUserSpotsResponse extends ApiResponse {
    @Builder
    public GetUserSpotsResponse(boolean success,
                                String message,
                                List<SpotResponse> spots) {
        super(success, message);
        this.data = Map.of("spots", spots);
    }
}
