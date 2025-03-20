package com.dsp.plot.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.dsp.plot.entity.MastersLokSabhaSession;
import com.dsp.plot.entity.MastersRajyaSabha;

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
public class PlotLtnoDto {
    private Long id;
    private Long lokSabhaNo;
    private Long lokSabhaSessionNo;
    private Long rajyaSabhaSessionNo;
    private String paperType;
    private LocalDate ltnoDate;
    private String paperName;
    private Long ministryId;
    private String ltnoNo;
    private String title;
    private List<Map<String, Object>> documents;
    private String status;
    private String remarks;
    private String refNo;
    private Long noOfPages;

    // Application Instance
    private Long applicationId;
    private String applicationNo;
    private ApplicationInstanceDTO applicationInstanceDTO;
    private MastersLokSabhaDTO lokSabhaDTO;
    private MastersLokSabhaSessionDTO lokSabhaSessionDTO;
    private MastersRajyaSabhaSessionDTO rajyaSabhaSessionDTO;
    private MinistriesDTO ministries;
}
