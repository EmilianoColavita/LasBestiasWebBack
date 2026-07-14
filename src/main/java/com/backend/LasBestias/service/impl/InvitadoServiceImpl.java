package com.backend.LasBestias.service.impl;

import com.backend.LasBestias.model.Invitado;
import com.backend.LasBestias.repository.InvitadoRepository;
import com.backend.LasBestias.service.InvitadoService;
import com.backend.LasBestias.service.dto.request.InvitadoDTOin;
import com.backend.LasBestias.service.dto.response.InvitadoDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InvitadoServiceImpl implements InvitadoService {

    private final InvitadoRepository invitadoRepository;

    public InvitadoServiceImpl(InvitadoRepository invitadoRepository) {
        this.invitadoRepository = invitadoRepository;
    }

    @Override
    public InvitadoDTO crear(InvitadoDTOin dto) {
        if (invitadoRepository.existsByEventoIdAndDni(dto.getEventoId(), dto.getDni())) {
            throw new RuntimeException("El DNI " + dto.getDni() + " ya está en la lista de invitados de este evento");
        }

        Invitado invitado = new Invitado();
        invitado.setEventoId(dto.getEventoId());
        invitado.setNombre(dto.getNombre());
        invitado.setApellido(dto.getApellido());
        invitado.setDni(dto.getDni());
        invitado.setFechaCarga(LocalDateTime.now());

        return toDTO(invitadoRepository.save(invitado));
    }

    @Override
    public List<InvitadoDTO> listarPorEvento(Long eventoId) {
        return invitadoRepository.findByEventoId(eventoId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminar(Long id) {
        if (!invitadoRepository.existsById(id)) {
            throw new RuntimeException("Invitado no encontrado");
        }
        invitadoRepository.deleteById(id);
    }

    @Override
    public Map<String, Object> validarDni(Long eventoId, String dni) {
        return invitadoRepository.findByEventoIdAndDni(eventoId, dni)
                .map(inv -> Map.<String, Object>of(
                        "valido", true,
                        "nombre", inv.getNombre(),
                        "apellido", inv.getApellido(),
                        "dni", inv.getDni()
                ))
                .orElse(Map.of("valido", false));
    }

    private InvitadoDTO toDTO(Invitado inv) {
        InvitadoDTO dto = new InvitadoDTO();
        dto.setId(inv.getId());
        dto.setEventoId(inv.getEventoId());
        dto.setNombre(inv.getNombre());
        dto.setApellido(inv.getApellido());
        dto.setDni(inv.getDni());
        dto.setFechaCarga(inv.getFechaCarga());
        return dto;
    }
}