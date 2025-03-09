package com.dsp.plot.entity.specification;

import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;
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

    public static Specification<PlotLtno> hasActualDateOfLaying(LocalDate actualDateOfLaying) {
        return (root, query, criteriaBuilder) -> {
            if (actualDateOfLaying != null) {
                return criteriaBuilder.between(root.get("actualDateOfLaying"), actualDateOfLaying, actualDateOfLaying);
            } else {
                return null;
            }
        };
    }

    public static Specification<PlotLtno> hasSubmissionDate(LocalDate submissionDate) {
        return (root, query, criteriaBuilder) -> {
            if (submissionDate != null) {
                return criteriaBuilder.between(root.get("submissionDate"), submissionDate, submissionDate);
            } else {
                return null;
            }
        };
    }

    public static Specification<PlotLtno> hasProposedDateOfLaying(LocalDate proposedDateOfLaying) {
        return (root, query, criteriaBuilder) -> {
            if (proposedDateOfLaying != null) {
                return criteriaBuilder.between(root.get("proposedDateOfLaying"), proposedDateOfLaying,
                        proposedDateOfLaying);
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

    public static Specification<PlotLtno> hasMinistryId(Long ministryId) {
        return (root, query, criteriaBuilder) -> ministryId != null
                ? criteriaBuilder.equal(root.get("ministryId"), ministryId)
                : null;
    }

}
