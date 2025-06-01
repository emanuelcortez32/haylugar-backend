package ar.com.velascosoft.haylugar.entities.app;

import ar.com.velascosoft.haylugar.entities.GenericEntity;
import io.r2dbc.postgresql.codec.Point;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("app.geo_zones")
public class ZoneEntity extends GenericEntity {
    private String name;
    private String description;
    private boolean enabled;
    private List<Point> coordinates;

    @Builder
    public ZoneEntity(String id, LocalDateTime createdAt, LocalDateTime updatedAt, Long version, boolean deleted, String name, String description, boolean enabled, List<Point> coordinates) {
        super(id, createdAt, updatedAt, version, deleted);
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.coordinates = coordinates;
    }
}
