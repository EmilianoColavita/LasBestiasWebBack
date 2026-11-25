package com.backend.LasBestias.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Ajustes de esquema que ddl-auto=update no hace solo.
 *
 * - evento.precio pasa a ser nullable (EXTERNO / SIN_VENTA_ONLINE pueden no tener precio).
 *   Hibernate agrega columnas pero nunca modifica la nulabilidad de una existente.
 * - Por si acaso, los eventos sin modo_venta quedan en MERCADO_PAGO.
 *
 * Es idempotente: solo actúa si hace falta.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventoSchemaMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            List<String> nullable = jdbcTemplate.queryForList(
                    "SELECT IS_NULLABLE FROM INFORMATION_SCHEMA.COLUMNS " +
                            "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'evento' AND COLUMN_NAME = 'precio'",
                    String.class);

            if (!nullable.isEmpty() && "NO".equalsIgnoreCase(nullable.get(0))) {
                jdbcTemplate.execute("ALTER TABLE evento MODIFY precio DOUBLE NULL");
                log.info("Migración: evento.precio ahora admite NULL");
            }

            int actualizados = jdbcTemplate.update(
                    "UPDATE evento SET modo_venta = 'MERCADO_PAGO' WHERE modo_venta IS NULL OR modo_venta = ''");
            if (actualizados > 0) {
                log.info("Migración: {} eventos quedaron en modo_venta = MERCADO_PAGO", actualizados);
            }
        } catch (Exception e) {
            log.error("No se pudo aplicar la migración de esquema de evento", e);
        }
    }
}
