package com.dsp.plot.entity.specification;

import org.springframework.data.jpa.domain.Specification;

import com.dsp.plot.entity.PlotDateChart;

public class DateChartSpec {
    public static Specification<PlotDateChart> isActive(boolean isActive) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), isActive);
    }
    public static Specification<PlotDateChart> hasLokSabhaNo(Long lokSabhaNo) 
    {
        return (root, query, criteriaBuilder) ->
            lokSabhaNo != null ? criteriaBuilder.equal(root.get("lokSabhaNo"), lokSabhaNo) : null;
    }
    
    
    public static Specification<PlotDateChart> hasLokSabhaSessionNo(Long lokSabhaSessionNo) 
    {        
        return (root, query, criteriaBuilder) ->
        lokSabhaSessionNo != null ? criteriaBuilder.equal(root.get("lokSabhaSessionNo"), lokSabhaSessionNo) : null;
    }
    public static Specification<PlotDateChart> hasRajyaSabhaSessionNo( Long rajyaSabhaSessionNo) {
        return (root, query, criteriaBuilder) -> 
        rajyaSabhaSessionNo != null ? criteriaBuilder.equal(root.get("rajyaSabhaSessionNo"), rajyaSabhaSessionNo) : null;
    }
    public static Specification<PlotDateChart> hasStatus( String status) {
        return (root, query, criteriaBuilder) -> 
        status != null ? criteriaBuilder.equal(root.get("status"), status) : null;
    }
    // public static Specification<PlotDateChart> hasStatus( String status) {
    //     return (root, query, criteriaBuilder) -> 
    //     status != null ? criteriaBuilder.equal(root.get("status"), status) : null;
    // }
    public static Specification<PlotDateChart> hasMinistryId(Long ministryId)
    {
        return(root,query,criteriaBuilder) ->
        ministryId!=null? criteriaBuilder.equal(root.get("ministryId"),ministryId):null;
    }
    public static Specification<PlotDateChart> hasGroupName(String groupName) {
        return (root, query, criteriaBuilder) -> 
            (groupName != null && !groupName.isEmpty()) 
                ? criteriaBuilder.like(criteriaBuilder.lower(root.get("groupName")), "%" + groupName.toLowerCase() + "%") 
                : null;
    }
    
    
}
