package com.dsp.plot.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.dsp.plot.config.XSSConfig.AllowHtml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
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
    private Integer ministryId;
    private Boolean digitallySigned;
    private String underAct;
    private String act;
    private Boolean draftFlag;
    private String status;
    private String remarks;
    private String refNo;
    private Long generatePlotId;
    private List<Map<String, Object>> paperDetails;
    private List<Map<String, Object>> documents;
    private LocalDate proposedDateOfLaying;
    private LocalDate actualDateOfLaying;
    private Long nodalOfficerId;
    private String nodalOfficerDesignation;
    private String nodalOfficerPhoneNo;
    private String nodalOfficerMobileNo;
    private LocalDate prevPaperLaidDate;
    @AllowHtml
    private String content;
    private Boolean sendToLibrary;
    @AllowHtml
    private String note;

    //changes
    private LocalDate dateOnWhichforwardedToLSSecretariat;
    // Input Instances DTO
    private Long applicationId; // Reference to Application ID
    private String applicationNo;
    private String currentStatus;
    private Long refId;
    private LocalDate documentDate;
    private InputInstanceDTO inputInstanceDTO;
    private Integer seqOfMinistryContent;
    private LocalDateTime createdAt;

    private String nodalOfficerName;
    private String authenticatorName;
    private String nodalOfficerEmail;


    private String ministryName;
    private String departmentName;
    private Integer departmentCode;
    private ApproverDTO nodalOfficerDTO;

    private MinistryAuthorityDTO authenticatorDTO;
    private MinistriesDTO ministies;
    private MasterCodeDTO paperTypes;
    private DepartmentDTO departments;
    private MastersLokSabhaDTO lokSabhaDTO;
    private MastersLokSabhaSessionDTO lokSabhaSessionDTO;
    private MastersRajyaSabhaSessionDTO rajyaSabhaSessionDTO;
    // requests
    private List<RequestDTO> requests;
}
