package com.backend.LasBestias.service;

import com.backend.LasBestias.service.dto.request.EventoDTOin;
import com.backend.LasBestias.service.dto.response.EventoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventoService {
    EventoDTO create(EventoDTOin dto);
    EventoDTO getById(Long id);
    Page<EventoDTO> getAll(Pageable pageable);
    EventoDTO update(Long id, EventoDTOin dto);
    void delete(Long id);
    List<EventoDTO> getEventosFuturos();

}
