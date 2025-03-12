package com.dsp.plot.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dsp.plot.dto.DocumentDTO;
import com.dsp.plot.dto.PlotGenerateDTO;
import com.dsp.plot.dto.PlotIndexDTO;
import com.dsp.plot.dto.PlotInputDTO;
@Service
public interface PlotGenerateService {

    PlotGenerateDTO save(PlotGenerateDTO plotDTO);

    List<PlotGenerateDTO> getAll(Boolean isActive, Boolean draftStatus, Long lokSabhaNo,
    Long lokSabhaSessionNo, Long rajyaSabhaSessionNo, LocalDate startDate, LocalDate endDate,
    String status,Boolean sendToLibrary);
    List<PlotGenerateDTO> getAll();

    PlotGenerateDTO getById(Long id);
    
    PlotGenerateDTO update(Long id, PlotGenerateDTO plotDTO);


    void delete(Long id);
    
	DocumentDTO getDocumentByDate(LocalDate dateOfDoc, String doc);

    List<PlotIndexDTO> getIndexes(Long lokSabhaNo,Long lokSabhaSessionNo,Long rajyaSabhaSessionNo,
    LocalDate startDate,LocalDate endDate,String status,String reportName,Long createdBy);

    PlotIndexDTO saveIndex(PlotIndexDTO dto);

    PlotIndexDTO getIndexById(Long id);

}
