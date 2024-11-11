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
public class GetUserVehiclesResponse extends ApiResponse {
    @Builder
    public GetUserVehiclesResponse(boolean success,
                                   String message,
                                   List<VehicleResponse> vehicles) {
        super(success, message);
        this.data = Map.of("vehicles", vehicles);
    }
}
