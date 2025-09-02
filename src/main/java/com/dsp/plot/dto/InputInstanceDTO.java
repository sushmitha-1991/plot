package com.dsp.plot.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.dsp.plot.config.XSSConfig.AllowHtml;

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
	private Long refId;
	private LocalDate documentDate;
	private String lokSabhaSession;
	private String lokSabhaSessionNumber;
	private String rajyaSabhaSession;
	private String pendingAt;
	private Long applicationInstanceId;
    @AllowHtml
	private String htmlContentEnglish;
    @AllowHtml
    private String htmlContentHindi;
}
