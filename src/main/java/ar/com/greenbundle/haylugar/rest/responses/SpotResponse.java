package ar.com.greenbundle.haylugar.rest.responses;

import ar.com.greenbundle.haylugar.dto.Address;
import ar.com.greenbundle.haylugar.dto.Location;
import ar.com.greenbundle.haylugar.dto.constants.SpotState;
import ar.com.greenbundle.haylugar.dto.constants.SpotType;
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
