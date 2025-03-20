package com.dsp.plot.mapper;

import com.dsp.plot.dto.ApplicationInstanceDTO;
import com.dsp.plot.dto.ApproverDTO;
import com.dsp.plot.dto.MastersLokSabhaDTO;
import com.dsp.plot.dto.MastersLokSabhaSessionDTO;
import com.dsp.plot.dto.MastersRajyaSabhaSessionDTO;
import com.dsp.plot.dto.PlotGenerateDTO;
import com.dsp.plot.dto.PlotIndexDTO;
import com.dsp.plot.entity.PlotGenerate;
import com.dsp.plot.entity.PlotIndex;

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
                                .sendToLibrary(dto.getSendToLibrary())
                                .referenceFiles(dto.getReferenceFiles())
                                .plotParentId(dto.getPlotParentId())
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
                                .sendToLibrary(entity.getSendToLibrary())
                                .draftFlag(entity.getDraftFlag())
                                .status(entity.getStatus())
                                .content(entity.getContent())
                                .footNote(entity.getFootNote())
                                .paperType(entity.getPaperType())
                                .paperName(entity.getPaperName())
                                .remarks(entity.getRemarks())
                                // ApplicationInstance
                                .id((entity.getId()))
                                .plotParentId(entity.getPlotParentId())
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
                                                .htmlContentEnglish(entity.getHtmlContentEnglish())
                                                .htmlContentHindi(entity.getHtmlContentHindi())
                                                .parentInstanceId(entity.getParentInstanceId())
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
                if (entity.getLokSabhSessionDetails() != null) {
                        if (entity.getLokSabhSessionDetails().getLokSabha() != null) {
                                dto.setLokSabhaDTO(MastersLokSabhaDTO.builder()
                                                .id(entity.getLokSabhSessionDetails().getLokSabha().getId())
                                                .lokSabhaNumber(entity.getLokSabhSessionDetails().getLokSabha()
                                                                .getLokSabhaNumber())
                                                .build());
                        }
                        dto.setLokSabhaSessionDTO(MastersLokSabhaSessionDTO.builder()
                                        .id(entity.getLokSabhSessionDetails().getId())
                                        .sessionNumber(entity.getLokSabhSessionDetails().getSessionNumber())
                                        .build());
                }
                if (entity.getRajyaSabhaSessionDetails() != null) {
                        dto.setRajyaSabhaSessionDTO(MastersRajyaSabhaSessionDTO.builder()
                                        .id(entity.getRajyaSabhaSessionDetails().getId())
                                        .sessionNumber(entity.getRajyaSabhaSessionDetails().getSessionNumber())
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
                PlotIndexDTO dto = PlotIndexDTO.builder()
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
                                .createdById(entity.getCreatedBy())
                                .build();
                if (entity.getLokSabhaSessionDetails() != null) {
                        if (entity.getLokSabhaSessionDetails().getLokSabha() != null) {
                                dto.setLokSabhaDTO(MastersLokSabhaDTO.builder()
                                                .id(entity.getLokSabhaSessionDetails().getLokSabha().getId())
                                                .lokSabhaNumber(entity.getLokSabhaSessionDetails().getLokSabha()
                                                                .getLokSabhaNumber())
                                                .build());
                        }
                        dto.setLokSabhaSessionDTO(MastersLokSabhaSessionDTO.builder()
                                        .id(entity.getLokSabhaSessionDetails().getId())
                                        .sessionNumber(entity.getLokSabhaSessionDetails().getSessionNumber())
                                        .build());
                }
                if (entity.getRajyaSabhaSessionDetails() != null) {
                        dto.setRajyaSabhaSessionDTO(MastersRajyaSabhaSessionDTO.builder()
                                        .id(entity.getRajyaSabhaSessionDetails().getId())
                                        .sessionNumber(entity.getRajyaSabhaSessionDetails().getSessionNumber())
                                        .build());
                }

                return dto;
        }

}
