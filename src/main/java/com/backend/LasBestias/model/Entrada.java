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
    private String nombre;
    private String apellido;
    private String email;

    @Column(unique = true)
    private String paymentId;

    @Column(unique = true)
    private String qrToken;

    private boolean usado = false;

    private LocalDateTime fechaUso;

    private LocalDateTime fechaCompra;
}