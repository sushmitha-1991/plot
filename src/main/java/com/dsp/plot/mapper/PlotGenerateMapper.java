package com.dsp.plot.mapper;

import com.dsp.plot.dto.ApplicationInstanceDTO;
import com.dsp.plot.dto.ApproverDTO;
import com.dsp.plot.dto.PlotGenerateDTO;
import com.dsp.plot.dto.PlotIndexDTO;
import com.dsp.plot.dto.RequestDTO;
import com.dsp.plot.entity.PlotGenerate;
import com.dsp.plot.entity.PlotIndex;
import com.dsp.plot.entity.Request;

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
        PlotGenerateDTO dto = PlotGenerateDTO.builder()
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
        if (entity.getApproverDetails() != null) {
            String designation = "";
            if (entity.getApproverDetails().getDesignation() != null) {
                designation = entity.getApproverDetails().getDesignation().getAbbr();
            }
            dto.setApproverDTO(ApproverDTO.builder()
                    .id(entity.getApproverDetails().getId())
                    .username(entity.getApproverDetails().getFirstName() + " "
                            + entity.getApproverDetails().getLastName())
                    .designation(designation)
            .build());
        }
                
                return dto;
    }

    public static PlotIndex toEntityIndex(PlotIndexDTO dto) {
        return PlotIndex.builder()
                .id(dto.getId())
                .lokSabhaNo(dto.getLokSabhaNo())
                .lokSabhaSessionNo(dto.getLokSabhaSessionNo())
                .rajyaSabhaSessionNo(dto.getRajyaSabhaSessionNo())
                .draftFlag(dto.getDraftFlag())
                .status(dto.getStatus())
                .refNo(dto.getRefNo())
                .content(dto.getContent())
                .indexDetails(dto.getIndexDetails())
                .applicationId(dto.getApplicationId())
                .reportName(dto.getReportName())
                .build();
    }

    public static PlotIndexDTO toIndexDTO(PlotIndex entity) {
        return PlotIndexDTO.builder()
                .id(entity.getId())
                .lokSabhaNo(entity.getLokSabhaNo())
                .lokSabhaSessionNo(entity.getLokSabhaSessionNo())
                .rajyaSabhaSessionNo(entity.getRajyaSabhaSessionNo())
                .draftFlag(entity.getDraftFlag())
                .status(entity.getStatus())
                .refNo(entity.getRefNo())
                .content(entity.getContent())
                .applicationId(entity.getApplicationId())
                .reportName(entity.getReportName())
                .indexDetails(entity.getIndexDetails())
                .build();
    }

}
