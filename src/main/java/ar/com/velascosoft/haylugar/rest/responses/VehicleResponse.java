package ar.com.velascosoft.haylugar.rest.responses;

import ar.com.velascosoft.haylugar.pojo.constants.VehicleSize;
import ar.com.velascosoft.haylugar.pojo.constants.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {
    private String id;
    private String brand;
    private String model;
    private String year;
    private String patent;
    private VehicleType type;
    private VehicleSize size;
}
