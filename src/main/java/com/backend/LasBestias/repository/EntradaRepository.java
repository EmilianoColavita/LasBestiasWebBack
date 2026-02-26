package com.backend.LasBestias.repository;

import com.backend.LasBestias.model.Entrada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EntradaRepository extends JpaRepository<Entrada, Long> {

    List<Entrada> findByEventoId(Long eventoId);

    boolean existsByPaymentId(String paymentId);

    Optional<Entrada> findByQrToken(String qrToken);

    List<Entrada> findByPaymentId(String paymentId);

}
