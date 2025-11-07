package com.backend.LasBestias.service.impl;

import com.backend.LasBestias.model.Evento;

import com.backend.LasBestias.repository.EventoRepository;
import com.backend.LasBestias.service.EventoService;
import com.backend.LasBestias.service.ImageService;
import com.backend.LasBestias.service.dto.request.EventoDTOin;

import com.backend.LasBestias.service.dto.response.EventoDTO;
import com.backend.LasBestias.model.ImageType;

import com.backend.LasBestias.service.mapper.EventoMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EventoServiceImpl implements EventoService {

    private final EventoRepository eventoRepository;
    private final ImageService imageService;

    public EventoServiceImpl(EventoRepository eventoRepository, ImageService imageService) {
        this.eventoRepository = eventoRepository;
        this.imageService = imageService;
    }

    @Override
    public EventoDTO create(EventoDTOin dto) {
        Evento evento = EventoMapper.MAPPER.toEntity(dto);
        evento = eventoRepository.save(evento);

        if (dto.getImage() != null) {
            Long imageId = imageService.uploadImage(dto.getImage(), ImageType.EVENTO, evento.getId());
            evento.setImageId(imageId);
            evento = eventoRepository.save(evento);
        }

        return EventoMapper.MAPPER.toDto(evento);
    }

    @Override
    public EventoDTO getById(Long id) {
        Evento evento = getEvento(id);
        EventoDTO dto = EventoMapper.MAPPER.toDto(evento);
        dto.setImagenUrl(imageService.getS3url(evento.getId(), ImageType.EVENTO));
        return dto;
    }

    @Override
    public Page<EventoDTO> getAll(Pageable pageable) {
        return eventoRepository.findAll(pageable).map(evento -> {
            EventoDTO dto = EventoMapper.MAPPER.toDto(evento);
            dto.setImagenUrl(imageService.getS3url(evento.getId(), ImageType.EVENTO));
            return dto;
        });
    }

    @Override
    public EventoDTO update(Long id, EventoDTOin dto) {
        Evento evento = getEvento(id);
        Evento updated = EventoMapper.MAPPER.toEntity(dto);

        evento.setNombre(updated.getNombre());
        evento.setDescripcion(updated.getDescripcion());
        evento.setLugar(updated.getLugar());
        evento.setCiudad(updated.getCiudad());
        evento.setFechaEvento(updated.getFechaEvento());

        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            imageService.deleteImage(evento.getId(), ImageType.EVENTO);

            Long imageId = imageService.uploadImage(dto.getImage(), ImageType.EVENTO, evento.getId());
            evento.setImageId(imageId);
        }

        evento = eventoRepository.save(evento);
        return EventoMapper.MAPPER.toDto(evento);
    }


    @Override
    public void delete(Long id) {
        eventoRepository.delete(getEvento(id));
    }

    private Evento getEvento(Long id) {
        Optional<Evento> evento = eventoRepository.findById(id);
        return evento.orElseThrow(() -> new RuntimeException("Evento no encontrado"));
    }
}
