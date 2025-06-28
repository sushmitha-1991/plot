/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.plot.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 */
@Data
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DraftDataDTO {
	private Long draftId;
	private JsonNode data;
	private Long applicationId;
	private String rajyaSabhaSession;
	private String lokSabhaSessionNumber;
	private String lokSabhaSession;
	private String applicationNo;
	private LocalDateTime createdAt;
    private LocalDate createdAtWithoutTime;
    private String createdBy;
    

}
