package ar.com.velascosoft.haylugar.rest.responses;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ZonesResponse extends ApiResponse {

    @Builder
    public ZonesResponse(boolean success,
                         String message,
                         List<ZoneResponse> zones) {
        super(success, message);
        this.data = Map.of("zones", zones);
    }
}
