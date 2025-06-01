package ar.com.velascosoft.haylugar.rest.requests;

import ar.com.velascosoft.haylugar.pojo.constants.VehicleSize;
import ar.com.velascosoft.haylugar.pojo.constants.VehicleType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class CreateVehicleRequest extends ValidatedRequestBean {
    @NotNull(message = "Vehicle brand is required")
    private String brand;
    @NotNull(message = "Vehicle model is required")
    private String model;
    @NotNull(message = "Vehicle patent is required")
    private String patent;
    @NotNull(message = "Vehicle year is required")
    @Pattern(regexp = "^\\d{4}$", message = "Year must be in format YYYY")
    private String year;
    @NotNull(message = "Vehicle type is required")
    private VehicleType type;
    @NotNull(message = "Vehicle size is required")
    private VehicleSize size;
}
