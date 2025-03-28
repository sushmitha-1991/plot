package com.dsp.plot.mapper;

import com.dsp.plot.dto.ApproverDTO;
import com.dsp.plot.dto.DepartmentDTO;
import com.dsp.plot.dto.InputInstanceDTO;
import com.dsp.plot.dto.MasterCodeDTO;
import com.dsp.plot.dto.MastersLokSabhaDTO;
import com.dsp.plot.dto.MastersLokSabhaSessionDTO;
import com.dsp.plot.dto.MastersRajyaSabhaSessionDTO;
import com.dsp.plot.dto.MinistriesDTO;
import com.dsp.plot.dto.MinistryAuthorityDTO;
import com.dsp.plot.dto.PlotDateChartDTO;
import com.dsp.plot.dto.PlotInputDTO;
import com.dsp.plot.entity.PlotDateChart;
import com.dsp.plot.entity.PlotInput;

public class PlotInputMapper {

        public static PlotInput toEntity(PlotInputDTO dto) {
                return PlotInput.builder()
                                .id(dto.getId())
                                .ministryId(dto.getMinistryId())
                                .departmentId(dto.getDepartmentId())
                                .submissionDate(dto.getSubmissionDate())
                                .prevPaperLaidDate(dto.getPrevPaperLaidDate())
                                .authenticatedById(dto.getAuthenticatedById())
                                .digitallySigned(dto.getDigitallySigned())
                                .actId(dto.getActId())
                                .paperTypeId(dto.getPaperTypeId())
                                .sendToLibrary(dto.getSendToLibrary())
                                .draftFlag(dto.getDraftFlag())
                                .status(dto.getStatus())
                                .refNo(dto.getRefNo())
                                .remarks(dto.getRemarks())
                                .paperDetails(dto.getPaperDetails())
                                .documents(dto.getDocuments())
                                .generatedPlotId(dto.getGeneratePlotId())
                                .proposedDateOfLaying(dto.getProposedDateOfLaying())
                                .actualDateOfLaying(dto.getActualDateOfLaying())
                                .nodalOfficerId(dto.getNodalOfficerId())
                                .nodalOfficerDesignation(dto.getNodalOfficerDesignation())
                                .nodalOfficerMobileNo(dto.getNodalOfficerMobileNo())
                                .nodalOfficerPhoneNo(dto.getNodalOfficerPhoneNo())
                                .content(dto.getContent())
                                .lokSabhaNo(dto.getLokSabhaNo())
                                .lokSabhaSessionNo(dto.getLokSabhaSessionNo())
                                .rajyaSabhaSessionNo(dto.getRajyaSabhaSessionNo())
                                .applicationId(dto.getApplicationId())
                                .applicationNo(dto.getApplicationNo())
                                .currentStatus(dto.getCurrentStatus())

                                .refId(dto.getRefId())
                                .documentDate(dto.getDocumentDate())
                                .paperName(dto.getPaperName()).build();
                // .paperTiltle(dto.getPaperTiltle())
                // .article(dto.getArticle())
                // .placeOfPublished(dto.getPlaceOfPublished())
                // .actualDateOfLaid(dto.getActualDateOfLaid())
                // .section(dto.getSection())
                // .subSection(dto.getSubSection())
                // .constitutionalBody(dto.getConstitutionalBody())
                // .headQuaters(dto.getHeadQuaters())
                // .year(dto.getYear())
                // .content(dto.getContent())
                // .paperPresentInRS(dto.getPaperPresentInRS())
                // .dateOfRS(dto.getDateOfRS())
                // .informedDate(dto.getInformedDate())
                // .isModifiedByHouse(dto.getIsModifiedByHouse())
                // .actAndSection(dto.getActAndSection())
                // .build();
        }

        public static PlotInputDTO toDTO(PlotInput entity) {
               
                PlotInputDTO dto = PlotInputDTO.builder()
                                .id(entity.getId())
                                .ministryId(entity.getMinistryId())
                                .departmentId(entity.getDepartmentId())
                                .submissionDate(entity.getSubmissionDate())
                                .prevPaperLaidDate(entity.getPrevPaperLaidDate())
                                .authenticatedById(entity.getAuthenticatedById())
                                .digitallySigned(entity.getDigitallySigned())
                                .actId(entity.getActId())
                                .paperTypeId(entity.getPaperTypeId())
                                .content(entity.getContent())
                                .draftFlag(entity.getDraftFlag())
                                .status(entity.getStatus())
                                .refNo(entity.getRefNo())
                                .sendToLibrary(entity.getSendToLibrary())
                                .remarks(entity.getRemarks())
                                .paperDetails(entity.getPaperDetails())
                                .documents(entity.getDocuments())
                                .proposedDateOfLaying(entity.getProposedDateOfLaying())
                                .actualDateOfLaying(entity.getActualDateOfLaying())
                                .nodalOfficerId(entity.getNodalOfficerId())
                                .nodalOfficerDesignation(entity.getNodalOfficerDesignation())
                                .nodalOfficerMobileNo(entity.getNodalOfficerMobileNo())
                                .nodalOfficerPhoneNo(entity.getNodalOfficerPhoneNo())
                                .lokSabhaNo(entity.getLokSabhaNo())
                                .lokSabhaSessionNo(entity.getLokSabhaSessionNo())
                                .rajyaSabhaSessionNo(entity.getRajyaSabhaSessionNo())
                                .paperName(entity.getPaperName())
                                .generatePlotId(entity.getGeneratedPlotId())
                                .applicationId(entity.getApplicationId())
                                .applicationNo(entity.getApplicationNo())
                                .currentStatus(entity.getCurrentStatus())
                                .refId(entity.getRefId())
                                .documentDate(entity.getDocumentDate())
                                .inputInstanceDTO(InputInstanceDTO.builder()
                                                .id(entity.getId())
                                                .createdAt(entity.getCreatedAt())
                                                .applicationId(entity.getApplicationId())
                                                .applicationNo(entity.getApplicationNo())
                                                .currentStatus(entity.getCurrentStatus())
                                                .refId(entity.getRefId())
                                                .documentDate(entity.getDocumentDate())
                                                .pendingAt(entity.getPendingAt())
                                                .lokSabhaSession(entity.getLokSabhaSession())
                                                .lokSabhaSessionNumber(entity.getLokSabhaSessionNumber())
                                                .rajyaSabhaSession(entity.getRajyaSabhaSession())
                                                .htmlContentEnglish(entity.getHtmlContentEnglish())
                                                .htmlContentHindi(entity.getHtmlContentHindi())
                                                .applicationInstanceId(entity.getApplicationInstanceId())
                                                .build())
                                .build();
                if (entity.getLokSabhaDetails() != null) {
                        dto.setLokSabhaDTO(MastersLokSabhaDTO.builder()
                                        .id(entity.getLokSabhaDetails().getId())
                                        .lokSabhaNumber(entity.getLokSabhaDetails().getLokSabhaNumber())
                                        .build());
                }
                if (entity.getLokSabhaSessionDetails() != null) {

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
                if (entity.getDepartments() != null) {
                        dto.setDepartmentName(entity.getDepartments().getDepartmentName());
                        dto.setDepartmentCode(entity.getDepartments().getDepartmentCode());
                        dto.setDepartments(DepartmentDTO.builder()
                                        .departmentName(entity.getDepartments().getDepartmentName())
                                        .departmentCode(entity.getDepartments().getDepartmentCode())
                                        .abbr(entity.getDepartments().getAbbr())
                                        .build());
                }
                if (entity.getMinistries() != null) {
                        dto.setMinisties(MinistriesDTO.builder()
                                        .ministryName(entity.getMinistries().getMinistryName())
                                        .build());
                        dto.setMinistryName(entity.getMinistries().getMinistryName());
                }
                if (entity.getPaperTypes() != null) {
                        dto.setPaperTypes(MasterCodeDTO.builder()
                                        .abbr(entity.getPaperTypes().getAbbr())
                                        .description(entity.getPaperTypes().getDescription())
                                        .code(entity.getPaperTypes().getCode())
                                        .displaySequence(entity.getPaperTypes().getDisplaySequence())
                                        .build());
                }
                if (entity.getAuthenticatorDetails() != null) {
                        dto.setAuthenticatorName(entity.getAuthenticatorDetails().getAuthorityName());
                        dto.setAuthenticatorDTO(MinistryAuthorityDTO.builder()
                                        .ministryCode(entity.getAuthenticatorDetails().getMinistryCode())
                                        .id(entity.getAuthenticatorDetails().getId())
                                        .ministryId(entity.getAuthenticatorDetails().getMinistryId())
                                        .authorityName(entity.getAuthenticatorDetails().getAuthorityName())
                                        .build());
                }
                if (entity.getNodalOfficerDetails() != null) {
                        dto.setNodalOfficerName(entity.getNodalOfficerDetails().getFirstName() + " "
                                        + entity.getNodalOfficerDetails().getMiddleName() + " "
                                        + entity.getNodalOfficerDetails().getLastName());
                        if (entity.getNodalOfficerDetails().getDesignation() != null) {
                                dto.setNodalOfficerDesignation(
                                                entity.getNodalOfficerDetails().getDesignation().getAbbr());
                        }
                        dto.setNodalOfficerDTO(ApproverDTO.builder()
                                        .id(entity.getNodalOfficerDetails().getId())
                                        .username(entity.getNodalOfficerDetails().getFirstName()
                                                        + " " + entity.getNodalOfficerDetails().getMiddleName()
                                                        + " " + entity.getNodalOfficerDetails().getLastName())
                                        .build());
                }

                return dto;
                // .paperTiltle(entity.getPaperTiltle())
                // .article(entity.getArticle())
                // .placeOfPublished(entity.getPlaceOfPublished())
                // .actualDateOfLaid(entity.getActualDateOfLaid())
                // .section(entity.getSection())
                // .subSection(entity.getSubSection())
                // .constitutionalBody(entity.getConstitutionalBody())
                // .headQuaters(entity.getHeadQuaters())
                // .year(entity.getYear())
                // .content(entity.getContent())
                // .paperPresentInRS(entity.getPaperPresentInRS())
                // .dateOfRS(entity.getDateOfRS())
                // .informedDate(entity.getInformedDate())
                // .isModifiedByHouse(entity.getIsModifiedByHouse())
                // .actAndSection(entity.getActAndSection())
                // .build();
        }

        public static PlotDateChartDTO dateChartMapToDTO(PlotDateChart entity) {
                PlotDateChartDTO dto = PlotDateChartDTO.builder()
                                .id(entity.getId())
                                .lokSabhaNo(entity.getLokSabhaNo())
                                .lokSabhaSessionNo(entity.getLokSabhaSessionNo())
                                .rajyaSabhaSessionNo(entity.getRajyaSabhaSessionNo())
                                .ministryId(entity.getMinistryId())
                                .groupName(entity.getGroupName())
                                .dates(entity.getDates())
                                .build();
                if (entity.getMinistries() != null) {
                        dto.setMinisties(MinistriesDTO.builder()
                                        .ministryName(entity.getMinistries().getMinistryName())
                                        .build());
                }

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

        public static PlotDateChart dateChartMapToEntity(PlotDateChartDTO dto) {
                return PlotDateChart.builder()
                                .id(dto.getId())
                                .lokSabhaNo(dto.getLokSabhaNo())
                                .lokSabhaSessionNo(dto.getLokSabhaSessionNo())
                                .rajyaSabhaSessionNo(dto.getRajyaSabhaSessionNo())
                                .ministryId(dto.getMinistryId())
                                .groupName(dto.getGroupName())
                                .dates(dto.getDates())
                                .build();
        }
       
}
