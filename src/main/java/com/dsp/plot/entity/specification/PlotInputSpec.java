package com.dsp.plot.entity.specification;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;
import com.dsp.plot.entity.PlotInput;
import jakarta.persistence.criteria.Expression;
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
    public static Specification<PlotInput> hasSendToLibrary(Boolean sendToLibrary) {
        return (root, query, criteriaBuilder) ->
        sendToLibrary != null ? criteriaBuilder.equal(root.get("sendToLibrary"), sendToLibrary) : null;
    }
    public static Specification<PlotInput> hasCreatedBy(Long createdBy) {
        return (root, query, criteriaBuilder) ->
        createdBy != null ? criteriaBuilder.equal(root.get("createdBy"), createdBy) : null;
    }
    public static Specification<PlotInput> hasDateBetween(LocalDate startDate, LocalDate endDate) {
            return (root, query, criteriaBuilder) -> {
                // Extract LocalDate from createdAt (LocalDateTime)
                Expression<LocalDate> createdAtDate = criteriaBuilder.function(
                    "DATE", LocalDate.class, root.get("createdAt")
                );
        
                if (startDate != null && endDate != null) {
                    return criteriaBuilder.between(createdAtDate, startDate, endDate);
                } else if (startDate != null) {
                    return criteriaBuilder.greaterThanOrEqualTo(createdAtDate, startDate);
                } else if (endDate != null) {
                    return criteriaBuilder.lessThanOrEqualTo(createdAtDate, endDate);
                } else {
                    return null;
                }
            };
        }
        
    public static Specification<PlotInput> hasActualDateOfLaying(LocalDate actualDateOfLaying) {
        return (root, query, criteriaBuilder) -> 
        {
            if ( actualDateOfLaying != null) {
                return criteriaBuilder.between(root.get("actualDateOfLaying"), actualDateOfLaying, actualDateOfLaying);
            } else {
                return null;
            }
        };
    }
    public static Specification<PlotInput> hasSubmissionDate(LocalDate submissionDate) {
        return (root, query, criteriaBuilder) -> 
        {
            if ( submissionDate != null) {
                return criteriaBuilder.between(root.get("submissionDate"), submissionDate, submissionDate);
            } else {
                return null;
            }
        };
    }
    public static Specification<PlotInput> hasProposedDateOfLaying(LocalDate proposedDateOfLaying) {
        return (root, query, criteriaBuilder) -> 
        {
            if ( proposedDateOfLaying != null) {
                return criteriaBuilder.between(root.get("proposedDateOfLaying"), proposedDateOfLaying, proposedDateOfLaying);
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
    public static Specification<PlotInput> hasPaperTypeId(Long paperTypeId) {
        return (root, query, criteriaBuilder) -> 
        paperTypeId != null ? criteriaBuilder.equal(root.get("paperTypeId"), paperTypeId) : null;
    }
    public static Specification<PlotInput> hasMinistryId(Integer ministryId) {
        return (root, query, criteriaBuilder) -> 
        ministryId != null ? criteriaBuilder.equal(root.get("ministryId"), ministryId) : null;
    }
    public static Specification<PlotInput> hasDepartmentId(Long departmentId) {
        return (root, query, criteriaBuilder) -> 
        departmentId != null ? criteriaBuilder.equal(root.get("departmentId"), departmentId) : null;
    }
    //paperName
    public static Specification<PlotInput> hasPaperName(String paperName) {
        return (root, query, criteriaBuilder) -> 
        paperName != null ? criteriaBuilder.equal(root.get("paperName"), "%"+paperName.toLowerCase()+"%") : null;
    }
}
    
