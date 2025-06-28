package com.dsp.plot.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RequestDTO {

    private Long id;
    private Long plotInputId;
    private String request;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDate createdAtWithOutTime;
    private Boolean isAlreadyReturned;

    // private PlotInputDTO plotDetails;
}
