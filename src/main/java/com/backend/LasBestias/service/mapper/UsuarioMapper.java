package com.backend.LasBestias.service.mapper;

import com.backend.LasBestias.model.Usuario;
import com.backend.LasBestias.service.dto.request.UsuarioDTOin;
import com.backend.LasBestias.service.dto.response.UsuarioDTO;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;




@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioMapper extends EntityMapper<UsuarioDTO, Usuario> {
    Usuario toEntity(UsuarioDTOin dto);
}
