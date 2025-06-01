package ar.com.velascosoft.haylugar.dto;

import ar.com.velascosoft.haylugar.entities.GenericEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class EntityDto<T extends GenericEntity, K> implements EntityMappable<T, K> {
    private String id;
    private LocalDateTime createdAt;
    private Long version;
}
