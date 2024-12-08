package ar.com.greenbundle.haylugar.dto;

import ar.com.greenbundle.haylugar.entities.GenericEntity;

public interface EntityMappable<T extends GenericEntity, K> {
    K dtoFromEntity(T entity);
    T dtoToEntity(K dto);
}
