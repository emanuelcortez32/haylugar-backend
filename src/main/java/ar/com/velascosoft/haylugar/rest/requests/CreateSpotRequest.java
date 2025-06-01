package ar.com.velascosoft.haylugar.rest.requests;

import ar.com.velascosoft.haylugar.pojo.Location;
import ar.com.velascosoft.haylugar.pojo.constants.SpotType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class CreateSpotRequest extends ValidatedRequestBean {
    @NotNull(message = "Spot type is required")
    private SpotType type;
    @Min(value = 1, message = "The min spot capacity is 1")
    private int capacity;
    @DecimalMin(value = "15.0", message = "The min price per minute is $15 ARS")
    private double pricePerMinute;
    private String description;
    @NotNull(message = "Location is required")
    private Location location;
}
