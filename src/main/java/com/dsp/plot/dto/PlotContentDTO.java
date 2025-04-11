package com.dsp.plot.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
public class PlotContentDTO {
    private Long minstryId;
    private String addedText;
    private Long plotInputId;
    private String status;
}
