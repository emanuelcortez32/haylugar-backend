package ar.com.greenbundle.haylugar.dto;

import ar.com.greenbundle.haylugar.dto.constants.SpotType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSpotData {
    private SpotType type;
    private Location location;
    private int capacity;
    private double pricePerMinute;
    private String description;
    private String[] photos;
}
