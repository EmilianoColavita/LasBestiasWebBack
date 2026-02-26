package com.backend.LasBestias.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Data
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private String lugar;

    @Column(nullable = false, length = 100)
    private String ciudad;


    @Column(nullable = false)
    private LocalDateTime fechaEvento;

    @Column(nullable = false)
    private Double precio;

    @Column
    private Long imageId;
}
