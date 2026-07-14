package com.backend.LasBestias.repository;

import com.backend.LasBestias.model.Invitado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitadoRepository extends JpaRepository<Invitado, Long> {

    List<Invitado> findByEventoId(Long eventoId);

    Optional<Invitado> findByEventoIdAndDni(Long eventoId, String dni);

    boolean existsByEventoIdAndDni(Long eventoId, String dni);
}