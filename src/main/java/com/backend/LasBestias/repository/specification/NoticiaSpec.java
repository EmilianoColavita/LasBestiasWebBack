package com.backend.LasBestias.repository.specification;


import com.backend.LasBestias.model.Noticia;
import com.backend.LasBestias.service.dto.request.NoticiaFilterDTO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collection;

public class NoticiaSpec {

    public static Specification<Noticia> getSpec(NoticiaFilterDTO filter) {
        return (root, query, cb) -> {
            Collection<Predicate> predicates = new ArrayList<>();

            if (filter.getTitulo() != null && !filter.getTitulo().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("titulo")), "%" + filter.getTitulo().toLowerCase() + "%"));
            }

            if (filter.getFechaDesde() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaPublicacion"), filter.getFechaDesde()));
            }

            if (filter.getFechaHasta() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fechaPublicacion"), filter.getFechaHasta()));
            }

            query.orderBy(cb.desc(root.get("fechaPublicacion")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

