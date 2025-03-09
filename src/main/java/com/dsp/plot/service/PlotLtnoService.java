package com.dsp.plot.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dsp.plot.dto.PlotLaidOnTableDTO;
import com.dsp.plot.dto.PlotLtnoDto;

@Service
public interface PlotLtnoService {

        PlotLtnoDto save(PlotLtnoDto plotLtnoDto);

        List<PlotLtnoDto> getAll();

        List<PlotLtnoDto> getAll(Boolean isActive, Long lokSabhaNo, Long lokSabhaSessionNo, Long rajyaSabhaSessionNo,
                        LocalDate startDate, LocalDate endDate, String status, Long ministryId, String ltnoNo,Long paperTypeId);

        PlotLtnoDto getById(Long id);

        PlotLtnoDto update(Long id, PlotLtnoDto plotLtnoDto);

        void delete(Long id);

        PlotLaidOnTableDTO getTitleById(Long id);

        PlotLaidOnTableDTO updateTitle(Long id, PlotLaidOnTableDTO plotDto);

        List<PlotLaidOnTableDTO> getTitles(Boolean isActive, Long lokSabhaNo, Long lokSabhaSessionNo,
                        Long rajyaSabhaSessionNo, LocalDate startDate, LocalDate endDate, String status,
                        Long ministryId);

        PlotLaidOnTableDTO saveTitle(PlotLaidOnTableDTO plotLaidOnTableDTO);

}
