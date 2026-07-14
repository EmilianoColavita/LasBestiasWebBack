package com.backend.LasBestias.service;

import com.backend.LasBestias.service.dto.request.InvitadoDTOin;
import com.backend.LasBestias.service.dto.response.InvitadoDTO;

import java.util.List;
import java.util.Map;

public interface InvitadoService {

    InvitadoDTO crear(InvitadoDTOin dto);

    List<InvitadoDTO> listarPorEvento(Long eventoId);

    void eliminar(Long id);

    Map<String, Object> validarDni(Long eventoId, String dni);
}