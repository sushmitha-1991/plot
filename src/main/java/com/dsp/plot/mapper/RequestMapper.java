package com.dsp.plot.mapper;

import com.dsp.plot.dto.RequestDTO;
import com.dsp.plot.entity.Request;

public class RequestMapper {
    public static Request toEntity(RequestDTO dto) {
        return Request.builder()
                .id(dto.getId())
                .plotInputId(dto.getPlotInputId())
                .request(dto.getRequest())

                .build();
    }

    public static RequestDTO toDTO(Request entity) {
        return RequestDTO.builder()
                .id(entity.getId())
                .plotInputId(entity.getPlotInputId())
                .request(entity.getRequest())
                .createdAt(entity.getCreatedAt())
                .createdAtWithOutTime(entity.getCreatedAt().toLocalDate())
                .build();
    }
}
