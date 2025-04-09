package com.dsp.plot.mapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.dsp.plot.dto.ApproverDTO;
import com.dsp.plot.dto.DepartmentDTO;
import com.dsp.plot.dto.DraftDataDTO;
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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PlotInputMapper {

        private static final ObjectMapper objectMapper = new ObjectMapper();

        public static PlotInput toEntity(PlotInputDTO dto) {
                return PlotInput.builder()
                                .id(dto.getId())
                                .ministryId(dto.getMinistryId())
                                .departmentId(dto.getDepartmentId())
                                .submissionDate(dto.getSubmissionDate())
                                .prevPaperLaidDate(dto.getPrevPaperLaidDate())
                                .authenticatedById(dto.getAuthenticatedById())
                                .digitallySigned(dto.getDigitallySigned())
                                .underAct(dto.getUnderAct())
                                .act(dto.getAct())
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
                                .DateOnWhichforwardedToLSSecretariat(dto.getDateOnWhichforwardedToLSSecretariat())
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
                                .act(entity.getAct())
                                .underAct(entity.getUnderAct())
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
                                .DateOnWhichforwardedToLSSecretariat(entity.getDateOnWhichforwardedToLSSecretariat())
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

        public static PlotInput mapDraftToEntity(DraftDataDTO dto, Long lokSabhaNo, Long lokSabhaSessionNo,
                        Long rajyaSabhaSessionNo, LocalDate startDate, LocalDate endDate, String status,
                        LocalDate submissionDate, Long department, Long paperType, String paperName,
                        LocalDate proposedDateOfLaying, LocalDate actualDateOfLaying, Integer ministry) {
                // TODO Auto-generated method stub
                if (dto == null) {
                        return null;
                }
                if (status != null && !status.equalsIgnoreCase("draft")) {
                        return null;
                }
                PlotInput entity = PlotInput.builder()
                                .applicationId(dto.getApplicationId())
                                .applicationNo(dto.getApplicationNo())
                                .id(dto.getDraftId())
                                .build();
                if (dto.getData() == null || dto.getData().isNull()) {
                        return entity;
                } else {
                        JsonNode jsonData = dto.getData(); // JsonNode
                        Map<String, Object> dataMap = convertToEntityAttribute(jsonData);
                        System.out.println("filters" + status + "::::::submissionDate:::" + submissionDate
                                        + "::::department::" + department + ":::::ministry::::::" + ministry);
                        if (lokSabhaNo != null && !lokSabhaNo.equals(getLongValue(dataMap, "lokSabhaNo")))
                                return null;
                        if (status != null && !status.equalsIgnoreCase(getStringValue(dataMap, "status")))
                                return null;
                        if (lokSabhaSessionNo != null && !lokSabhaSessionNo
                                        .equals(getLongValue(dataMap, "lokSabhaSessionNo")))
                                return null;
                        if (rajyaSabhaSessionNo != null
                                        && !rajyaSabhaSessionNo.equals(getLongValue(dataMap, "rajyaSabhaSessionNo")))
                                return null;
                        if (submissionDate != null && !submissionDate
                                        .isEqual(getLocalDateValue(dataMap, "submissionDate")))
                                return null;
                        if (department != null
                                        && !department.equals(getLongValue(dataMap, "departmentId")))
                                return null;
                        if (paperType != null && !paperType.equals(getLongValue(dataMap, "paperTypeId")))
                                return null;
                        if (paperName != null && !((getStringValue(dataMap, "paperName").toLowerCase())
                                        .contains(paperName.toLowerCase())))
                                return null;
                        if (ministry != null && !ministry.equals(getIntegerValue(dataMap, "ministryId")))
                                return null;
                        if (proposedDateOfLaying != null && !proposedDateOfLaying
                                        .isEqual(getLocalDateValue(dataMap, "proposedDateOfLaying")))
                                return null;
                        if (actualDateOfLaying != null && !actualDateOfLaying
                                        .isEqual(getLocalDateValue(dataMap, "actualDateOfLaying")))
                                return null; // Return empty if any validation fails
                        entity.setLokSabhaNo(getLongValue(dataMap, "lokSabhaNo"));
                        entity.setLokSabhaSessionNo(getLongValue(dataMap, "lokSabhaSessionNo"));
                        entity.setRajyaSabhaSessionNo(getLongValue(dataMap, "rajyaSabhaSessionNo"));
                        entity.setSubmissionDate(getLocalDateValue(dataMap, "submissionDate"));
                        entity.setDepartmentId(getLongValue(dataMap, "departmentId"));
                        entity.setAuthenticatedById(getLongValue(dataMap, "authenticatedById"));
                        entity.setPaperTypeId(getLongValue(dataMap, "paperTypeId"));
                        entity.setPaperName(getStringValue(dataMap, "paperName"));
                        entity.setMinistryId(getIntegerValue(dataMap, "ministryId"));
                        entity.setGeneratedPlotId(getLongValue(dataMap, "generatedPlotId"));
                        entity.setContent(getStringValue(dataMap, "content"));
                        entity.setDigitallySigned(getBooleanValue(dataMap, "digitallySigned"));
                        entity.setAct(getStringValue(dataMap, "act"));
                        entity.setUnderAct(getStringValue(dataMap, "underAct"));
                        entity.setDraftFlag(getBooleanValue(dataMap, "draftFlag"));
                        entity.setDateOnWhichforwardedToLSSecretariat(getLocalDateValue(dataMap,"DateOnWhichforwardedToLSSecretariat"));
                        entity.setStatus(getStringValue(dataMap, "status"));
                        entity.setRemarks(getStringValue(dataMap, "remarks"));
                        entity.setRefNo(dto.getApplicationNo());
                        entity.setProposedDateOfLaying(getLocalDateValue(dataMap, "proposedDateOfLaying"));
                        entity.setActualDateOfLaying(getLocalDateValue(dataMap, "actualDateOfLaying"));
                        entity.setPrevPaperLaidDate(getLocalDateValue(dataMap, "prevPaperLaidDate"));
                        entity.setNodalOfficerId(getLongValue(dataMap, "nodalOfficerId"));
                        entity.setNodalOfficerDesignation(getStringValue(dataMap, "nodalOfficerDesignation"));
                        entity.setNodalOfficerPhoneNo(getStringValue(dataMap, "nodalOfficerPhoneNo"));
                        entity.setSendToLibrary(getBooleanValue(dataMap, "sendToLibrary"));
                        entity.setNodalOfficerMobileNo(getStringValue(dataMap, "nodalOfficerMobileNo"));
                        List<Map<String, Object>> paperDetailsList = getListOfMapValue(dataMap, "paperDetails");
                        if (paperDetailsList != null) {
                                entity.setPaperDetails(paperDetailsList);
                        }
                        List<Map<String, Object>> documents = getListOfMapValue(dataMap, "documents");
                        if (documents != null) {
                                entity.setDocuments(documents);
                        }

                }
                return entity;
        }

        private static List<Map<String, Object>> getListOfMapValue(Map<String, Object> map, String key) {
                Object value = map.get(key);
                if (value instanceof List) {
                        return (List<Map<String, Object>>) value;
                }
                return new ArrayList<>();
        }

        private static Long getLongValue(Map<String, Object> map, String key) {
                return Optional.ofNullable(map.get(key))
                                .filter(value -> value instanceof Number)
                                .map(value -> ((Number) value).longValue())
                                .orElse(null);
        }

        private static Integer getIntegerValue(Map<String, Object> map, String key) {
                return Optional.ofNullable(map.get(key))
                                .filter(value -> value instanceof Number)
                                .map(value -> ((Number) value).intValue()) // Use intValue() instead
                                .orElse(null);
        }

        private static String getStringValue(Map<String, Object> map, String key) {
                return Optional.ofNullable(map.get(key))
                                .map(Object::toString)
                                .orElse(null);
        }

        private static Boolean getBooleanValue(Map<String, Object> map, String key) {
                return Optional.ofNullable(map.get(key))
                                .filter(value -> value instanceof Boolean)
                                .map(value -> (Boolean) value)
                                .orElse(null);
        }

        private static LocalDate getLocalDateValue(Map<String, Object> map, String key) {
                // return Optional.ofNullable(map.get(key))
                // .map(value -> {
                // if (value instanceof LocalDate) {
                // return (LocalDate) value;
                // } else if (value instanceof String) {
                // return LocalDate.parse((String) value);
                // }
                // return null;
                // })
                // .orElse(null);

                return Optional.ofNullable(map.get(key))
                                .map(value -> {
                                        try {
                                                if (value instanceof LocalDate) {
                                                        return (LocalDate) value;
                                                } else if (value instanceof String) {
                                                        return LocalDate.parse((String) value);
                                                }
                                        } catch (Exception e) {
                                                System.err.println("Error parsing date for key: " + key + " - "
                                                                + e.getMessage());
                                        }
                                        return null;
                                })
                                .orElse(null);
        }

        public static Map<String, Object> convertToEntityAttribute(JsonNode jsonNode) {
                if (jsonNode == null || jsonNode.isNull()) {
                        return new HashMap<>(); // Return an empty map if JsonNode is null
                }
                return objectMapper.convertValue(jsonNode, Map.class);
        }

}
