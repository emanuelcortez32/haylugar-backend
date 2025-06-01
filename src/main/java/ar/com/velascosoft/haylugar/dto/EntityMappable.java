package ar.com.velascosoft.haylugar.dto;

import ar.com.velascosoft.haylugar.entities.GenericEntity;

public interface EntityMappable<T extends GenericEntity, K> {
    K dtoFromEntity(T entity);
    T dtoToEntity(K dto);
}
