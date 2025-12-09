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
    private String paymentId;

    private LocalDateTime fechaCompra;
}
