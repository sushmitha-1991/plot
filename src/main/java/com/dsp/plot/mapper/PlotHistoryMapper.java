package com.dsp.plot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.dsp.plot.dto.PlotHistoryDTO;
import com.dsp.plot.dto.RequestDTO;
import com.dsp.plot.entity.PlotHistory;
import com.dsp.plot.entity.Request;


public class PlotHistoryMapper {

public static PlotHistory toEntity(PlotHistoryDTO dto) {
        return PlotHistory.builder()
                .id(dto.getId())
                .plotInputId(dto.getPlotInputId())
                .ministryId(dto.getMinistryId())
                .status(dto.getStatus())
                .addedText(dto.getAddedText())
                .build();
    }

    public static PlotHistoryDTO toDTO(PlotHistory entity) {
        return PlotHistoryDTO.builder()
        .id(entity.getId())
        .plotInputId(entity.getPlotInputId())
        .ministryId(entity.getMinistryId())
        .status(entity.getStatus())
        .addedText(entity.getAddedText())
        .build();
    }

}