package com.dsp.plot.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dsp.plot.dto.DocumentDTO;
import com.dsp.plot.dto.PlotGenerateDTO;
@Service
public interface PlotGenerateService {

    PlotGenerateDTO save(PlotGenerateDTO plotDTO);

    List<PlotGenerateDTO> getAll();

    PlotGenerateDTO getById(Long id);
    
    PlotGenerateDTO update(Long id, PlotGenerateDTO plotDTO);

    void delete(Long id);
    
	DocumentDTO getDocumentByDate(LocalDate dateOfDoc, String doc);
}
