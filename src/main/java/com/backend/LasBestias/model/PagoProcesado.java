package com.backend.LasBestias.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class PagoProcesado {

    @Id
    private String paymentId;

    private LocalDateTime fechaProcesado;
}