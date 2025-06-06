package ar.com.velascosoft.haylugar.entities;

import ar.com.velascosoft.haylugar.pojo.Address;
import ar.com.velascosoft.haylugar.pojo.constants.SpotState;
import ar.com.velascosoft.haylugar.pojo.constants.SpotType;
import io.r2dbc.postgresql.codec.Point;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("spots")
public class SpotEntity extends GenericEntity {
    @Column("landlord_user_id")
    private String landLordUserId;
    private SpotType type;
    private Point location;
    private Address address;
    private String zone;
    private int capacity;
    @Column("capacity_available")
    private int capacityAvailable;
    @Column("price_per_minute")
    private double pricePerMinute;
    private String description;
    private SpotState state;
    private String[] photos;

    @Builder
    public SpotEntity(String id, LocalDateTime createdAt, LocalDateTime updatedAt, Long version, boolean deleted, String landLordUserId, SpotType type, Point location, Address address, String zone, int capacity, int capacityAvailable, double pricePerMinute, String description, SpotState state, String[] photos) {
        super(id, createdAt, updatedAt, version, deleted);
        this.landLordUserId = landLordUserId;
        this.type = type;
        this.location = location;
        this.address = address;
        this.zone = zone;
        this.capacity = capacity;
        this.capacityAvailable = capacityAvailable;
        this.pricePerMinute = pricePerMinute;
        this.description = description;
        this.state = state;
        this.photos = photos;
    }
}
