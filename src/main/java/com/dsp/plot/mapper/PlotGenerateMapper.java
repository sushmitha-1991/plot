package com.dsp.plot.mapper;

import com.dsp.plot.dto.ApplicationInstanceDTO;
import com.dsp.plot.dto.PlotGenerateDTO;
import com.dsp.plot.entity.PlotGenerate;

public class PlotGenerateMapper {
    public static PlotGenerate toEntity(PlotGenerateDTO dto) {
        return PlotGenerate.builder()
                .lokSabhaNo(dto.getLokSabhaNo())
                .lokSabhaSessionNo(dto.getLokSabhaSessionNo())
                .rajyaSabhaSessionNo(dto.getRajyaSabhaSessionNo())
                .plotDate(dto.getPlotDate())
                .plotTime(dto.getPlotTime())
                .approverAuthority(dto.getApproverAuthority())
                .designation(dto.getDesignation())
                .location(dto.getLocation())
                .draftFlag(dto.getDraftFlag())
                .status(dto.getStatus())
                .content(dto.getContent())
                .footNote(dto.getFootNote())
                .referenceFiles(dto.getReferenceFiles())
                .paperType(dto.getPaperType())
                .paperName(dto.getPaperName())
                .remarks(dto.getRemarks())
                .referenceFiles(dto.getReferenceFiles())
                // ApplicationInstanceDTO
                .id((dto.getId()))
                .applicationId(dto.getApplicationId())
                .applicationNo(dto.getApplicationNo())
                .currentStatus(dto.getCurrentStatus())
                .documentDate(dto.getPlotDate())
                .refId(dto.getRefId())
                .build();
    }

    public static PlotGenerateDTO toDTO(PlotGenerate entity) {
        return PlotGenerateDTO.builder()
                .lokSabhaNo(entity.getLokSabhaNo())
                .lokSabhaSessionNo(entity.getLokSabhaSessionNo())
                .rajyaSabhaSessionNo(entity.getRajyaSabhaSessionNo())
                .plotDate(entity.getPlotDate())
                .plotTime(entity.getPlotTime())
                .approverAuthority(entity.getApproverAuthority())
                .designation(entity.getDesignation())
                .location(entity.getLocation())
                .draftFlag(entity.getDraftFlag())
                .status(entity.getStatus())
                .content(entity.getContent())
                .footNote(entity.getFootNote())
                .paperType(entity.getPaperType())
                .paperName(entity.getPaperName())
                .remarks(entity.getRemarks())
                // ApplicationInstance
                .id((entity.getId()))
                .applicationId(entity.getApplicationId())
                .applicationNo(entity.getApplicationNo())
                .currentStatus(entity.getCurrentStatus())
                .refId(entity.getRefId())
                .applicationInstanceDTO(ApplicationInstanceDTO.builder()
                        .id(entity.getId())
                        .applicationId(entity.getApplicationId())
                        .applicationNo(entity.getApplicationNo())
                        .refId(entity.getRefId())
                        .currentStatus(entity.getCurrentStatus())
                        .documentDate(entity.getDocumentDate())
                        .lokSabhaSession(entity.getLokSabhaSession())
                        .lokSabhaSessionNumber(entity.getLokSabhaSessionNumber())
                        .rajyaSabhaSession(entity.getRajyaSabhaSession())
                        .build())
                .build();
    }
}
