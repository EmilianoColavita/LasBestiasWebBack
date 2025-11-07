package com.backend.LasBestias.service.mapper;

import com.backend.LasBestias.model.Evento;
import com.backend.LasBestias.service.dto.request.EventoDTOin;
import com.backend.LasBestias.service.dto.response.EventoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EventoMapper {
    EventoMapper MAPPER = Mappers.getMapper(EventoMapper.class);

    Evento toEntity(EventoDTOin dto);
    EventoDTO toDto(Evento entity);
}
