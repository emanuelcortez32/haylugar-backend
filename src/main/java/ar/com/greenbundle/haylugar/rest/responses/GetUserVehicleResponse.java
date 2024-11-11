package ar.com.greenbundle.haylugar.rest.responses;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetUserVehicleResponse extends ApiResponse {
    @Builder
    public GetUserVehicleResponse(boolean success,
                                  String message,
                                  VehicleResponse vehicleResponse) {
        super(success, message);
        this.data = Map.of("vehicle", vehicleResponse);
    }
}
