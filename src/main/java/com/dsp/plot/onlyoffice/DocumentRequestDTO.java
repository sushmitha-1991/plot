package com.dsp.plot.onlyoffice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequestDTO {

    private int empty;
    private boolean edit;
    private String documentKey;
    private String dsp2Key;
    private String fileName;
    private String applicationId;
    private Long refId;
    private String templateId;

}
