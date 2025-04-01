package com.dsp.plot.service.impl;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.dsp.plot.dto.DraftDataDTO;
import com.dsp.plot.dto.PlotDateChartDTO;
import com.dsp.plot.dto.PlotInputDTO;
import com.dsp.plot.dto.QuesBranchApiDto;
import com.dsp.plot.dto.QuesBranchApiDtoRS;
import com.dsp.plot.dto.RequestDTO;
import com.dsp.plot.dto.SerialNumberRequest;
import com.dsp.plot.dto.SerialNumberResponse;
import com.dsp.plot.dto.UserDetailsDTO;
import com.dsp.plot.entity.BaseUser;
import com.dsp.plot.entity.Departments;
import com.dsp.plot.entity.MasterCode;
import com.dsp.plot.entity.MastersLokSabhaSession;
import com.dsp.plot.entity.MastersRajyaSabha;
import com.dsp.plot.entity.Ministries;
import com.dsp.plot.entity.MinistryAuthority;
import com.dsp.plot.entity.PlotDateChart;
import com.dsp.plot.entity.PlotGenerate;
import com.dsp.plot.entity.PlotHistory;
import com.dsp.plot.entity.PlotInput;
import com.dsp.plot.entity.Request;
import com.dsp.plot.util.JwtUtil;
import com.dsp.plot.entity.specification.DateChartSpec;
import com.dsp.plot.entity.specification.PlotGenerateSpec;
import com.dsp.plot.entity.specification.PlotInputSpec;
import com.dsp.plot.exception.CustomDatabaseException;
import com.dsp.plot.exception.ResourceNotFoundException;
import com.dsp.plot.mapper.PlotInputMapper;
import com.dsp.plot.mapper.RequestMapper;
import com.dsp.plot.repository.AuthorityRepository;
import com.dsp.plot.repository.CodeRepository;
import com.dsp.plot.repository.DepartmentRepository;
import com.dsp.plot.repository.MasterLSSRepository;
import com.dsp.plot.repository.MastersRSSRepository;
import com.dsp.plot.repository.MinistriesRepository;
import com.dsp.plot.repository.MyUserRepository;
import com.dsp.plot.repository.PlotDateChartRepository;
import com.dsp.plot.repository.PlotGenerateRepository;
import com.dsp.plot.repository.PlotHistoryRepository;
import com.dsp.plot.repository.PlotInputRepository;
import com.dsp.plot.repository.RequestRepository;
import com.dsp.plot.response.GenericResponse;
import com.dsp.plot.service.PlotInputService;
import com.dsp.plot.util.UserUtils;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PlotInputServiceImpl implements PlotInputService {

    @Value("${master.service.url}")
    private String masterServiceUrl;
    @Value("${workflow.service.url}")
    private String draftUrl;
    @Autowired
    private HttpServletRequest httpRequest;
    @Autowired
    private UserUtils utils;
    @Autowired
    private PlotGenerateRepository repository;
    @Autowired
    private MastersRSSRepository mastersRSSRepository;
    @Autowired
    private CodeRepository codeRepository;
    @Autowired
    private MinistriesRepository ministriesRepository;
    @Autowired
    private MasterLSSRepository masterLSSRepository;

    @Autowired
    private PlotDateChartRepository plotDateChartRepository;
    @Autowired
    private PlotHistoryRepository plotHistoryRepository;
    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private MyUserRepository userRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${feign.client.bearer.url}")
    private String sansadUrl;
    // @Autowired
    // private BearerClient bearerClient;
    private static final Logger log = LoggerFactory.getLogger(PlotInputServiceImpl.class);
    // private final PlotInputMapper mapper;

    @Autowired
    private PlotInputRepository plotinputRepository;

    @Autowired
    private DepartmentRepository departmentRepo;
    @Autowired

    private AuthorityRepository authorityRepository;

    @Transactional
    @Override
    public PlotInputDTO save(PlotInputDTO plotinputDTO) {
        if (plotinputDTO.getStatus() == null) {
            plotinputDTO.setStatus("Submitted");
        }
        log.debug("Entering savePlotInput with PlotInputDTO: {}", plotinputDTO);
        if (plotinputDTO.getSubmissionDate() != null && plotinputDTO.getProposedDateOfLaying() != null) {
            long daysDifference = ChronoUnit.DAYS.between(plotinputDTO.getSubmissionDate(),
                    plotinputDTO.getProposedDateOfLaying());
            if (daysDifference < 2) {
                log.debug("Date of Submission must be at least 2 days before the Proposed Date Of Lying", plotinputDTO);
                // log.info("Date of Submission must be at least 2 days before the Proposed Date
                // Of Lying", plotinputDTO);
                throw new IllegalArgumentException(
                        "Date of Submission must be at least 2 days before the Proposed Date Of Lying.");
            }
        }
        try {
            // Convert DTO to entity
            PlotInput entity = PlotInputMapper.toEntity(plotinputDTO);
            log.debug("Converted PlotInputDTO to entity: {}", entity);
            // Save PlotInput entity
            SerialNumberRequest serialNumberRequest = new SerialNumberRequest();
            serialNumberRequest.setAppId(entity.getApplicationId());
            Long houseId = getHouseId();
            if (houseId == 1) {
                if (entity.getRajyaSabhaSessionNo() == null) {
                    Long rajyaSabhaSession = mastersRSSRepository.findLatestByCreatedBy();
                    entity.setRajyaSabhaSessionNo(rajyaSabhaSession);
                }
                Optional<MastersRajyaSabha> optionalSession = mastersRSSRepository
                        .findById(entity.getRajyaSabhaSessionNo());
                if (optionalSession == null) {
                    throw new RuntimeException(
                            "Session No is not present in Session List");
                }
                MastersRajyaSabha session = optionalSession.get();
                entity.setRajyaSabhaSession(session.getSessionNumber());
                entity.setLokSabhaSessionNo(null);
                entity.setLokSabhaNo(null);
                entity.setPendingAt("Table Office");
                serialNumberRequest.setSessionId(entity.getRajyaSabhaSessionNo());
            } else if (houseId == 2) {
                if (entity.getLokSabhaSessionNo() == null) {
                    Long lokSabhaSession = masterLSSRepository.findLatestByCreatedBy();
                    entity.setLokSabhaSessionNo(lokSabhaSession);
                }
                Optional<MastersLokSabhaSession> optionalSession = masterLSSRepository
                        .findById(entity.getLokSabhaSessionNo());
                if (optionalSession == null) {
                    throw new RuntimeException(
                            "Session No is not present in Session List");
                }
                MastersLokSabhaSession session = optionalSession.get();
                entity.setLokSabhaSessionNumber(session.getSessionNumber());
                entity.setLokSabhaNo(session.getLokSabha().getId());
                entity.setLokSabhaSession(session.getLokSabha().getLokSabhaNumber());
                entity.setRajyaSabhaSessionNo(null);
                entity.setPendingAt("Table Office A");
                serialNumberRequest.setSessionId(entity.getLokSabhaSessionNo());
            }
            if (StringUtils.isBlank(plotinputDTO.getApplicationNo())) {
                SerialNumberResponse applicationNo = generateApplicationNo(serialNumberRequest);
                entity.setApplicationNo(applicationNo.getId());
                entity.setRefNo(applicationNo.getId());
            }
            entity.setCurrentStatus(entity.getStatus());
            entity.setDocumentDate(entity.getSubmissionDate());
            log.debug("entity: {}", entity);
            // set content
            String departmentName = "";
            if (entity.getDepartmentId() != null) {
                Optional<Departments> optionalDepartment = departmentRepo.findById(entity.getDepartmentId());
                departmentName = optionalDepartment.get().getDepartmentName();
            }
            Optional<MinistryAuthority> optionalAuthority = authorityRepository.findById(entity.getAuthenticatedById());
            Optional<MasterCode> paperAbbr = codeRepository.findById(entity.getPaperTypeId());
            String abbr = "";
            if (paperAbbr.isPresent())
                abbr = paperAbbr.get().getAbbr();
            String contentText = formatPaperDetails(entity.getPaperDetails(),
                    optionalAuthority.get().getAuthorityName(), departmentName, abbr,
                    entity.getApplicationNo());
            entity.setContent(contentText);
            entity.setHtmlContentEnglish(contentText);
            entity = plotinputRepository.save(entity);
            entity.setRefId(entity.getId());
            // log.info("before plot Input Saved: ID={}, Status={}", entity.getId(),
            // entity.getStatus());
            entity = plotinputRepository.save(entity);
            // log.info("after plot Input Saved: ID={}, Status={}", entity.getId(),
            // entity.getStatus());
            // update Application No in DocumentMetadata table using Uuid
            updateAppNo(entity.getDocuments(), entity.getApplicationNo());
            // Convert saved entity back to DTO
            PlotInputDTO responseDTO = PlotInputMapper.toDTO(entity);
            // log.info("Returning PlotInputDTO: {} and saved Entity: {}",
            // responseDTO,entity);
            log.debug("Returning PlotInputDTO: {}", responseDTO);
            return responseDTO;
        } catch (DataIntegrityViolationException e) {
            log.error("Database constraint violated: {}", e.getMessage());
            throw new CustomDatabaseException("Invalid data input", e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new CustomDatabaseException("An error occurred while saving plot input", e);
        }
    }

    private PlotInput updateRefId(Long id) {
        Optional<PlotInput> entity = plotinputRepository.findById(id);
        if (entity.isPresent()) {
            PlotInput entityData = entity.get();
            entityData.setRefId(id);
            return entityData;
        }
        return null;

    }

    private String formatPaperDetails(List<Map<String, Object>> paperDetails, String Authenticator, String department,
            String abbr,
            String applicationNo) {
        if (Authenticator == null) {
            throw new IllegalArgumentException("Authenticator details cannot be null.");
        }
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("<html><body>");
        // Authenticator details (always included)
        contentBuilder.append("<b>")
                .append(escapeHtml(Authenticator))
                .append(" (")
                .append(escapeHtml(department))
                .append(")</b> to lay on the Table:<br>");
        // Check if paper details exist
        if (paperDetails == null || paperDetails.isEmpty()) {
            // System.out.println("<b>No paper details available.</b> Details");
            contentBuilder.append("<b>No paper details available.</b>");
        } else {
            int i = 1; // Initialize numbering for list items
            for (Map<String, Object> paper : paperDetails) {
                if (paper == null || paper.isEmpty())
                    continue; // Skip null/empty entries
                // Create formatted output for each paper
                // Reports
                if (abbr.equals("PLOT_REPORT")) {
                    String formattedString = getFormattedReportDetails(paper, applicationNo);
                    // Append numbered list item
                    if (StringUtils.isBlank(formattedString)) {
                        contentBuilder.append(formattedString);
                    }
                    contentBuilder.append(i++).append(") ").append(formattedString).append("<br>");
                }
                // notifications
                else if (abbr.equals("PLOT_NOTIFICATION")) {
                    String formattedString = getFormattedNotificationDetails(paper, applicationNo);
                    if (StringUtils.isBlank(formattedString)) {
                        contentBuilder.append(formattedString);
                    }
                    // Append numbered list item
                    contentBuilder.append(i++).append(") ").append(formattedString).append("<br>");
                }
                // others
                else if (abbr.equals("PLOT_OTHER")) {
                    String formattedString = getFormattedOtherPaperDetails(paper, applicationNo);
                    // Append numbered list item
                    if (StringUtils.isBlank(formattedString)) {
                        contentBuilder.append(formattedString);
                    }
                    contentBuilder.append(i++).append(") ").append(formattedString).append("<br>");
                }

            }
        }
        contentBuilder.append("</body></html>");
        return contentBuilder.toString();
    }

    // Report
    private String getFormattedReportDetails(Map<String, Object> paper, String applicationNo) {
        Object paperDocs = paper.get("documents");
        if (paperDocs instanceof List<?>) {
            List<?> tempList = (List<?>) paperDocs;
            // Ensure all elements are of type Map
            boolean allMaps = tempList.stream().allMatch(item -> item instanceof Map);
            if (allMaps) {
                List<Map<String, Object>> docs = tempList.stream()
                        .map(item -> (Map<String, Object>) item)
                        .toList();
                updateAppNo(docs, applicationNo);
            }
        }

        String template = "A copy of the {0} (Hindi and English versions) of the {1}, for the year {2}";
        Object paperNameData = paper.get("paperName");

        Long paperType = null;
        String paperTitle = "";

        if (paperNameData instanceof Map<?, ?> paperNameMap) {
            Object valueObj = paperNameMap.get("value"); // Extract "value" field
            Object labelObj = paperNameMap.get("label"); // Extract "value" field

            if (valueObj instanceof Number numberValue) { // Ensure it's a number before parsing
                paperType = numberValue.longValue();
            }
            if (labelObj instanceof String stringValue) {
                paperTitle = stringValue;
            }
        }

        String formattedString = "";
        MasterCode codeAbbr;
        try {
            codeAbbr = codeRepository.findById(paperType).get();
        } catch (Exception e) {
            return "";
        }
        String abbr = codeAbbr.getAbbr();
        switch (abbr) {
            case "PLOT_RPT_1":
                String nameAndPlace = escapeHtml(paper.get("constitutionalBody")) + ", "
                        + escapeHtml(paper.get("headquarters"));
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTitle),
                        nameAndPlace,
                        escapeHtml(paper.get("year")));
                break;
            case "PLOT_RPT_2":
                // Map<String, Object> ministry = (Map<String, Object>)
                // paper.get("ministryDepartmentPSU");
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTitle),
                        escapeHtml(paper.get("ministryOrDepartment")),
                        escapeHtml(paper.get("year")));
                break;
            case "PLOT_RPT_3":
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTitle),
                        escapeHtml(paper.get("companyHeadquarters")),
                        escapeHtml(paper.get("financialYear")));
                break;
            case "PLOT_RPT_4":
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTitle),
                        escapeHtml(paper.get("bankHeadquarters")),
                        escapeHtml(paper.get("financialYear")));
                break;
            case "PLOT_RPT_5":
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTitle),
                        escapeHtml(paper.get("headquarters")),
                        escapeHtml(paper.get("financialYear")));
                break;
            default:
                formattedString = ""; // Default empty value if paper type is not recognized
                break;
        }
        return formattedString;
    }

    // notification
    private String getFormattedNotificationDetails(Map<String, Object> paper, String applicationNo) {
        Object paperDocs = paper.get("documents");
        if (paperDocs instanceof List<?>) {
            List<?> tempList = (List<?>) paperDocs;
            // Ensure all elements are of type Map
            boolean allMaps = tempList.stream().allMatch(item -> item instanceof Map);
            if (allMaps) {
                List<Map<String, Object>> docs = tempList.stream()
                        .map(item -> (Map<String, Object>) item)
                        .toList();
                updateAppNo(docs, applicationNo);
            }
        }
        String template = "A copy of the {0} (Hindi and English versions) of the {1}, for the year {2}";
        Object paperNameData = paper.get("paperName");

        Long paperType = null;
        String paperTitle = "";

        if (paperNameData instanceof Map<?, ?> paperNameMap) {
            Object valueObj = paperNameMap.get("value"); // Extract "value" field
            Object labelObj = paperNameMap.get("label"); // Extract "value" field

            if (valueObj instanceof Number numberValue) { // Ensure it's a number before parsing
                paperType = numberValue.longValue();
            }
            if (labelObj instanceof String stringValue) {
                paperTitle = stringValue;
            }
        }

        // Object paperNameId = paper.get("notificationId");
        // Long paperType = null;
        // if (paperNameId instanceof Long) {
        // paperType = (Long) paperNameId;
        // } else if (paperNameId instanceof Integer) {
        // paperType = ((Integer) paperNameId).longValue(); // Convert Integer to Long
        // } else if (paperNameId instanceof String) {
        // try {
        // paperType = Long.parseLong((String) paperNameId); // Convert String to Long
        // } catch (NumberFormatException e) {
        // paperType = null; // Handle invalid string cases
        // }
        // }
        // if (paperType == null) {
        // return "";
        // }
        // MasterCode codeAbbr;
        // try {
        // codeAbbr = codeRepository.findById(paperType).get();
        // } catch (Exception e) {
        // return "";
        // }
        // String abbr = codeAbbr.getAbbr();
        // switch (abbr) {
        // case "PLOT_NF_GEN":
        String formattedString = MessageFormat.format(template,
                escapeHtml(paperTitle),
                escapeHtml(paper.get("publishedIn")),
                escapeHtml(paper.get("laidUnderAct")));
        // break;
        // case "PLOT_NF_SON":
        // formattedString = MessageFormat.format(template,
        // escapeHtml(paper.get("notificationType")),
        // escapeHtml(paper.get("publishedIn")),
        // escapeHtml(paper.get("actUnderWhichLaid")));
        // break;
        // case "PLOT_NF_NO":

        // formattedString = MessageFormat.format(template,
        // escapeHtml(paper.get("notificationType")),
        // escapeHtml(paper.get("publishedIn")),
        // escapeHtml(paper.get("actUnderWhichLaid")));
        // break;
        // case "PLOT_NF_ON":
        // formattedString = MessageFormat.format(template,
        // escapeHtml(paper.get("notificationType")),
        // escapeHtml(paper.get("publishedIn")),
        // escapeHtml(paper.get("actUnderWhichLaid")));
        // break;
        // default:
        // formattedString = ""; // Default empty value if paper type is not recognized
        // break;
        // }
        // System.out.println(formattedString);
        return formattedString;
    }

    // other paper
    private String getFormattedOtherPaperDetails(Map<String, Object> paper, String applicationNo) {
        Object paperDocs = paper.get("documents");
        if (paperDocs instanceof List<?>) {
            List<?> tempList = (List<?>) paperDocs;
            // Ensure all elements are of type Map
            boolean allMaps = tempList.stream().allMatch(item -> item instanceof Map);
            if (allMaps) {
                List<Map<String, Object>> docs = tempList.stream()
                        .map(item -> (Map<String, Object>) item)
                        .toList();
                updateAppNo(docs, applicationNo);
            }
        }
        String template = "A copy of the {0} (Hindi and English versions) of the {1}, for the year {2}";
        // Object paperNameData = paper.get("paperName");
        Object paperNameData = paper.get("othersPaperName");
        Long paperType = null;
        String paperTitle = "";
        if (paperNameData instanceof Map<?, ?> paperNameMap) {
            Object valueObj = paperNameMap.get("value");
            Object labelObj = paperNameMap.get("label"); // Extract "value" field
            // Extract "value" field
            // label
            if (valueObj instanceof Number numberValue) { // Ensure it's a number before parsing
                paperType = numberValue.longValue();
            }
            if (labelObj instanceof String stringValue) {
                paperTitle = stringValue;
            }
        }
        if (paperType == null) {
            return "";
        }
        String formattedString = "";
        MasterCode codeAbbr;
        try {
            codeAbbr = codeRepository.findById(paperType).get();
        } catch (Exception e) {
            return "";
        }
        String abbr = codeAbbr.getAbbr();
        switch (abbr) {
            case "PLOT_OTH_1":
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTitle),
                        escapeHtml(paper.get("")),
                        escapeHtml(paper.get("year")));
                break;
            case "PLOT_OTH_2":
                // Map<String, Object> ministry = (Map<String, Object>)
                // paper.get("ministryDepartmentPSU");
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTitle),
                        escapeHtml(paper.get("department")),
                        escapeHtml(paper.get("financialYear")));
                break;
            case "PLOT_OTH_3":
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTitle),
                        escapeHtml(paper.get("department")),
                        escapeHtml(paper.get("financialYear")));
                break;
            case "PLOT_OTH_4":
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTitle),
                        escapeHtml(paper.get("bankHeadquarters")),
                        escapeHtml(paper.get("financialYear")));
                break;
            case "PLOT_OTH_5":
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTitle),
                        escapeHtml(paper.get("statementNumber")),
                        escapeHtml(paper.get("year")));
                break;
            case "PLOT_OTH_6":
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTitle),
                        escapeHtml(paper.get("stateUT")),
                        escapeHtml(paper.get("financialYear")));
                break;
            case "PLOT_OTH_7":
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTitle),
                        escapeHtml(paper.get("requirementUnderAct")),
                        escapeHtml(paper.get("year")));
                break;
            default:
                formattedString = ""; // Default empty value if paper type is not recognized
                break;
        }
        return formattedString;
    }

    // Helper method to escape HTML
    private String escapeHtml(Object input) {
        return (input == null) ? ""
                : String.valueOf(input)
                        .replace("&", "&amp;")
                        .replace("<", "&lt;")
                        .replace(">", "&gt;")
                        .replace("\"", "&quot;")
                        .replace("'", "&#39;");
    }

    @Override
    public List<RequestDTO> getAllRequestsByPlotId(Long plotId) {
        List<RequestDTO> dtoList = new ArrayList<>();

        try {
            dtoList = requestRepository.findByPlotInputIdAndIsActive(plotId, true)
                    .stream().map(RequestMapper::toDTO)
                    .collect(Collectors.toList());
            return dtoList;
        } catch (Exception e) {
            log.error("Error in fetching requests", e);
        }
        return dtoList;
    }

    @Override
    public List<RequestDTO> getAllRequests() {
        List<RequestDTO> dtoList = new ArrayList<>();
        try {
            List<Request> entities = requestRepository.findAll();
            log.debug("Returning list of requests: {}", dtoList);

            return dtoList;
        } catch (Exception e) {
            log.error("Error in fetching requests", e);
            // return new GenericResponse<>("Error occurred while fetching requests", null);
        }
        return dtoList;
        // return new GenericResponse<>("Requests fetched successfully", dtoList);
    }

    @Override
    public List<PlotInputDTO> getAll(Boolean isActive, Boolean draftStatus, Long lokSabhaNo, Long lokSabhaSessionNo,
            Long rajyaSabhaSessionNo, LocalDate startDate, LocalDate endDate, String status, LocalDate submissionDate,
            Long department, Long paperType, String paperName, LocalDate proposedDateOfLaying,
            LocalDate actualDateOfLaying, Integer ministry, Boolean sendToLibrary, Long applicationId) {
        // log.debug("Entering getAll Plots");
        List<PlotInputDTO> dtoList = new ArrayList<>();
        if (status != null && status.equalsIgnoreCase("draft")) {
            List<PlotInput> plotInputs = getDraftedData(applicationId, lokSabhaNo, lokSabhaSessionNo,
                    rajyaSabhaSessionNo, startDate, endDate, status, submissionDate, department, paperType, paperName,
                    proposedDateOfLaying, actualDateOfLaying, ministry);
            for (PlotInput input : plotInputs) {
                System.out.println("entity");
                System.out.println(input);
                if (input != null) {
                    PlotInputDTO draftDto = PlotInputMapper.toDTO(input);
                    dtoList.add(draftDto);
                }
            }
        } else {
            List<PlotInput> entitys;
            if (isActive == null) {
                isActive = true;
            }
            Specification<PlotInput> spec = Specification.where(null);
            spec = spec.and(PlotInputSpec.isActive(isActive));
            if (lokSabhaSessionNo != null) {
                spec = spec.and(PlotInputSpec.hasLokSabhaSessionNo(lokSabhaSessionNo));
            }
            if (rajyaSabhaSessionNo != null) {
                spec = spec.and(PlotInputSpec.hasRajyaSabhaSessionNo(rajyaSabhaSessionNo));
            }
            if (startDate != null || endDate != null) {
                spec = spec.and(PlotInputSpec.hasDateBetween(startDate, endDate));
            }
            if (status != null) {
                spec = spec.and(PlotInputSpec.hasStatus(status));
            }
            if (department != null) {
                spec = spec.and(PlotInputSpec.hasDepartmentId(department));
            }
            if (ministry != null) {
                spec = spec.and(PlotInputSpec.hasMinistryId(ministry));
            }
            if (proposedDateOfLaying != null) {
                spec = spec.and(PlotInputSpec.hasProposedDateOfLaying(proposedDateOfLaying));
            }
            if (sendToLibrary != null) {
                spec = spec.and(PlotInputSpec.hasSendToLibrary(sendToLibrary));
            }
            if (actualDateOfLaying != null) {
                spec = spec.and(PlotInputSpec.hasActualDateOfLaying(actualDateOfLaying));
            }
            if (submissionDate != null) {
                spec = spec.and(PlotInputSpec.hasSubmissionDate(submissionDate));
            }
            if (paperType != null) {
                spec = spec.and(PlotInputSpec.hasPaperTypeId(paperType));
            }
            if (paperName != null) {
                spec = spec.and(PlotInputSpec.hasPaperName(paperName));
            }
            Sort sort = Sort.by(Sort.Direction.DESC, "submissionDate");
            entitys = plotinputRepository.findAll(spec, sort);
            // entitys = plotinputRepository.findAll(spec);
            Long houseId = getHouseId();
            for (PlotInput entity : entitys) {
                if (houseId == 1) {
                    if (entity.getRajyaSabhaSessionNo() != null) {
                        if (entity.getApplicationInstanceId() != null) {
                            PlotGenerate plot = repository
                                    .findById(entity.getApplicationInstanceId()).get();
                            if (!plot.getCurrentStatus().equalsIgnoreCase("returned")
                                    || !plot.getCurrentStatus().equalsIgnoreCase("draft")) {
                                if (plot.getCurrentStatus().equalsIgnoreCase("published")
                                        || plot.getCurrentStatus().equalsIgnoreCase("approved")) {
                                    entity.setPendingAt(" ");
                                    entity.setStatus(plot.getCurrentStatus());
                                    entity.setCurrentStatus(plot.getCurrentStatus());
                                } 
                                // else {
                                //     entity.setPendingAt("Table Office");
                                //     entity.setStatus("In Progress");
                                //     entity.setCurrentStatus("In Progress");
                                // }
                            }
                        }
                        entity = plotinputRepository.save(entity);
                        PlotInputDTO dto = PlotInputMapper.toDTO(entity);
                        dto.setRequests(getRequests(dto.getId()));
                        dtoList.add(dto);
                    }
                } else if (houseId == 2) {
                    if (entity.getLokSabhaSessionNo() != null) {
                        if (entity.getApplicationInstanceId() != null) {
                            PlotGenerate plot = repository
                                    .findById(entity.getApplicationInstanceId()).get();
                            if (!plot.getCurrentStatus().equalsIgnoreCase("returned")
                                    || !plot.getCurrentStatus().equalsIgnoreCase("draft")) {
                                if (plot.getCurrentStatus().equalsIgnoreCase("published")
                                        || plot.getCurrentStatus().equalsIgnoreCase("approved")) {
                                    entity.setPendingAt(" ");
                                    entity.setStatus(plot.getCurrentStatus());
                                    entity.setCurrentStatus(plot.getCurrentStatus());
                                }
                                // else {
                                //     entity.setPendingAt("Table Office A");
                                //     entity.setStatus("In Progress");
                                //     entity.setCurrentStatus("In Progress");
                                // }
                            }
                        }
                        entity = plotinputRepository.save(entity);
                        PlotInputDTO dto = PlotInputMapper.toDTO(entity);
                        dto.setRequests(getRequests(dto.getId()));
                        dtoList.add(dto);
                    }
                }
            }
            // List<PlotInput> plotInputs=new ArrayList<>();
            if (status != null) {
                if (!status.equalsIgnoreCase("draft")) {
                    List<PlotInput> plotInputs = getDraftedData(dtoList.get(0).getApplicationId(), lokSabhaNo, lokSabhaSessionNo,
                            rajyaSabhaSessionNo, startDate, endDate, status, submissionDate, department, paperType,
                            paperName, proposedDateOfLaying, actualDateOfLaying, ministry);
                    for (PlotInput input : plotInputs) {
                        if (input != null) {
                            dtoList.add(PlotInputMapper.toDTO(input));
                        }
                    }
                }
            } else {
                List<PlotInput> plotInputs = getDraftedData(dtoList.get(0).getApplicationId(), lokSabhaNo, lokSabhaSessionNo,
                        rajyaSabhaSessionNo, startDate, endDate, status, submissionDate, department, paperType,
                        paperName, proposedDateOfLaying, actualDateOfLaying, ministry);
                for (PlotInput input : plotInputs) {
                    if (input != null) {
                        dtoList.add(PlotInputMapper.toDTO(input));
                    }
                }
            }
        }
        // log.debug("Returning list of PlotDTOs: {}", dtoList);
        // log.info("Returning list of PlotDTOs: {}", dtoList);
        // log.error("Returning list of PlotDTOs: {}", dtoList);
        return dtoList;
    }

    private List<PlotInput> getDraftedData(Long applicationId, Long lokSabhaNo, Long lokSabhaSessionNo,
            Long rajyaSabhaSessionNo, LocalDate startDate, LocalDate endDate, String status, LocalDate submissionDate,
            Long department, Long paperType, String paperName, LocalDate proposedDateOfLaying,
            LocalDate actualDateOfLaying, Integer ministry) {
        String url = draftUrl + "/getDraftByAppId/" + applicationId;
        // System.out.println("@@@@@@@");
        // System.err.println(url);
        List<PlotInput> plotInputs = new ArrayList<>();
        String token = getTokenFromRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Add the Authorization token if available
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", "Bearer " + token);
        } else {
            throw new RuntimeException("JWT Token is missing. Cannot proceed with the API request.");
        }
        // Create request entity with headers
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        try {
            ResponseEntity<GenericResponse<List<DraftDataDTO>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<GenericResponse<List<DraftDataDTO>>>() {
                    });
            System.out.println(response);
            if (response.getBody() != null && response.getBody().getData() != null) {
                List<DraftDataDTO> draftDataDTOs = response.getBody().getData();
                // System.out.println("$$$$$$$$$");
                // System.out.println(draftDataDTOs.get(0));
                if (draftDataDTOs != null) {
                    for (DraftDataDTO dto : draftDataDTOs) {
                        System.out.println("dto");
                        System.out.println(dto);
                        PlotInput inputEntity = PlotInputMapper.mapDraftToEntity(dto, lokSabhaNo, lokSabhaSessionNo,
                                rajyaSabhaSessionNo, startDate, endDate, status, submissionDate,
                                department, paperType, paperName, proposedDateOfLaying,
                                actualDateOfLaying, ministry);
                        System.out.println(inputEntity);
                        if(inputEntity!=null)
                        plotInputs.add(inputEntity);
                    }
                }
            }
            return plotInputs;
        } catch (Exception e) {
            System.out.println("response exception in drafted content" + e);
            e.printStackTrace();
        }
        return plotInputs;
    }

    @Override
    public List<PlotInputDTO> getHistories(Boolean isActive, Boolean draftStatus, Long lokSabhaNo,
            Long lokSabhaSessionNo, Long rajyaSabhaSessionNo, LocalDate startDate, LocalDate endDate, String status,
            LocalDate dateOfSubmission, Long department, Long paperType, String paperName,
            LocalDate proposedDateOfLaying, LocalDate actualDateOfLaying, Integer ministry) {
        // log.debug("Entering getAll Plots");
        List<PlotInputDTO> dtoList = new ArrayList<>();
        Long createdBy = getUserIdFromToken();
        List<PlotInput> entitys;
        if (isActive == null) {
            isActive = true;
        }
        Specification<PlotInput> spec = Specification.where(null);
        spec = spec.and(PlotInputSpec.isActive(isActive));
        if (lokSabhaSessionNo != null) {
            spec = spec.and(PlotInputSpec.hasLokSabhaSessionNo(lokSabhaSessionNo));
        }
        if (rajyaSabhaSessionNo != null) {
            spec = spec.and(PlotInputSpec.hasRajyaSabhaSessionNo(rajyaSabhaSessionNo));
        }
        if (startDate != null || endDate != null) {
            spec = spec.and(PlotInputSpec.hasDateBetween(startDate, endDate));
        }
        if (status != null) {
            spec = spec.and(PlotInputSpec.hasStatus(status));
        }
        if (department != null) {
            spec = spec.and(PlotInputSpec.hasDepartmentId(department));
        }
        if (ministry != null) {
            spec = spec.and(PlotInputSpec.hasMinistryId(ministry));
        }
        if (proposedDateOfLaying != null) {
            spec = spec.and(PlotInputSpec.hasProposedDateOfLaying(proposedDateOfLaying));
        }
        if (actualDateOfLaying != null) {
            spec = spec.and(PlotInputSpec.hasActualDateOfLaying(actualDateOfLaying));
        }
        if (dateOfSubmission != null) {
            spec = spec.and(PlotInputSpec.hasSubmissionDate(dateOfSubmission));
        }
        if (paperType != null) {
            spec = spec.and(PlotInputSpec.hasPaperTypeId(paperType));
        }
        if (paperName != null) {
            spec = spec.and(PlotInputSpec.hasPaperName(paperName));
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "submissionDate");
        entitys = plotinputRepository.findAll(spec, sort);
        // entitys = plotinputRepository.findAll(spec);
        Long houseId = getHouseId();
        for (PlotInput entity : entitys) {
            if (houseId == 1) {
                if (entity.getRajyaSabhaSessionNo() != null) {
                    if (entity.getApplicationInstanceId() != null) {
                        PlotGenerate plot = repository
                                .findById(entity.getApplicationInstanceId()).get();
                        if (!plot.getCurrentStatus().equalsIgnoreCase("returned")
                                || !plot.getCurrentStatus().equalsIgnoreCase("draft")) {
                            if (plot.getCurrentStatus().equalsIgnoreCase("published")
                                    || plot.getCurrentStatus().equalsIgnoreCase("approved")) {
                                entity.setPendingAt(" ");
                                entity.setStatus(plot.getCurrentStatus());
                                entity.setCurrentStatus(plot.getCurrentStatus());
                            } else {
                                // entity.setPendingAt("Table Office");
                                entity.setStatus("In Progress");
                                entity.setCurrentStatus("In Progress");
                            }
                        }
                    }
                    PlotInputDTO dto = PlotInputMapper.toDTO(entity);
                    dto.setRequests(getRequests(dto.getId()));
                    List<PlotHistory> histories = plotHistoryRepository
                            .findAllByPlotInputIdOrderByCreatedAtAsc(dto.getId());
                    for (PlotHistory history : histories) {
                        Long createdData = history.getCreatedBy();
                        if (createdData == createdBy)
                            dto.setContent(history.getAddedText());
                    }
                    dtoList.add(dto);
                }
            } else if (houseId == 2) {
                if (entity.getLokSabhaSessionNo() != null) {
                    if (entity.getApplicationInstanceId() != null) {
                        PlotGenerate plot = repository
                                .findById(entity.getApplicationInstanceId()).get();
                        if (!plot.getCurrentStatus().equalsIgnoreCase("returned")
                                || !plot.getCurrentStatus().equalsIgnoreCase("draft")) {
                            if (plot.getCurrentStatus().equalsIgnoreCase("published")
                                    || plot.getCurrentStatus().equalsIgnoreCase("approved")) {
                                entity.setPendingAt(" ");
                                entity.setStatus(plot.getCurrentStatus());
                                entity.setCurrentStatus(plot.getCurrentStatus());
                            } else {
                                // entity.setPendingAt("Table Office");
                                entity.setStatus("In Progress");
                                entity.setCurrentStatus("In Progress");
                            }
                        }
                    }
                    PlotInputDTO dto = PlotInputMapper.toDTO(entity);
                    List<PlotHistory> histories = plotHistoryRepository
                            .findAllByPlotInputIdOrderByCreatedAtAsc(dto.getId());
                    for (PlotHistory history : histories) {
                        Long createdData = history.getCreatedBy();
                        if (createdData == createdBy)
                            dto.setContent(history.getAddedText());
                    }
                    dto.setRequests(getRequests(dto.getId()));
                    dtoList.add(dto);
                }
            }
        }
        return dtoList;
    }

    @Override
    public PlotInputDTO getById(Long id) {
        // log.debug("Entering getById with id: {}", id);
        PlotInput dBEntity = plotinputRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlotInput not found with id: " + id));
        // log.debug("Fetched PlotInput by id: {}", dBEntity);

        if (dBEntity.getApplicationInstanceId() != null) {
            PlotGenerate plot = repository
                    .findById(dBEntity.getApplicationInstanceId()).get();
            if (!plot.getCurrentStatus().equalsIgnoreCase("returned")
                    || !plot.getCurrentStatus().equalsIgnoreCase("draft")) {
                if (plot.getCurrentStatus().equalsIgnoreCase("published")
                        || plot.getCurrentStatus().equalsIgnoreCase("approved")) {
                    dBEntity.setPendingAt(" ");
                    dBEntity.setStatus(plot.getCurrentStatus());
                    dBEntity.setCurrentStatus(plot.getCurrentStatus());
                } else {
                    // entity.setPendingAt("Table Office");
                    dBEntity.setStatus("In Progress");
                    dBEntity.setCurrentStatus("In Progress");
                }
            }
        } else {
            dBEntity.setStatus("Submitted");
            dBEntity.setCurrentStatus("Submitted");
        }
        dBEntity = plotinputRepository.save(dBEntity);
        PlotInputDTO dto = PlotInputMapper.toDTO(dBEntity);
        dto.setRequests(getRequests(dto.getId()));

        return dto;
    }

    @Override
    public RequestDTO getRequestById(Long id) {
        // log.debug("Entering getById with id: {}", id);
        Request dBEntity = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + id));
        // log.debug("Fetched Request by id: {}", dBEntity);
        RequestDTO dto = RequestMapper.toDTO(dBEntity);
        return dto;
    }

    @Transactional
    @Override
    public PlotInputDTO update(Long id, PlotInputDTO plotinputDTO) {
        log.debug("Starting update for PlotInput with ID: {}", id);
        // Fetch the existing PlotInput
        PlotInput dbEntity = plotinputRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlotInput not found with id: " + id));
        // Update only the fields you want to update: TODO. Default -> All parameters
        // are updated
        PlotInput dbEntityNew = PlotInputMapper.toEntity(plotinputDTO);
        Field[] fields = dbEntityNew.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true); // Allow access to private fields
            try {
                Object newValue = field.get(dbEntityNew);
                if (newValue != null) {
                    Field entityField = dbEntity.getClass().getDeclaredField(field.getName());
                    entityField.setAccessible(true);
                    entityField.set(dbEntity, newValue);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Error updating field: " + field.getName(), e);
            }
        }
        if (plotinputDTO.getPaperDetails() != null) {
            // Optional<BaseUser> optionalUser =
            // userRepository.findById(dbEntity.getAuthenticatedById());
            Optional<MinistryAuthority> optionalAuthority = authorityRepository
                    .findById(dbEntity.getAuthenticatedById());
            String departmentName = "";
            if (dbEntity.getDepartmentId() != null) {
                Optional<Departments> optionalDepartment = departmentRepo.findById(dbEntity.getDepartmentId());
                departmentName = optionalDepartment.get().getDepartmentName();
            }
            // Optional<Departments> optionalDepartment =
            // departmentRepo.findById(dbEntity.getDepartmentId());
            Optional<MasterCode> paperAbbr = codeRepository.findById(dbEntity.getPaperTypeId());
            String abbr = "";
            if (paperAbbr.isPresent())
                abbr = paperAbbr.get().getAbbr();
            String contentText = formatPaperDetails(dbEntity.getPaperDetails(),
                    optionalAuthority.get().getAuthorityName(), departmentName, abbr,
                    dbEntity.getApplicationNo());
            dbEntity.setContent(contentText);
            dbEntity.setHtmlContentEnglish(contentText);
        }
        // Save the updated PlotInput
        Long houseId = getHouseId();
        if (dbEntityNew.getStatus().equalsIgnoreCase("returned")) {
            // List<Request> requests =
            // requestRepository.findByPlotInputIdAndIsActive(dbEntity.getId(), true);
            // for (Request request : requests) {
            // request.setIsActive(false);
            // request.setIsDeleted(true);
            // request=requestRepository.save(request);
            // }
            List<Request> requests = requestRepository.findByPlotInputIdAndIsActive(dbEntity.getId(), true);

            // Update fields in a single loop
            requests.forEach(request -> {
                request.setIsActive(false);
                request.setIsDeleted(true);
            });

            // Save all updated requests in a single batch operation
            requestRepository.saveAll(requests);
            if (dbEntity.getApplicationInstanceId() != null) {
                throw new ResourceNotFoundException(
                        "Plot has already generated with Specific Ministry Input  So cannot be returned: " + id);
            }
            BaseUser user = userRepository.findById(dbEntity.getCreatedBy()).get();
            dbEntity.setPendingAt(user.getFirstName() + " " + user.getLastName());
        } else if (dbEntityNew.getStatus().equalsIgnoreCase("published") ||
                dbEntityNew.getStatus().equalsIgnoreCase("approved")) {
            if (dbEntity.getApplicationInstanceId() != null) {
                PlotGenerate plot = repository
                        .findById(dbEntity.getApplicationInstanceId()).get();
                plot.setStatus(dbEntity.getStatus());
                plot.setCurrentStatus(dbEntity.getStatus());
                plot = repository.save(plot);
                if (houseId == 1) {
                    dbEntity.setPendingAt("Table Office");
                } else if (houseId == 2) {
                    dbEntity.setPendingAt("Table Office A");
                }
            }
        } else {
            if (houseId == 1) {
                dbEntity.setPendingAt("Table Office");
            } else if (houseId == 2) {
                dbEntity.setPendingAt("Table Office A");
            }
        }
        dbEntity.setCurrentStatus(dbEntity.getStatus());
        dbEntity.setDocumentDate(dbEntity.getSubmissionDate());
        dbEntity.setHtmlContentEnglish(dbEntity.getContent());
        dbEntity.setApplicationInstanceId(dbEntity.getGeneratedPlotId());
        dbEntity = plotinputRepository.save(dbEntity);
        if (dbEntity.getApplicationInstanceId() != null && dbEntity.getSendToLibrary()) {
            PlotGenerate plot = repository
                    .findById(dbEntity.getApplicationInstanceId()).get();
            plot.setSendToLibrary(dbEntity.getSendToLibrary());
            plot = repository.save(plot);
        }
        // Prepare the response DTO
        PlotInputDTO dto = PlotInputMapper.toDTO(dbEntity);
        dto.setRequests(getRequests(dto.getId()));

        return dto;
    }

    @Override
    public void delete(Long id) {
        Optional<PlotInput> dBEntryOptional = plotinputRepository.findByIdAndIsActive(id, true);
        if (dBEntryOptional.isEmpty()) {
            throw new ResourceNotFoundException("Already Deleted or does not exists");
        }
        PlotInput dBEntry = dBEntryOptional.get();
        dBEntry.setIsDeleted(true);
        dBEntry.setIsActive(false);
        plotinputRepository.saveAndFlush(dBEntry);
    }

    @Override
    public RequestDTO saveRequest(RequestDTO dto) {

        try {
            // Convert DTO to entity
            Request entity = RequestMapper.toEntity(dto);
            entity = requestRepository.save(entity);
            log.debug("raised Request to entity: {}", entity);
            // log.info("Saved request attributes: ID={}, plot id={}", entity.getId(),
            // entity.getPlotInputId());
            return RequestMapper.toDTO(entity);
        } catch (Exception e) {
            log.error("Error in save Request: ", e);
            throw e;
        }
    }

    // date chart Services
    @Override
    public PlotDateChartDTO addDateChart(PlotDateChartDTO dto) {
        PlotDateChart entity = PlotInputMapper.dateChartMapToEntity(dto);
        Optional<PlotDateChart> optionalEntity;
        // Optional<PlotDateChart> optionalEntity =
        // plotDateChartRepository.findByMinistryId(entity.getMinistryId());
        if (entity.getLokSabhaSessionNo() != null) {
            optionalEntity = plotDateChartRepository.findByMinistryIdAndLokSabhaNoAndLokSabhaSessionNo(
                    entity.getMinistryId(),
                    entity.getLokSabhaNo(),
                    entity.getLokSabhaSessionNo());
        } else {
            optionalEntity = plotDateChartRepository.findByMinistryIdAndRajyaSabhaSessionNo(
                    entity.getMinistryId(),
                    entity.getRajyaSabhaSessionNo());
        }
        // System.out.println("@@@@@@@@@@@@");
        if (optionalEntity.isPresent()) {
            // System.out.println(optionalEntity.get());
            PlotDateChart existingEntity = optionalEntity.get();
            Field[] fields = entity.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true); // Allow access to private fields
                try {
                    Object newValue = field.get(entity);
                    if (newValue != null) {
                        Field entityField = existingEntity.getClass().getDeclaredField(field.getName());
                        entityField.setAccessible(true);
                        entityField.set(existingEntity, newValue);
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException("Error updating field: " + field.getName(), e);
                }
            }

            // System.out.println("Existing entity");
            // System.out.println(existingEntity);
            existingEntity = plotDateChartRepository.save(existingEntity);
            return PlotInputMapper.dateChartMapToDTO(existingEntity);
        } else {

            entity = plotDateChartRepository.save(entity);
            return PlotInputMapper.dateChartMapToDTO(entity);
        }
    }

    @Override
    public PlotDateChartDTO getDateChartById(Long id) {
        // TODO Auto-generated method stub
        PlotDateChart savedEntity = plotDateChartRepository.findById(id).get();
        return PlotInputMapper.dateChartMapToDTO(savedEntity);
    }

    @Override
    public List<PlotDateChartDTO> getAllDateCharts(String lokSabhaNo, String lokSabhaSessionNo, String rajyaSabhaNo,
            Boolean isActive, LocalDate startDate, LocalDate endDate, String status, Long ministryId, String groupName,
            LocalDate dateofSubmissionOfPapers, LocalDate actualDateOfLaying) {
        Long houseId = getHouseId();
        if (isActive == null) {
            isActive = true;
        }
        Specification<PlotDateChart> spec = Specification.where(null);
        spec = spec.and(DateChartSpec.isActive(isActive));
        if (ministryId != null) {
            spec = spec.and(DateChartSpec.hasMinistryId(ministryId));
        }
        if (groupName != null) {
            spec = spec.and(DateChartSpec.hasGroupName(groupName));
        }
        if (lokSabhaSessionNo == null || lokSabhaSessionNo.isEmpty() || lokSabhaSessionNo.isBlank()) {
            spec = spec.and(DateChartSpec.hasLokSabhaSessionNo(null));
        } else {
            spec = spec.and(DateChartSpec.hasLokSabhaSessionNo(Long.parseLong(lokSabhaSessionNo)));
        }
        if (rajyaSabhaNo == null || rajyaSabhaNo.isEmpty() || rajyaSabhaNo.isBlank()) {
            spec = spec.and(DateChartSpec.hasRajyaSabhaSessionNo(null));
        } else {
            Optional<MastersRajyaSabha> rajyaSabha = mastersRSSRepository.findBySessionNumber(rajyaSabhaNo);
            spec = spec.and(DateChartSpec.hasRajyaSabhaSessionNo(rajyaSabha.get().getId()));
        }
        List<PlotDateChart> listOfDateCharts = plotDateChartRepository.findAll();
        List<Long> existingMinistries = listOfDateCharts.stream()
                .map(PlotDateChart::getMinistryId)
                .collect(Collectors.toList());
        String response = questionBranchApi(lokSabhaNo, lokSabhaSessionNo, rajyaSabhaNo, existingMinistries, houseId);
        listOfDateCharts = plotDateChartRepository.findAll(spec);
        List<PlotDateChartDTO> dtoList = new ArrayList<>();
        for (PlotDateChart entity : listOfDateCharts) {
            boolean shouldAdd = true;
            if (houseId == 1) {
                if (entity.getRajyaSabhaSessionNo() == null) {
                    shouldAdd = false;
                    continue;
                }

            } else if (houseId == 2) {
                if (entity.getLokSabhaSessionNo() == null) {
                    shouldAdd = false;
                    continue;
                }
            }
            List<Map<String, Object>> dateList = entity.getDates();
            List<String> existingDOS = new ArrayList<>();
            List<String> existingADL = new ArrayList<>();
            // Extracts list of date mappings
            for (Map<String, Object> dateEntry : dateList) {
                String existingdateofSubmissionOfPapers = (String) dateEntry.get("dateofSubmissionOfPapers");
                String existingactualDateOfLaying = (String) dateEntry.get("actualDateOfLaying");

                if (existingdateofSubmissionOfPapers != null) {
                    existingDOS.add(existingdateofSubmissionOfPapers);
                }
                if (existingactualDateOfLaying != null) {
                    existingADL.add(existingactualDateOfLaying);
                }
            }
            if (dateofSubmissionOfPapers != null) {
                if (existingDOS.contains(dateofSubmissionOfPapers.toString())) {
                    shouldAdd = true;
                } else {
                    shouldAdd = false;
                }
            }
            if (actualDateOfLaying != null) {
                if (existingADL.contains(actualDateOfLaying.toString())) {
                    shouldAdd = true;
                } else {
                    shouldAdd = false;
                }
            }
            if (shouldAdd) {
                dtoList.add(PlotInputMapper.dateChartMapToDTO(entity));
                // Stop checking further dates for this entity once added
            }
        }
        return dtoList;
    }

    // question api
    public String questionBranchApi(String lokSabhaNo, String lokSabhaSessionNo, String rajyaSabhaNo,
            List<Long> existingMinistries, Long houseId) {
        List<PlotDateChart> entityList = new ArrayList<>();
        if (houseId == 1) {
            String url = sansadUrl + "/api_rs/question/ministry-group-sitting?session="
                    + rajyaSabhaNo;
            String token = getTokenFromRequest();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // Add the Authorization token if available
            if (token != null && !token.isEmpty()) {
                headers.set("Authorization", "Bearer " + token);
            } else {
                throw new RuntimeException("JWT Token is missing. Cannot proceed with the API request.");
            }
            // Create request entity with headers
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            try {
                ResponseEntity<List<QuesBranchApiDtoRS>> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        requestEntity,
                        new ParameterizedTypeReference<List<QuesBranchApiDtoRS>>() {
                        });
                List<QuesBranchApiDtoRS> quesDtoList = response.getBody();
                for (QuesBranchApiDtoRS quesDto : quesDtoList) {
                    // Define date format
                    // "2024-07-03 00:00:00.0"
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                    List<Map<String, Object>> datesList = quesDto.getSittingDates().stream()
                            .map(date -> {
                                LocalDate proposedDate = LocalDate.parse(date, formatter);
                                LocalDate submissionDate = proposedDate.minusDays(2);
                                // Create the JSON-like structure
                                Map<String, Object> dateMap = new HashMap<>();
                                dateMap.put("proposedDateOfLaying", proposedDate.toString());
                                dateMap.put("dateofSubmissionOfPapers", submissionDate.toString());
                                dateMap.put("actualDateOfLaying", "");
                                return dateMap;
                            })
                            .collect(Collectors.toList());
                    // String[] ministries = quesDto.getMinistriesDepartments().split(";");
                    for (String ministry : quesDto.getMinistries()) {
                        ministry = ministry.toUpperCase();
                        ministry = ministry.trim();
                        Optional<Ministries> optionalMinistries = ministriesRepository
                                .findByMinistryName(ministry);
                        if (optionalMinistries.isPresent()) {
                            Long ministryId = optionalMinistries.get().getId();
                            // Check if ministryId already exists in the list
                            if (existingMinistries.contains(ministryId)) {
                                continue; // Skip adding entry if ministryId is already present
                            }
                            PlotDateChartDTO dto = PlotDateChartDTO.builder()
                                    .dates(datesList)
                                    .groupName(quesDto.getGroup())
                                    .ministryId(ministryId)
                                    .build();
                            Optional<MastersRajyaSabha> optionalSession = mastersRSSRepository
                                    .findBySessionNumber(rajyaSabhaNo);
                            if (optionalSession.isPresent()) {
                                MastersRajyaSabha session = optionalSession.get();
                                dto.setRajyaSabhaSessionNo(session.getId());
                            }

                            dto.setMinistryId(optionalMinistries.get().getId());
                            PlotDateChart entity = PlotInputMapper.dateChartMapToEntity(dto);
                            entityList.add(entity);
                        }
                    }
                }

                entityList = plotDateChartRepository.saveAll(entityList);
                return "saved";
            } catch (HttpClientErrorException e) {
                System.err.println("API Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
                throw new RuntimeException("Failed to call external API", e);
            }

        } else {
            String url = sansadUrl + "/integration-gateway/question-branch-api/get-question-chart?lsNo="
                    + lokSabhaNo + "&sesNo=" + lokSabhaSessionNo;
            String token = getTokenFromRequest();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // Add the Authorization token if available
            if (token != null && !token.isEmpty()) {
                headers.set("Authorization", "Bearer " + token);
            } else {
                throw new RuntimeException("JWT Token is missing. Cannot proceed with the API request.");
            }
            // Create request entity with headers
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            try {
                ResponseEntity<List<QuesBranchApiDto>> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        requestEntity,
                        new ParameterizedTypeReference<List<QuesBranchApiDto>>() {
                        });
                List<QuesBranchApiDto> quesDtoList = response.getBody();
                for (QuesBranchApiDto quesDto : quesDtoList) {
                    // Define date format
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
                    // Convert to List<Map<String, Object>>
                    List<Map<String, Object>> datesList = Arrays
                            .stream(quesDto.getDateOfAnsweringQuestions().split(","))
                            .map(String::trim)
                            .map(date -> {
                                LocalDate proposedDate = LocalDate.parse(date, formatter);
                                LocalDate submissionDate = proposedDate.minusDays(2);
                                // Create the JSON-like structure
                                Map<String, Object> dateMap = new HashMap<>();
                                dateMap.put("proposedDateOfLaying", proposedDate.toString());
                                dateMap.put("dateofSubmissionOfPapers", submissionDate.toString());
                                dateMap.put("actualDateOfLaying", "");
                                return dateMap;
                            })
                            .collect(Collectors.toList());
                    String ministriesStr = quesDto.getMinistriesDepartments();
                    String[] ministries;

                    if (ministriesStr.contains(";")) {
                        ministries = ministriesStr.split(";");
                    } else {
                        ministries = new String[] { ministriesStr }; // Store as a single-element array
                    }
                    // String[] ministries = quesDto.getMinistriesDepartments().split(";");
                    for (String ministry : ministries) {
                        ministry = ministry.toUpperCase();
                        ministry = ministry.trim();
                        Optional<Ministries> optionalMinistries = ministriesRepository
                                .findByMinistryName(ministry);

                        if (optionalMinistries.isPresent()) {
                            Long ministryId = optionalMinistries.get().getId();
                            // Check if ministryId already exists in the list

                            if (existingMinistries.contains(ministryId)) {
                                continue; // Skip adding entry if ministryId is already present
                            }
                            PlotDateChartDTO dto = PlotDateChartDTO.builder()
                                    .dates(datesList)
                                    .groupName(quesDto.getGroup())
                                    .ministryId(ministryId)
                                    .build();
                            Optional<MastersLokSabhaSession> optionalSession = masterLSSRepository
                                    .findById(Long.parseLong(lokSabhaSessionNo));
                            if (optionalSession.isPresent()) {
                                MastersLokSabhaSession session = optionalSession.get();
                                dto.setLokSabhaNo(session.getLokSabha().getId());
                                dto.setLokSabhaSessionNo(session.getId());
                            }

                            dto.setMinistryId(optionalMinistries.get().getId());
                            PlotDateChart entity = PlotInputMapper.dateChartMapToEntity(dto);
                            entityList.add(entity);
                        }
                    }
                }

                entityList = plotDateChartRepository.saveAll(entityList);
                return "saved";
            } catch (HttpClientErrorException e) {
                System.err.println("API Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
                throw new RuntimeException("Failed to call external API", e);
            }
        }

    }

    // Generate Application No Functionality
    public SerialNumberResponse generateApplicationNo(SerialNumberRequest request) {
        String url = masterServiceUrl + "/serial-number";
        String token = getTokenFromRequest();
        if (token == null) {
            throw new IllegalStateException("Authorization token is missing");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SerialNumberRequest> entity = new HttpEntity<>(request, headers);
        try {
            // RestTemplate restTemplate = new RestTemplate();
            return restTemplate.postForObject(url, entity, SerialNumberResponse.class);
        } catch (Exception e) {
            System.err.println("Error during API call: " + e.getMessage());
            throw e;
        }
    }

    public String getTokenFromRequest() {

        String authorizationHeader = httpRequest.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    private Long getHouseId() {
        Long houseId = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsDTO) {
            houseId = ((UserDetailsDTO) principal).getHouseId();
            // System.out.println("houseId " + houseId);
        }
        return houseId;
    }

    private void updateAppNo(List<Map<String, Object>> docs, String applicationNo) {

        if (docs != null && docs.size() > 0) {
            for (Map<String, Object> doc : docs) {
                Object uuidObj = doc.get("documentUuid");
                if (uuidObj instanceof String uuid && StringUtils.isNotBlank(uuid)) {
                    try {
                        int uuidExist = plotinputRepository.checkUuidExists(uuid);
                        // System.out.println("Checking UUID: " + uuid);
                        System.err.println("UUID Exist Status: " + uuidExist);

                        if (uuidExist == 1) {
                            // System.out.println("Updating application number for UUID: " + uuid);
                            plotinputRepository.updateApplicationNoByUUID(uuid, applicationNo);
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing UUID: " + uuid + " - " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Invalid or missing UUID in document: " + doc);
                }
            }
        } else {
            System.err.println("Document list is null or empty.");
        }
    }

    private Long getUserIdFromToken() {
        Long userId = utils.getUserIdFromAuthentication();
        // System.out.println(userId);
        return userId;
    }

    @Override
    public List<PlotInputDTO> getAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }

    public List<RequestDTO> getRequests(Long plotId) {
        List<RequestDTO> dtos = new ArrayList<>();
        List<Request> entity = requestRepository.findByPlotInputIdAndIsActive(plotId, true);
        if (entity != null) {
            dtos = entity.stream().map(RequestMapper::toDTO).collect(Collectors.toList());
        }
        return dtos;
    }
}
