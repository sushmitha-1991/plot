package com.dsp.plot.entity.specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.dsp.plot.entity.PlotGenerate;
import com.dsp.plot.entity.PlotInput;

public class PlotGenerateSpec 
{
    public static Specification<PlotGenerate> isActive(boolean isActive) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), isActive);
    }
    public static Specification<PlotGenerate> hasLokSabhaNo(Long lokSabhaNo) 
    {
        return (root, query, criteriaBuilder) ->
            lokSabhaNo != null ? criteriaBuilder.equal(root.get("lokSabhaNo"), lokSabhaNo) : null;
    }
    public static Specification<PlotGenerate> hasDraftStatus(Boolean draftStatus) {
        return (root, query, criteriaBuilder) ->
                draftStatus != null ? criteriaBuilder.equal(root.get("draftFlag"), draftStatus) : null;
    }
     public static Specification<PlotGenerate> hasSendToLibrary(Boolean sendToLibrary) {
        return (root, query, criteriaBuilder) ->
        sendToLibrary != null ? criteriaBuilder.equal(root.get("sendToLibrary"), sendToLibrary) : null;
    }
    public static Specification<PlotGenerate> hasDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> 
        {
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("plotDate"), startDate, endDate);
            } else if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("plotDate"), startDate);
            } else if (endDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("plotDate"), endDate);
            } else {
                return null;
            }
        };
    }
    public static Specification<PlotGenerate> hasLokSabhaSessionNo(Long lokSabhaSessionNo) 
    {        
        return (root, query, criteriaBuilder) ->
        lokSabhaSessionNo != null ? criteriaBuilder.equal(root.get("lokSabhaSessionNo"), lokSabhaSessionNo) : null;
    }
    public static Specification<PlotGenerate> hasRajyaSabhaSessionNo( Long rajyaSabhaSessionNo) {
        return (root, query, criteriaBuilder) -> 
        rajyaSabhaSessionNo != null ? criteriaBuilder.equal(root.get("rajyaSabhaSessionNo"), rajyaSabhaSessionNo) : null;
    }
    public static Specification<PlotGenerate> hasStatus( String status) {
        return (root, query, criteriaBuilder) -> 
        status != null ? criteriaBuilder.equal(root.get("status"), status) : null;
    }
}
