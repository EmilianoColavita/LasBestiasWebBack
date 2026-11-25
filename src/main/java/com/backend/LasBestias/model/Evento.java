package com.backend.LasBestias.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    // Obligatorio solo para MERCADO_PAGO (validado en el service).
    // Ojo: ddl-auto=update no quita el NOT NULL de la columna existente;
    // de eso se encarga EventoSchemaMigration al arrancar.
    @Column
    private Double precio;

    @Column
    private Long imageId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @ColumnDefault("'MERCADO_PAGO'")
    @Column(nullable = false, length = 30)
    private ModoVenta modoVenta = ModoVenta.MERCADO_PAGO;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 30)
    private PlataformaVenta plataformaVenta;

    @Column(length = 500)
    private String urlVenta;

    @Column(length = 200)
    private String notaVenta;
}
