package ar.com.greenbundle.haylugar.entities;

import ar.com.greenbundle.haylugar.pojo.constants.VehicleSize;
import ar.com.greenbundle.haylugar.pojo.constants.VehicleType;
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
@Table("user_vehicles")
public class UserVehicleEntity extends GenericEntity {
    @Column("user_id")
    private String userId;
    private String brand;
    private String model;
    private String patent;
    private String year;
    private VehicleType type;
    private VehicleSize size;
    @Column("default_vehicle")
    private boolean defaultVehicle;

    @Builder
    public UserVehicleEntity(String id, LocalDateTime createdAt, LocalDateTime updatedAt, Long version, boolean deleted, String userId, String brand, String model, String patent, String year, VehicleType type, VehicleSize size, boolean defaultVehicle) {
        super(id, createdAt, updatedAt, version, deleted);
        this.userId = userId;
        this.brand = brand;
        this.model = model;
        this.patent = patent;
        this.year = year;
        this.type = type;
        this.size = size;
        this.defaultVehicle = defaultVehicle;
    }
}
