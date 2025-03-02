package com.dsp.plot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.dsp.plot.dto.PlotHistoryDTO;
import com.dsp.plot.entity.PlotHistory;

@Mapper
public interface PlotHistoryMapper {

    PlotHistoryMapper INSTANCE = Mappers.getMapper(PlotHistoryMapper.class);

    PlotHistoryDTO entityToDto(PlotHistory plotHistory);

    PlotHistory dtoToEntity(PlotHistoryDTO plotHistoryDTO);
}