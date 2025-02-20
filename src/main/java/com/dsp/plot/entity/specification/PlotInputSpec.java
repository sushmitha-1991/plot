package com.dsp.plot.entity.specification;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.dsp.plot.entity.PlotGenerate;
import com.dsp.plot.entity.PlotInput;

public class PlotInputSpec 
{
    public static Specification<PlotInput> isActive(boolean isActive) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), isActive);
    }
    public static Specification<PlotInput> hasLokSabhaNo(Long lokSabhaNo) 
    {
        return (root, query, criteriaBuilder) ->
            lokSabhaNo != null ? criteriaBuilder.equal(root.get("lokSabhaNo"), lokSabhaNo) : null;
    }
    public static Specification<PlotInput> hasDraftStatus(Boolean draftStatus) {
        return (root, query, criteriaBuilder) ->
                draftStatus != null ? criteriaBuilder.equal(root.get("draftFlag"), draftStatus) : null;
    }
    public static Specification<PlotInput> hasDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> 
        {
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("submissionDate"), startDate, endDate);
            } else if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("submissionDate"), startDate);
            } else if (endDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("submissionDate"), endDate);
            } else {
                return null;
            }
        };
    }
    public static Specification<PlotInput> hasLokSabhaSessionNo(Long lokSabhaSessionNo) 
    {        
        return (root, query, criteriaBuilder) ->
        lokSabhaSessionNo != null ? criteriaBuilder.equal(root.get("lokSabhaSessionNo"), lokSabhaSessionNo) : null;
    }
    public static Specification<PlotInput> hasRajyaSabhaSessionNo( Long rajyaSabhaSessionNo) {
        return (root, query, criteriaBuilder) -> 
        rajyaSabhaSessionNo != null ? criteriaBuilder.equal(root.get("rajyaSabhaSessionNo"), rajyaSabhaSessionNo) : null;
    }
     public static Specification<PlotInput> hasStatus( String status) {
        return (root, query, criteriaBuilder) -> 
        status != null ? criteriaBuilder.equal(root.get("status"), status) : null;
    }
}
    
