package com.dsp.plot.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dsp.plot.dto.DocumentDTO;
import com.dsp.plot.dto.PlotDateChartDTO;
import com.dsp.plot.dto.PlotInputDTO;
import com.dsp.plot.dto.RequestDTO;
import com.dsp.plot.entity.PlotDateChart;
@Service
public interface PlotInputService {

    PlotInputDTO save(PlotInputDTO plotinputDTO);

    List<PlotInputDTO> getAll();

    List<PlotInputDTO> getAll(Boolean isActive, Boolean draftStatus, Long lokSabhaNo,
    Long lokSabhaSessionNo, Long rajyaSabhaSessionNo, LocalDate startDate, LocalDate endDate,
    String status);

    PlotInputDTO getById(Long id);
    
    PlotInputDTO update(Long id, PlotInputDTO plotinputDTO);

    void delete(Long id);

    RequestDTO saveRequest(RequestDTO dto);
     
    List<RequestDTO> getAllRequests();

    List<RequestDTO> getAllRequestsByPlotId(Long plotId);

    RequestDTO getRequestById(Long id);
        // DocumentDTO getDocumentByDate(LocalDate dateOfDoc, String doc);
     PlotDateChartDTO addDateChart(PlotDateChartDTO dto);
     List<PlotInputDTO> getHistories(Boolean isActive, Boolean draftStatus, Long lokSabhaNo,
     Long lokSabhaSessionNo, Long rajyaSabhaSessionNo, LocalDate startDate, LocalDate endDate,
     String status);
     PlotDateChartDTO getDateChartById(Long id);
     List<PlotDateChartDTO> getAllDateCharts(String lokSabhaNo,String lokSabhaSessionNo,String rajyaSabhaNo);
     }
