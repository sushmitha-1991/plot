package com.dsp.plot.dto;

import java.time.LocalDate;

import com.dsp.plot.config.XSSConfig.AllowHtml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationInstanceDTO {
    private Long id;
    private Long applicationId; // Reference to Application ID
    private String applicationNo;
    private String currentStatus;
    private LocalDate documentDate;
    // private String publishedDocumentUrl;
    private Long refId;
    private String lokSabhaSession;
    private String lokSabhaSessionNumber;
    private String rajyaSabhaSession;
    @AllowHtml
    private String htmlContentEnglish;
    @AllowHtml
    private String htmlContentHindi;
    private Long parentInstanceId;
    // private String startingPageNo;
    // private String processInstanceId;
}
