package com.backend.LasBestias.service.mapper;

import com.backend.LasBestias.model.Noticia;
import com.backend.LasBestias.service.dto.response.NoticiaDTO;
import com.backend.LasBestias.service.dto.request.NoticiaDTOin;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NoticiaMapper extends EntityMapper<NoticiaDTO, Noticia> {

    NoticiaMapper MAPPER = Mappers.getMapper(NoticiaMapper.class);

    // Convierte el DTO de entrada en entidad
    Noticia toEntity(NoticiaDTOin dto);

    // Actualiza una entidad existente (sin cambiar el id)
    @Override
    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget Noticia entity, Noticia updateEntity);
}
