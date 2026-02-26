package com.backend.LasBestias.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Entrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long eventoId;

    private String nombreComprador;
    private String email;
    private String telefono;
    private String dni;

    private String paymentId;

    @Column(unique = true)
    private String qrToken;

    private LocalDateTime fechaCompra;

    private LocalDateTime fechaUso;

    private Boolean usada = false;
}