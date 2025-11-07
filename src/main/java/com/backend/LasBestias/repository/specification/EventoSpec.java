package com.backend.LasBestias.repository.specification;

import com.backend.LasBestias.model.Evento;
import com.backend.LasBestias.service.dto.request.EventoFilterDTO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collection;

public class EventoSpec {

    public static Specification<Evento> getSpec(EventoFilterDTO filter) {
        return (root, query, cb) -> {
            Collection<Predicate> predicates = new ArrayList<>();

            // 🔍 Filtro por nombre
            if (filter.getNombre() != null && !filter.getNombre().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + filter.getNombre().toLowerCase() + "%"));
            }

            // 📅 Filtro por fecha de evento (desde)
            if (filter.getFechaDesde() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaEvento"), filter.getFechaDesde()));
            }

            // 📅 Filtro por fecha de evento (hasta)
            if (filter.getFechaHasta() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fechaEvento"), filter.getFechaHasta()));
            }

            // 🔽 Ordenar por fecha de evento descendente (los más próximos primero)
            query.orderBy(cb.asc(root.get("fechaEvento")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
