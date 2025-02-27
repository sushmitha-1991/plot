package com.dsp.plot.mapper;

import com.dsp.plot.dto.ApplicationInstanceDTO;
import com.dsp.plot.dto.PlotLtnoDto;
import com.dsp.plot.entity.PlotLtno;

public interface LtnoMapper {
    public static PlotLtno toEntity(PlotLtnoDto dto) {
        return PlotLtno.builder()
        .id(dto.getId())
        .lokSabhaNo(dto.getLokSabhaNo())
        .lokSabhaSessionNo(dto.getLokSabhaSessionNo())
        .rajyaSabhaSessionNo(dto.getRajyaSabhaSessionNo())
        .paperTypeId(dto.getPaperTypeId())
        .ltnoDate(dto.getLtnoDate())
        .paperName(dto.getPaperName())
        .ministryId(dto.getMinistryId())
        .documents(dto.getDocuments())
        .status(dto.getStatus())
        .remarks(dto.getRemarks())
        .refNo(dto.getRefNo())
        .applicationId(dto.getApplicationId())
        .applicationNo(dto.getApplicationNo())
        .build();
    }

    public static PlotLtnoDto toDTO(PlotLtno entity) {
        return PlotLtnoDto.builder()
        .id(entity.getId())
        .lokSabhaNo(entity.getLokSabhaNo())
        .ltnoDate(entity.getLtnoDate())
        .lokSabhaSessionNo(entity.getLokSabhaSessionNo())
        .rajyaSabhaSessionNo(entity.getRajyaSabhaSessionNo())
        .paperTypeId(entity.getPaperTypeId())
        .paperName(entity.getPaperName())
        .ministryId(entity.getMinistryId())
        .documents(entity.getDocuments())
        .status(entity.getStatus())
        .remarks(entity.getRemarks())
        .refNo(entity.getRefNo())
        .applicationId(entity.getApplicationId())
        .applicationNo(entity.getApplicationNo())
        //application Instance
        .applicationInstanceDTO(ApplicationInstanceDTO.builder()
        .id(entity.getId())
        .refId(entity.getRefId())
        .applicationNo(entity.getApplicationNo())
        .applicationId(entity.getApplicationId())
        .currentStatus(entity.getCurrentStatus())
        .documentDate(entity.getDocumentDate())
        .lokSabhaSession(entity.getLokSabhaSession())
        .lokSabhaSessionNumber(entity.getLokSabhaSessionNumber())
        .rajyaSabhaSession(entity.getRajyaSabhaSession())
        .htmlContentEnglish(entity.getHtmlContentEnglish())
        .htmlContentHindi(entity.getHtmlContentHindi())
        .build())
        .build();
    }
}
