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
public class PlotDateChartDTO {

    private Long id;
    private Long lokSabhaNo;
    private Long lokSabhaSessionNo;
    private Long rajyaSabhaSessionNo;
    private Long ministryId;
    private Long inputId;
    private String groupName;
    private List<Map<String, Object>> dates;

}
