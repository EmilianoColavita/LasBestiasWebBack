package com.backend.LasBestias.repository;

import com.backend.LasBestias.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long>, JpaSpecificationExecutor<Evento> {

    List<Evento> findByFechaEventoAfterOrderByFechaEventoAsc(LocalDateTime fechaEvento);

}
