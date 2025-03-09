package com.dsp.plot.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.dsp.plot.entity.Departments;
import com.dsp.plot.entity.MasterCode;
import com.dsp.plot.entity.MastersLokSabhaSession;
import com.dsp.plot.entity.MastersRajyaSabha;
import com.dsp.plot.entity.Ministries;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private List<Map<String, Object>> paperDetails;
    private List<Map<String, Object>> documents;
    private LocalDate proposedDateOfLaying;
    private LocalDate actualDateOfLaying;
    private Long nodalOfficerId;
    private String nodalOfficerDesignation;
    private String nodalOfficerPhoneNo;
    private String nodalOfficerMobileNo;
    private LocalDate prevPaperLaidDate;
    private String content;
    // Input Instances DTO
    private Long applicationId; // Reference to Application ID
    private String applicationNo;
    private String currentStatus;
    private Long refId;
    private LocalDate documentDate;
    private InputInstanceDTO inputInstanceDTO;
    private ApproverDTO nodalOfficerDTO;
    private ApproverDTO authenticatorDTO;
    private MinistriesDTO ministies;
    private MasterCodeDTO PaperTypes;
    private DepartmentDTO departments;
    private MastersLokSabhaDTO lokSabhaDTO;
    private MastersLokSabhaSessionDTO lokSabhaSessionDTO;
    private MastersRajyaSabhaSessionDTO rajyaSabhaSessionDTO;
    // requests
    private List<RequestDTO> requests;
}
