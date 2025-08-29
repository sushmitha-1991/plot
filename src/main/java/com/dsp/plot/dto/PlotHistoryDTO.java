package com.dsp.plot.dto;

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
public class PlotHistoryDTO {
    private Long id;
    private String status;
    private Long plotInputId;
    private Long ministryId;
    @AllowHtml
    private String addedText;
}
