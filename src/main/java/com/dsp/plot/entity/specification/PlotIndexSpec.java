package com.dsp.plot.entity.specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.dsp.plot.entity.PlotIndex;

public class PlotIndexSpec
{
    public static Specification<PlotIndex> isActive(boolean isActive) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), isActive);
    }
    public static Specification<PlotIndex> hasLokSabhaNo(Long lokSabhaNo) 
    {
        return (root, query, criteriaBuilder) ->
            lokSabhaNo != null ? criteriaBuilder.equal(root.get("lokSabhaNo"), lokSabhaNo) : null;
    }
    public static Specification<PlotIndex> hasDraftStatus(Boolean draftStatus) {
        return (root, query, criteriaBuilder) ->
                draftStatus != null ? criteriaBuilder.equal(root.get("draftFlag"), draftStatus) : null;
    }
    public static Specification<PlotIndex> hasLokSabhaSessionNo(Long lokSabhaSessionNo) 
    {        
        return (root, query, criteriaBuilder) ->
        lokSabhaSessionNo != null ? criteriaBuilder.equal(root.get("lokSabhaSessionNo"), lokSabhaSessionNo) : null;
    }
    public static Specification<PlotIndex> hasRajyaSabhaSessionNo( Long rajyaSabhaSessionNo) {
        return (root, query, criteriaBuilder) -> 
        rajyaSabhaSessionNo != null ? criteriaBuilder.equal(root.get("rajyaSabhaSessionNo"), rajyaSabhaSessionNo) : null;
    }
    public static Specification<PlotIndex> hasReportName( String reportName) {
        return (root, query, criteriaBuilder) -> {
                return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("reportName")),
                    "%" + reportName.toLowerCase() + "%"
                );
        };
    }
    public static Specification<PlotIndex> hasCreatedBy(Long createdBy) {
        return (root, query, criteriaBuilder) ->
        createdBy != null ? criteriaBuilder.equal(root.get("createdBy"), createdBy) : null;
    }
    
}
