package com.dsp.plot.entity.specification;

import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

import com.dsp.plot.entity.PaperLaidOnTable;
import com.dsp.plot.entity.PlotLtno;
import jakarta.persistence.criteria.Expression;

public class PlotLtnoSpec {
    public static Specification<PlotLtno> isActive(boolean isActive) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), isActive);
    }

    public static Specification<PlotLtno> hasLokSabhaNo(Long lokSabhaNo) {
        return (root, query, criteriaBuilder) -> lokSabhaNo != null
                ? criteriaBuilder.equal(root.get("lokSabhaNo"), lokSabhaNo)
                : null;
    }


    public static Specification<PlotLtno> hasDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> 
        {
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("ltnoDate"), startDate, endDate);
            } else if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("ltnoDate"), startDate);
            } else if (endDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("ltnoDate"), endDate);
            } else {
                return null;
            }
        };
    }

   
    public static Specification<PlotLtno> hasLokSabhaSessionNo(Long lokSabhaSessionNo) {
        return (root, query, criteriaBuilder) -> lokSabhaSessionNo != null
                ? criteriaBuilder.equal(root.get("lokSabhaSessionNo"), lokSabhaSessionNo)
                : null;
    }

    public static Specification<PlotLtno> hasRajyaSabhaSessionNo(Long rajyaSabhaSessionNo) {
        return (root, query, criteriaBuilder) -> rajyaSabhaSessionNo != null
                ? criteriaBuilder.equal(root.get("rajyaSabhaSessionNo"), rajyaSabhaSessionNo)
                : null;
    }

    public static Specification<PlotLtno> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> status != null ? criteriaBuilder.equal(root.get("status"), status)
                : null;
    }
    public static Specification<PlotLtno> hasLtnoNo(String ltnoNo) {
        return (root, query, criteriaBuilder) -> ltnoNo != null ? criteriaBuilder.equal(root.get("ltnoNo"), ltnoNo)
                : null;
    }

    public static Specification<PlotLtno> hasMinistryId(Long ministryId) {
        return (root, query, criteriaBuilder) -> ministryId != null
                ? criteriaBuilder.equal(root.get("ministryId"), ministryId)
                : null;
    }
    public static Specification<PlotLtno> hasPaperType(String paperType) {
        return (root, query, criteriaBuilder) -> {
            if (paperType == null || paperType.isBlank()) {
                return null; // No filter applied if title is null or empty
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("paperType")), "%" + paperType.toLowerCase() + "%");
        };
    }

}
