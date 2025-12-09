package com.backend.LasBestias.repository;

import com.backend.LasBestias.model.Entrada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntradaRepository extends JpaRepository<Entrada, Long> {
    List<Entrada> findByEventoId(Long eventoId);
}
