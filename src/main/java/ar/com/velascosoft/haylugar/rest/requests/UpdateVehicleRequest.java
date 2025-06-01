package ar.com.velascosoft.haylugar.rest.requests;

import ar.com.velascosoft.haylugar.pojo.constants.VehicleSize;
import ar.com.velascosoft.haylugar.pojo.constants.VehicleType;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateVehicleRequest extends ValidatedRequestBean {
    private String brand;
    private String model;
    private String patent;
    @Pattern(regexp = "^\\d{4}$", message = "Year must be in format YYYY")
    private String year;
    private VehicleType type;
    private VehicleSize size;
}
