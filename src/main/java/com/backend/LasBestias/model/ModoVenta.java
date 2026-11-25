package com.backend.LasBestias.model;

/**
 * Por dónde se venden las entradas de un evento.
 */
public enum ModoVenta {
    /** Venta por la web con Mercado Pago (flujo con QR). */
    MERCADO_PAGO,
    /** Venta en una plataforma externa (Ticketek, WhatsApp, Instagram, etc.) mediante un link. */
    EXTERNO,
    /** Sin venta online (por ejemplo, entradas en puerta). */
    SIN_VENTA_ONLINE
}
