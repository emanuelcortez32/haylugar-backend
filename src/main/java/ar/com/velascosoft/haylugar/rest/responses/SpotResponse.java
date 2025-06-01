package ar.com.velascosoft.haylugar.rest.responses;

import ar.com.velascosoft.haylugar.pojo.Address;
import ar.com.velascosoft.haylugar.pojo.Location;
import ar.com.velascosoft.haylugar.pojo.constants.SpotState;
import ar.com.velascosoft.haylugar.pojo.constants.SpotType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpotResponse {
    private String id;
    private SpotType type;
    private Location location;
    private Address address;
    private double pricePerMinute;
    private String description;
    private SpotState spotState;
    private String[] photos;
}
