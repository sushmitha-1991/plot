package com.dsp.plot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplatesDTO {

    private long id;
    private Long applicationId;
    private String templateName;
    private String abbr;
    private String content;
    private String language; 
    private Long lokSabhaNo;
    private String sessionNo;
    private Long houseId;
    private Long sessionId;


}

