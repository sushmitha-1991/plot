package com.dsp.plot.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlotInputDTO {
    private Long id;
    private Long lokSabhaNo;
    private Long lokSabhaSessionNo;
    private Long rajyaSabhaSessionNo;
    private LocalDate submissionDate;
    private Long departmentId;
    private Long authenticatedById;
    private Long paperTypeId;
    private String paperName;
    private Long ministryId;
    private Boolean digitallySigned;
    private Long actId;
    private Boolean draftFlag;
    private String status;
    private String remarks;
    private String refNo;
    private Long generatePlotId;
    private List<Map<String,Object>> paperDetails;
    private List<Map<String,Object>> documents;
    private LocalDate proposedDateOfLaying;
    private Long nodalOfficerId;
    private String nodalOfficerDesignation;
    private String nodalOfficerPhoneNo;
    private String nodalOfficerMobileNo;
    private LocalDate prevPaperLaidDate;
    //Input Instances DTO
	private Long applicationId; // Reference to Application ID
	private String applicationNo;
	private String currentStatus;
	// private String publishedDocumentUrl;
	private Long refId;
	// private String processInstanceId;
	private LocalDate documentDate;
    private InputInstanceDTO inputInstanceDTO;
    // requests
    private List<RequestDTO> requests;
}
