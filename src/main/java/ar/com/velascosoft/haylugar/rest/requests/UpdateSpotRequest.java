package ar.com.velascosoft.haylugar.rest.requests;

import ar.com.velascosoft.haylugar.pojo.Location;
import ar.com.velascosoft.haylugar.pojo.constants.SpotType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSpotRequest extends ValidatedRequestBean {
    private SpotType type;
    @Min(value = 1, message = "The min spot capacity is 1")
    private int capacity;
    @DecimalMin(value = "15.0", message = "The min price per minute is $15 ARS")
    private double pricePerMinute;
    private String description;
    private Location location;
}
