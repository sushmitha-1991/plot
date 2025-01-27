package com.dsp.plot.dto;
import java.time.LocalDate;
import java.time.LocalTime;
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
public class PlotGenerateDTO {
    private Long lokSabhaNo;
    private Long lokSabhaSessionNo;
    private Long rajyaSabhaSessionNo;
    private LocalDate plotDate;
    private LocalTime plotTime;
    private Long approverAuthority;
    private Long designation;
    private String location;
    private Boolean draftFlag;
    private String status;
    private String content;
    private String footNote;
    private List<Map<String, Object>> referenceFiles;
    private String paperType;
    private String paperName;
    private String remarks;
    //PlotGenerate
    private List<Long> plotInputs;
    //Application Instance DTO
    private Long id;
	private Long applicationId;
	private String applicationNo;
	private Long refId;
	private String currentStatus;
	// private String publishedDocumentUrl;
	// private String processInstanceId;
    private ApplicationInstanceDTO applicationInstanceDTO;
}

