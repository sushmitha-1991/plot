package com.dsp.plot.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlotHistoryDTO {
    private Long id;
    private String status;
    private Long plotInputId;
    private Long ministryId;
    private String addedText;
}
