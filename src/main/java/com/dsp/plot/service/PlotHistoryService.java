package com.dsp.plot.service;

import com.dsp.plot.dto.PlotHistoryDTO;
import java.util.List;

public interface PlotHistoryService {
    List<PlotHistoryDTO> createPlotHistory(List<PlotHistoryDTO> plotHistoryDTO);
    List<PlotHistoryDTO> getAllPlotHistories();
    List<PlotHistoryDTO> getHistories(Long plotInputId);
    PlotHistoryDTO getPlotHistoryById(Long id);
    // PlotHistoryDTO updatePlotHistory(Long id, PlotHistoryDTO plotHistoryDTO);
    // void deletePlotHistory(Long id);
}
