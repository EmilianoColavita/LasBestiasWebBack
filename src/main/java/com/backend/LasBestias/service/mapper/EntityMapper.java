package com.backend.LasBestias.service.mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

public interface EntityMapper<D,E> {
    E toEntity(D dto);

    //RESPONSE
    D toDto(E entity);

    //REQUEST
    List<E> toEntity(List<D> dtoList);

    //RESPONSE
    List<D> toDto(List<E> entityList);

    //REQUEST
    Iterable<E> toEntity(Iterable<D> dtoList);

    //RESPONSE
    Iterable<D> toDto(Iterable<E> entityList);

    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget E entity, E updateEntity);
}