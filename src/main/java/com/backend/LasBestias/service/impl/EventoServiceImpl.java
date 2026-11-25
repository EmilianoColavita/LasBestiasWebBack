package com.backend.LasBestias.service.impl;

import com.backend.LasBestias.model.Evento;
import com.backend.LasBestias.model.ModoVenta;

import com.backend.LasBestias.repository.EventoRepository;
import com.backend.LasBestias.service.EventoService;
import com.backend.LasBestias.service.ImageService;
import com.backend.LasBestias.service.dto.request.EventoDTOin;

import com.backend.LasBestias.service.dto.response.EventoDTO;
import com.backend.LasBestias.model.ImageType;

import com.backend.LasBestias.service.mapper.EventoMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventoServiceImpl implements EventoService {

    private static final int MAX_NOTA_VENTA = 200;
    private static final int MAX_URL_VENTA = 500;

    private final EventoRepository eventoRepository;
    private final ImageService imageService;

    public EventoServiceImpl(EventoRepository eventoRepository, ImageService imageService) {
        this.eventoRepository = eventoRepository;
        this.imageService = imageService;
    }

    @Override
    public EventoDTO create(EventoDTOin dto) {
        if (dto.getModoVenta() == null) {
            dto.setModoVenta(ModoVenta.MERCADO_PAGO);
        }
        normalizarYValidarVenta(dto);

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

        if (dto.getModoVenta() == null) {
            // Request sin datos de venta (p. ej. frontend viejo): se conservan los actuales.
            dto.setModoVenta(evento.getModoVenta() != null ? evento.getModoVenta() : ModoVenta.MERCADO_PAGO);
            dto.setPlataformaVenta(evento.getPlataformaVenta());
            dto.setUrlVenta(evento.getUrlVenta());
            dto.setNotaVenta(evento.getNotaVenta());
        }
        normalizarYValidarVenta(dto);

        Evento updated = EventoMapper.MAPPER.toEntity(dto);

        evento.setNombre(updated.getNombre());
        evento.setDescripcion(updated.getDescripcion());
        evento.setLugar(updated.getLugar());
        evento.setCiudad(updated.getCiudad());
        evento.setFechaEvento(updated.getFechaEvento());
        evento.setPrecio(updated.getPrecio());
        evento.setModoVenta(updated.getModoVenta());
        evento.setPlataformaVenta(updated.getPlataformaVenta());
        evento.setUrlVenta(updated.getUrlVenta());
        evento.setNotaVenta(updated.getNotaVenta());

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

    @Override
    public List<EventoDTO> getEventosFuturos() {
        LocalDateTime ahora = LocalDateTime.now();

        List<Evento> eventos = eventoRepository.findByFechaEventoAfterOrderByFechaEventoAsc(ahora);

        return eventos.stream().map(evento -> {
            EventoDTO dto = EventoMapper.MAPPER.toDto(evento);
            dto.setImagenUrl(imageService.getS3url(evento.getId(), ImageType.EVENTO));
            return dto;
        }).toList();
    }


    /**
     * Aplica las reglas del modo de venta sobre el DTO (modoVenta ya no nulo):
     * - MERCADO_PAGO: precio obligatorio y > 0.
     * - EXTERNO: urlVenta (http/https) y plataformaVenta obligatorias; precio opcional e informativo.
     * - SIN_VENTA_ONLINE: sin URL; precio opcional.
     * Fuera de EXTERNO se limpian urlVenta y plataformaVenta. notaVenta es opcional (máx. 200).
     */
    private void normalizarYValidarVenta(EventoDTOin dto) {
        ModoVenta modo = dto.getModoVenta();

        String nota = dto.getNotaVenta() == null ? null : dto.getNotaVenta().trim();
        if (nota != null && nota.isEmpty()) {
            nota = null;
        }
        if (nota != null && nota.length() > MAX_NOTA_VENTA) {
            throw badRequest("notaVenta no puede superar los " + MAX_NOTA_VENTA + " caracteres");
        }
        dto.setNotaVenta(nota);

        if (dto.getPrecio() != null && dto.getPrecio() < 0) {
            throw badRequest("El precio no puede ser negativo");
        }

        switch (modo) {
            case MERCADO_PAGO -> {
                if (dto.getPrecio() == null || dto.getPrecio() <= 0) {
                    throw badRequest("Para vender por Mercado Pago el precio es obligatorio y debe ser mayor a 0");
                }
                dto.setUrlVenta(null);
                dto.setPlataformaVenta(null);
            }
            case EXTERNO -> {
                if (dto.getPlataformaVenta() == null) {
                    throw badRequest("plataformaVenta es obligatoria cuando modoVenta es EXTERNO");
                }
                String url = dto.getUrlVenta() == null ? "" : dto.getUrlVenta().trim();
                if (url.isEmpty()) {
                    throw badRequest("urlVenta es obligatoria cuando modoVenta es EXTERNO");
                }
                if (url.length() > MAX_URL_VENTA || !esUrlHttpValida(url)) {
                    throw badRequest("urlVenta debe ser una URL válida que empiece con http:// o https://");
                }
                dto.setUrlVenta(url);
            }
            case SIN_VENTA_ONLINE -> {
                dto.setUrlVenta(null);
                dto.setPlataformaVenta(null);
            }
        }
    }

    private static boolean esUrlHttpValida(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            return scheme != null
                    && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                    && uri.getHost() != null
                    && !uri.getHost().isBlank();
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private static ResponseStatusException badRequest(String mensaje) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }

    private Evento getEvento(Long id) {
        Optional<Evento> evento = eventoRepository.findById(id);
        return evento.orElseThrow(() -> new RuntimeException("Evento no encontrado"));
    }
}
