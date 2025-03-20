package com.dsp.plot.mapper;

import com.dsp.plot.dto.ApplicationInstanceDTO;
import com.dsp.plot.dto.MasterCodeDTO;
import com.dsp.plot.dto.MastersLokSabhaDTO;
import com.dsp.plot.dto.MastersLokSabhaSessionDTO;
import com.dsp.plot.dto.MastersRajyaSabhaSessionDTO;
import com.dsp.plot.dto.MinistriesDTO;
import com.dsp.plot.dto.PlotLaidOnTableDTO;
import com.dsp.plot.dto.PlotLtnoDto;
import com.dsp.plot.entity.MasterCode;
import com.dsp.plot.entity.PaperLaidOnTable;
import com.dsp.plot.entity.PlotLtno;

public interface LtnoMapper {
    public static PlotLtno toEntity(PlotLtnoDto dto) {
        return PlotLtno.builder()
                .id(dto.getId())
                .lokSabhaNo(dto.getLokSabhaNo())
                .noOfPages(dto.getNoOfPages())
                .lokSabhaSessionNo(dto.getLokSabhaSessionNo())
                .rajyaSabhaSessionNo(dto.getRajyaSabhaSessionNo())
                .paperType(dto.getPaperType())
                .ltnoDate(dto.getLtnoDate())
                .paperName(dto.getPaperName())
                .ministryId(dto.getMinistryId())
                .ltnoNo(dto.getLtnoNo())
                .title(dto.getTitle())
                .documents(dto.getDocuments())
                .status(dto.getStatus())
                .remarks(dto.getRemarks())
                .refNo(dto.getRefNo())
                .applicationId(dto.getApplicationId())
                .applicationNo(dto.getApplicationNo())
                .build();
        /*
         * 
         * 
         * 
         * private String ltnoNo;
         * private String title;
         * 
         */
    }

    public static PlotLtnoDto toDTO(PlotLtno entity) {
        PlotLtnoDto dto = PlotLtnoDto.builder()
                .id(entity.getId())
                .lokSabhaNo(entity.getLokSabhaNo())
                .ltnoDate(entity.getLtnoDate())
                .noOfPages(entity.getNoOfPages())
                .lokSabhaSessionNo(entity.getLokSabhaSessionNo())
                .rajyaSabhaSessionNo(entity.getRajyaSabhaSessionNo())
                .paperType(entity.getPaperType())
                .paperName(entity.getPaperName())
                .ministryId(entity.getMinistryId())
                .documents(entity.getDocuments())
                .status(entity.getStatus())
                .ltnoNo(entity.getLtnoNo())
                .title(entity.getTitle())
                .remarks(entity.getRemarks())
                .refNo(entity.getRefNo())
                .applicationId(entity.getApplicationId())
                .applicationNo(entity.getApplicationNo())

                // application Instance
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
        if (entity.getLokSabhSessionDetails() != null) {
            if (entity.getLokSabhSessionDetails().getLokSabha() != null) {
                dto.setLokSabhaDTO(MastersLokSabhaDTO.builder()
                        .id(entity.getLokSabhSessionDetails().getLokSabha().getId())
                        .lokSabhaNumber(entity.getLokSabhSessionDetails().getLokSabha().getLokSabhaNumber())
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
        if (entity.getMinistries() != null) {
            dto.setMinistries(MinistriesDTO.builder()
                    .ministryName(entity.getMinistries().getMinistryName())
                    .build());

        }
        return dto;
    }

    public static PaperLaidOnTable titleToEntity(PlotLaidOnTableDTO dto) {
        return PaperLaidOnTable.builder()
                .id(dto.getId())
                .lokSabhaNo(dto.getLokSabhaNo())
                .lokSabhaSessionNo(dto.getLokSabhaSessionNo())
                .rajyaSabhaSessionNo(dto.getRajyaSabhaSessionNo())
                .documents(dto.getDocuments())
                .plotLaidDate(dto.getPlotLaidDate())
                .ministryId(dto.getMinistryId())
                .status(dto.getStatus())
                .applicationId(dto.getApplicationId())
                .refNo(dto.getRefNo())
                .title(dto.getTitle())
                .build();

    }

    public static PlotLaidOnTableDTO titleToDTO(PaperLaidOnTable entity) {
        PlotLaidOnTableDTO dto = PlotLaidOnTableDTO.builder()
                .id(entity.getId())
                .lokSabhaNo(entity.getLokSabhaNo())
                .lokSabhaSessionNo(entity.getLokSabhaSessionNo())
                .rajyaSabhaSessionNo(entity.getRajyaSabhaSessionNo())
                .documents(entity.getDocuments())
                .plotLaidDate(entity.getPlotLaidDate())
                .ministryId(entity.getMinistryId())
                .title(entity.getTitle())
                .applicationId(entity.getApplicationId())
                .refNo(entity.getRefNo())
                .status(entity.getStatus())
                .build();
        if (entity.getMinistries() != null) {
            dto.setMinistries(MinistriesDTO.builder()
                    .ministryName(entity.getMinistries().getMinistryName())
                    .build());

        }
        if (entity.getLokSabhSessionDetails() != null) {
            if (entity.getLokSabhSessionDetails().getLokSabha() != null) {
                dto.setLokSabhaDTO(MastersLokSabhaDTO.builder()
                        .id(entity.getLokSabhSessionDetails().getLokSabha().getId())
                        .lokSabhaNumber(entity.getLokSabhSessionDetails().getLokSabha().getLokSabhaNumber())
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
}
