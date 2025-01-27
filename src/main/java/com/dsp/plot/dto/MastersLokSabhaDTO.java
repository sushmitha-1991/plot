
        
    package com.dsp.plot.dto;


    import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
    
    @Data
    @EqualsAndHashCode(callSuper = false)
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    public class MastersLokSabhaDTO {
        @NotEmpty
        private Long id;

        @NotEmpty
        private String lokSabhaNumber;

        // private LocalDate startDate;
        // private LocalDate endDate;
        
        // private Long totalSessions;

        // private Long totalSittings;
        // @NotEmpty
        // private Long createdBy;
        
        }
    
        
        
    
    
    