package com.dsp.plot.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InputInstanceDTO {
    
	private Long id;
	    private LocalDateTime createdAt;
	private Long applicationId; // Reference to Application ID
	private String applicationNo;
	private String currentStatus;
	// private String publishedDocumentUrl;
	private Long refId;
	// private String processInstanceId;
	private LocalDate documentDate;
	private String lokSabhaSession;
    private String lokSabhaSessionNumber;
    private String rajyaSabhaSession;
	 private String pendingAt;
    private Long applicationInstanceId;
	private String htmlContentEnglish;
	private String htmlContentHindi;
}
