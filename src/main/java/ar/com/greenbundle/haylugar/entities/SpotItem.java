package ar.com.greenbundle.haylugar.entities;

import ar.com.greenbundle.haylugar.dto.Address;
import ar.com.greenbundle.haylugar.dto.constants.SpotState;
import ar.com.greenbundle.haylugar.dto.constants.SpotType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("spots")
public class SpotItem extends GenericItem {
    private String landLordUserId;
    private SpotType type;
    @Builder.Default
    private GeoJsonPoint location = new GeoJsonPoint(0,0);
    private Address address;
    private int capacity;
    @Builder.Default
    private double pricePerMinute = 15.0;
    private String description;
    private SpotState spotState;
    private String[] photos;
}
