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
public class PlotLaidOnTableDTO {
    private Long id;
    private Long lokSabhaNo;
    private Long lokSabhaSessionNo;
    private Long rajyaSabhaSessionNo;
    private LocalDate plotLaidDate;
    private Long ministryId;
    private String status;
    private String refNo;
    private Long applicationId;
    private String title;
    private List<Map<String, Object>> documents;
    private MastersLokSabhaDTO lokSabhaDTO;
    private MastersLokSabhaSessionDTO lokSabhaSessionDTO;
    private MastersRajyaSabhaSessionDTO rajyaSabhaSessionDTO;
    private MinistriesDTO ministries;
    
}
