package com.backend.LasBestias.payment;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long eventoId;
    private String email;
    private String nombre;
    private String apellido;
}
