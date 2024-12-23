package com.dsp.plot.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dsp.plot.dto.DocumentDTO;
import com.dsp.plot.dto.PlotInputDTO;
import com.dsp.plot.dto.RequestDTO;
@Service
public interface PlotInputService {

    PlotInputDTO save(PlotInputDTO plotinputDTO);

    List<PlotInputDTO> getAll();

    PlotInputDTO getById(Long id);
    
    PlotInputDTO update(Long id, PlotInputDTO plotinputDTO);

    void delete(Long id);

    RequestDTO saveRequest(RequestDTO dto);
     
    List<RequestDTO> getAllRequests();
        // DocumentDTO getDocumentByDate(LocalDate dateOfDoc, String doc);
     
     }
