package com.dsp.plot.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dsp.plot.dto.PlotHistoryDTO;
import com.dsp.plot.entity.PlotHistory;
import com.dsp.plot.mapper.PlotHistoryMapper;
import com.dsp.plot.repository.PlotHistoryRepository;
import com.dsp.plot.service.PlotHistoryService;

@Service
public class PlotHistoryServiceImpl implements PlotHistoryService {

    @Autowired
    private PlotHistoryRepository plotHistoryRepository;
    @Override
    public List<PlotHistoryDTO> createPlotHistory(List<PlotHistoryDTO> plotHistoryDTO) {
        List<PlotHistory> entities = plotHistoryDTO.stream()
                .map(PlotHistoryMapper::toEntity)
                .collect(Collectors.toList());
        // Save all entities
        List<PlotHistory> savedEntities = plotHistoryRepository.saveAll(entities);
        // Convert List of saved entities back to List of DTOs
        return savedEntities.stream()
                .map(PlotHistoryMapper::toDTO)
                .collect(Collectors.toList());
        // PlotHistory entity = mapper.dtoToEntity(plotHistoryDTO);
        // PlotHistory savedEntity = plotHistoryRepository.save(entity);
        // return mapper.entityToDto(savedEntity);
    }

    @Override
    public List<PlotHistoryDTO> getAllPlotHistories() {
        return plotHistoryRepository.findAll()
                .stream()
                .map(PlotHistoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlotHistoryDTO> getHistories(Long plotInputId) {
        return plotHistoryRepository.findAllByPlotInputId(plotInputId)
                .stream()
                .map(PlotHistoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PlotHistoryDTO getPlotHistoryById(Long id) {
        Optional<PlotHistory> optional = plotHistoryRepository.findById(id);
        return optional.map(PlotHistoryMapper::toDTO).orElse(null);
    }

    // @Override
    // public PlotHistoryDTO updatePlotHistory(Long id, PlotHistoryDTO
    // plotHistoryDTO) {
    // Optional<PlotHistory> optional = plotHistoryRepository.findById(id);
    // if (optional.isPresent()) {
    // PlotHistory existingEntity = optional.get();
    // existingEntity.setAddedText(plotHistoryDTO.getAddedText());
    // existingEntity.setUpdatedBy(plotHistoryDTO.getUpdatedBy());
    // existingEntity.setUpdatedAt(plotHistoryDTO.getUpdatedAt());
    // PlotHistory updatedEntity = plotHistoryRepository.save(existingEntity);
    // return mapper.entityToDto(updatedEntity);
    // }
    // return null;
    // }

    // @Override
    // public void deletePlotHistory(Long id) {
    // plotHistoryRepository.deleteById(id);
    // }
}
