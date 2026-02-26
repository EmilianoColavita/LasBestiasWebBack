package com.backend.LasBestias.payment;

import lombok.Data;

@Data
public class PaymentRequest {

    private Long eventoId;

    private String nombre;
    private String email;
    private String telefono;
    private String dni;

    private Integer cantidad;
}