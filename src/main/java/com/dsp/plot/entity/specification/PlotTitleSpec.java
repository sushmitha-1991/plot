package com.dsp.plot.entity.specification;

import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;
import com.dsp.plot.entity.PaperLaidOnTable;
import jakarta.persistence.criteria.Expression;

public class PlotTitleSpec {
    public static Specification<PaperLaidOnTable> isActive(boolean isActive) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), isActive);
    }

    public static Specification<PaperLaidOnTable> hasLokSabhaNo(Long lokSabhaNo) {
        return (root, query, criteriaBuilder) -> lokSabhaNo != null
                ? criteriaBuilder.equal(root.get("lokSabhaNo"), lokSabhaNo)
                : null;
    }

    public static Specification<PaperLaidOnTable> hasDraftStatus(Boolean draftStatus) {
        return (root, query, criteriaBuilder) -> draftStatus != null
                ? criteriaBuilder.equal(root.get("draftFlag"), draftStatus)
                : null;
    }

    public static Specification<PaperLaidOnTable> hasDateBetween(LocalDate startDate, LocalDate endDate) {
        
        return (root, query, criteriaBuilder) -> 
        {
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("plotLaidDate"), startDate, endDate);
            } else if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("plotLaidDate"), startDate);
            } else if (endDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("plotLaidDate"), endDate);
            } else {
                return null;
            }
        };
    }

  
    public static Specification<PaperLaidOnTable> hasLokSabhaSessionNo(Long lokSabhaSessionNo) {
        return (root, query, criteriaBuilder) -> lokSabhaSessionNo != null
                ? criteriaBuilder.equal(root.get("lokSabhaSessionNo"), lokSabhaSessionNo)
                : null;
    }

    public static Specification<PaperLaidOnTable> hasRajyaSabhaSessionNo(Long rajyaSabhaSessionNo) {
        return (root, query, criteriaBuilder) -> rajyaSabhaSessionNo != null
                ? criteriaBuilder.equal(root.get("rajyaSabhaSessionNo"), rajyaSabhaSessionNo)
                : null;
    }

    public static Specification<PaperLaidOnTable> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> status != null ? criteriaBuilder.equal(root.get("status"), status)
                : null;
    }
    public static Specification<PaperLaidOnTable> hasTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.isBlank()) {
                return null; // No filter applied if title is null or empty
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<PaperLaidOnTable> hasMinistryId(Long ministryId) {
        return (root, query, criteriaBuilder) -> ministryId != null
                ? criteriaBuilder.equal(root.get("ministryId"), ministryId)
                : null;
    }

}
