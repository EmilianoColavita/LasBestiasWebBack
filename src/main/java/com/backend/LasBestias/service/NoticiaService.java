package com.backend.LasBestias.service;

import com.backend.LasBestias.service.dto.request.NoticiaDTOin;
import com.backend.LasBestias.service.dto.response.NoticiaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticiaService {
    NoticiaDTO create(NoticiaDTOin dto);
    NoticiaDTO getById(Long id);
    Page<NoticiaDTO> getAll(Pageable pageable);
    NoticiaDTO update(Long id, NoticiaDTOin dto);
    void delete(Long id);
}

