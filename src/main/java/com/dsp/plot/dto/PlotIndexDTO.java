package com.dsp.plot.dto;


import java.util.List;
import java.util.Map;

import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Audited
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PlotIndexDTO {
    private Long id;
    private Long lokSabhaNo;
    private Long lokSabhaSessionNo;
    private Long rajyaSabhaSessionNo;
    private Boolean draftFlag;
    private String reportName;
    private Long applicationId;
    private String status;
    private String refNo;
    private String content;
    private List<Map<String,Object>> indexDetails;

}
