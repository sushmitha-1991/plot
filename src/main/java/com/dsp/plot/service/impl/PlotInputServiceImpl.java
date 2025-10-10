package com.dsp.plot.service.impl;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.dsp.plot.entity.*;
import com.dsp.plot.repository.*;
import org.apache.commons.lang3.StringUtils;
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
import com.dsp.plot.entity.specification.DateChartSpec;
import com.dsp.plot.entity.specification.PlotInputSpec;
import com.dsp.plot.exception.CustomDatabaseException;
import com.dsp.plot.exception.ResourceNotFoundException;
import com.dsp.plot.mapper.PlotInputMapper;
import com.dsp.plot.mapper.RequestMapper;
import com.dsp.plot.onlyoffice.DocumentConfigDTO;
import com.dsp.plot.onlyoffice.DocumentRequestDTO;
import com.dsp.plot.onlyoffice.service.OnlyOfficeService;
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
    private static final String LS = "LS";
    private static final String RS = "RS";
    
    @Autowired
    private HttpServletRequest httpRequest;
    @Autowired
    private UserUtils utils;
    // only office Editor
    @Autowired
    private OnlyOfficeService onlyOfficeService;
    @Autowired
    private PlotGenerateRepository repository;
    @Autowired
    private MastersRSSRepository mastersRSSRepository;

    @Autowired
    private PlotInputMapper plotinputMap;
    @Autowired
    private CodeRepository codeRepository;
    @Autowired
    private MinistriesRepository ministriesRepository;
    @Autowired
    private MasterLSSRepository masterLSSRepository;
    @Autowired
    private MasterLsRepository lsRepository;
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

    @Autowired
    private InputWorkflowRepository inputHistoryRepository;

    @Transactional
    @Override
    public PlotInputDTO save(PlotInputDTO plotinputDTO) {
        if (plotinputDTO.getStatus() == null) {
            plotinputDTO.setStatus("Submitted");
        }
        log.debug("Entering savePlotInput with PlotInputDTO: {}", plotinputDTO);
        String house = houseAbbr();
        if (RS.equalsIgnoreCase(house)) {
            if (plotinputDTO.getSubmissionDate() != null && plotinputDTO.getProposedDateOfLaying() != null) {
                long daysDifference = ChronoUnit.DAYS.between(plotinputDTO.getSubmissionDate(),
                        plotinputDTO.getProposedDateOfLaying());
                if (daysDifference < 3) {
                    log.debug("Date of Submission must be at least 3 days before the Proposed Date Of Laying",
                            plotinputDTO);
                    throw new IllegalArgumentException(
                            "Date of Submission must be at least 3 days before the Proposed Date Of Laying.");
                }
            }

        } else {
            if (plotinputDTO.getSubmissionDate() != null && plotinputDTO.getProposedDateOfLaying() != null) {
                long daysDifference = ChronoUnit.DAYS.between(plotinputDTO.getSubmissionDate(),
                        plotinputDTO.getProposedDateOfLaying());
                if (daysDifference < 2) {
                    log.debug("Date of Submission must be at least 2 days before the Proposed Date Of Laying",
                            plotinputDTO);
                    // log.info("Date of Submission must be at least 2 days before the Proposed Date
                    // Of Lying", plotinputDTO);
                    throw new IllegalArgumentException(
                            "Date of Submission must be at least 2 days before the Proposed Date Of Laying.");
                }
            }
        }
        try {

            if (plotinputDTO.getId() != null || StringUtils.isNotBlank(plotinputDTO.getApplicationNo())) {
                // Optional<PlotInput> existEntity =
                // plotinputRepository.findById(plotinputDTO.getId());
                // if (existEntity.isPresent())
                // return update(plotinputDTO.getId(), plotinputDTO);
                // else {
                System.out.println("entered loop");
                Optional<PlotInput> existingEntity = plotinputRepository
                        .findByRefNo(plotinputDTO.getApplicationNo());
                if (existingEntity.isPresent()) {
                    System.out.println("id {}" + existingEntity.get().getId());
                    plotinputDTO.setId(existingEntity.get().getId());
                    return update(existingEntity.get().getId(), plotinputDTO);
                }
                // }
            }
            // Convert DTO to entity
            PlotInput entity = plotinputMap.toEntity(plotinputDTO);
            log.debug("Converted PlotInputDTO to entity: {}", entity);
            // Save PlotInput entity
            SerialNumberRequest serialNumberRequest = new SerialNumberRequest();
            serialNumberRequest.setAppId(entity.getApplicationId());
            if (RS.equalsIgnoreCase(house)) {
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
            } else if (LS.equalsIgnoreCase(house)) {
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
            }
            entity.setRefNo(entity.getApplicationNo());
            entity.setCurrentStatus(entity.getStatus());
            entity.setDocumentDate(entity.getSubmissionDate());
            log.debug("entity: {}", entity);
            // generate content in input
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
            // generate content
            String contentText = formatPaperDetails(entity.getPaperDetails(),
                    optionalAuthority.get().getAuthorityName(), departmentName, abbr,
                    entity.getApplicationNo(), entity.getMinistryId());
            entity.setContent(contentText);
            entity.setHtmlContentEnglish(contentText);
            entity = plotinputRepository.save(entity);
            entity.setRefId(entity.getId());
            // only office related Changes
            System.out.println("plot input " + entity);
            entity = plotinputRepository.save(entity);
            updateAppNo(entity.getDocuments(), entity.getApplicationNo());
            createInputHistory(entity, "Created", entity.getPendingAt());
            PlotInputDTO responseDTO = plotinputMap.toDTO(entity);
            log.debug("Returning PlotInputDTO: {}", responseDTO);
            if (!StringUtils.isBlank(plotinputDTO.getApplicationNo())) {
                // Get the Draft ID from draft table using appNo
                String res = deleteDraftEntry(plotinputDTO.getApplicationNo());
                System.out.println("res:::::::" + res);
                // /delete-save-as-draft
            }
            return responseDTO;
        } catch (DataIntegrityViolationException e) {
            log.error("Database constraint violated: {}", e.getMessage());
            throw new CustomDatabaseException("Invalid data input", e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new CustomDatabaseException("An error occurred while saving plot input", e);
        }
    }

    // only office Related Changes
    public DocumentRequestDTO getConfigReq(PlotInput plotInput, String house) {
       
        return DocumentRequestDTO.builder()
                .empty(0)
                .refId(plotInput.getId())
                .edit(true)
                .dsp2Key(plotInput.getOnlyOfficeDsp2Key())
                .fileName("plotInput.docx")
                .templateId("0")
                .applicationId(String.valueOf(plotInput.getApplicationId()))
                .build();
    }

    // for the input history
    private void createInputHistory(PlotInput input, String actionTaken, String pendingWith) {
        InputWorkflowHistory history = InputWorkflowHistory.builder()
                .applicationId(input.getApplicationId())
                .inputInstanceId(input.getId())
                .actionTakenBy(input.getUpdatedBy())
                .actionTakenByName(getActionTakenBy())
                .actionTakenBy(utils.getUserId())
                .status(input.getStatus())
                .actionTaken(actionTaken)
                .applicationRecordId(input.getId())
                .pendingWith(null)
                .pendingWithName(pendingWith)
                .actionTakenOn(input.getCreatedAt())
                .commentForDisplay(input.getRemarks())
                // .ipAddress(getClientIp(httpRequest)) // optional, if you have request
                .referenceFiles(input.getDocuments()) // mirrors Bulletin2: referenceFiles(...)
                .build();

        inputHistoryRepository.save(history);
    }

    private String getActionTakenBy() {
        Optional<BaseUser> userOpt = userRepository.findById(utils.getUserId());
        BaseUser user = userOpt.orElse(null);
        if (user == null)
            return null;

        return user.getFirstName() + " " +
                (user.getMiddleName() != null ? user.getMiddleName() + " " : "") +
                (user.getLastName() != null ? user.getLastName() : "");
    }

    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
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
            String applicationNo, Integer ministryCode) {
        if (Authenticator == null) {
            throw new IllegalArgumentException("Authenticator details cannot be null.");
        }
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("<html><body>");
        // Authenticator details (always included)

        contentBuilder.append("<b>")
                .append(escapeHtml(Authenticator));
        // if (!department.isBlank()) {
        // contentBuilder.append(" (")
        // .append(escapeHtml(department))
        // .append(")");
        // }
        contentBuilder.append("</b> to lay on the Table:<br/>");

        // Check if paper details exist
        if (paperDetails == null || paperDetails.isEmpty()) {
            // System.out.println("<b>No paper details available.</b> Details");
            contentBuilder.append("<b>No paper details available.</b>");
        } else {

            contentBuilder.append("<ol>"); // Start ordered list

            for (Map<String, Object> paper : paperDetails) {
                if (paper == null || paper.isEmpty())
                    continue;

                String formattedString = "";

                switch (abbr) {
                    case "PLOT_REPORT":
                        formattedString = getFormattedReportDetails(paper, applicationNo);
                        break;
                    case "PLOT_NOTIFICATION":
                        formattedString = getFormattedNotificationDetails(paper, applicationNo, ministryCode,
                                department);
                        break;
                    case "PLOT_OTHER":
                        formattedString = getFormattedOtherPaperDetails(paper, applicationNo);
                        break;
                    default:
                        break;
                }

                if (StringUtils.isNotBlank(formattedString)) {
                    contentBuilder.append("<li>").append(formattedString).append("</li>");
                }
            }

            contentBuilder.append("</ol>"); // End ordered list
        }
        contentBuilder.append("</body></html>");
        return contentBuilder.toString();
    }

    // Report
    private String getFormattedReportDetails(Map<String, Object> paper, String applicationNo) {
        Object paperDocs = paper.get("documents");
        List<String> docLangType = new ArrayList<>();
        if (paperDocs instanceof List<?>) {
            List<?> tempList = (List<?>) paperDocs;
            // Ensure all elements are of type Map
            boolean allMaps = tempList.stream().allMatch(item -> item instanceof Map);
            if (allMaps) {

                List<Map<String, Object>> docs = tempList.stream()
                        .map(item -> (Map<String, Object>) item)
                        .toList();
                docLangType = docs.stream()
                        .map(doc -> (String) doc.get("fieldName"))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                updateAppNo(docs, applicationNo);
            }
        }
        boolean containsHindi = docLangType.stream()
                .anyMatch(name -> name.toLowerCase().contains("hindi"));
        boolean containsEnglish = docLangType.stream()
                .anyMatch(name -> name.toLowerCase().contains("english"));

        if (containsEnglish) {
            System.out.println("Document contains English field.");
            // Add your logic here
        }
        if (containsHindi) {
            System.out.println("Document contains Hindi field.");
            // Add your logic here
        }
        Object paperNameData = paper.get("paperName");
        Long paperType = null;
        String paperTypeTitle = "";
        if (paperNameData instanceof Map<?, ?> paperNameMap) {
            Object valueObj = paperNameMap.get("value"); // Extract "value" field
            Object labelObj = paperNameMap.get("label"); // Extract "value" field
            if (valueObj instanceof Number numberValue) { // Ensure it's a number before parsing
                paperType = numberValue.longValue();
            }
            if (labelObj instanceof String stringValue) {
                paperTypeTitle = stringValue;
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
        String template = "";
        String languageType = "";
        if (containsEnglish) {
            if (languageType.length() < 1) {
                languageType += "( ";
            }
            languageType += "English";
            if (!containsHindi) {
                languageType += " Versions )";
            }
        }
        if (containsHindi) {
            if (!containsHindi) {
                languageType += "( ";
            } else {
                languageType += " And ";
            }
            languageType += "Hindi Versions )";
        }
        switch (abbr) {
            case "PLOT_RPT_1":
                template = "A copy of the {0} {1} of {2}, laid under Article {4}, Section {3} for {5} for the year {6}";
                String nameAndPlace = escapeHtml(paper.get("constitutionalBody")) + ", "
                        + escapeHtml(paper.get("headquarters"));
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTypeTitle),
                        escapeHtml(languageType),
                        escapeHtml(paper.get("paperTiltle")),
                        escapeHtml(paper.get("section")),
                        escapeHtml(paper.get("laidUnderArticle")),
                        nameAndPlace,
                        escapeHtml(paper.get("year")));
                break;
            case "PLOT_RPT_2":
                template = "A copy of the {0} {10} of the {1}, for the type of {2} and No. {3}, {4}," +
                        "laid under Article {6}, Section {5} in year {7}";

                if (paper.get("informedToMinistryOfParliamentaryAffarisOnDate") != null) {
                    template += ", informed in Office on {8}";
                }
                if (paper.get("DateOnWhichforwardedToLSSecretariat") != null) {
                    template += ", and forwarded to LS Secretariat on {9}";
                }

                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTypeTitle),
                        escapeHtml(paper.get("paperTiltle")),
                        escapeHtml(paper.get("reportType")),
                        escapeHtml(paper.get("reportNumber")),
                        escapeHtml(paper.get("ministryOrDepartment")),
                        escapeHtml(paper.get("section")),
                        escapeHtml(paper.get("laidUnderArticle")),
                        escapeHtml(paper.get("year")),
                        escapeHtml(paper.get("informedToMinistryOfParliamentaryAffarisOnDate")),
                        escapeHtml(paper.get("DateOnWhichforwardedToLSSecretariat")),
                        escapeHtml(languageType));

                break;
            case "PLOT_RPT_3":
                template = "A copy of the {0} {1} of the {2}, {3} for the year {4}, laid under Section {6}, Section {5}";
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTypeTitle),
                        escapeHtml(languageType),
                        escapeHtml(paper.get("paperTiltle")),
                        escapeHtml(paper.get("companyHeadquarters")),
                        escapeHtml(paper.get("financialYear")),
                        escapeHtml(paper.get("subSection")),
                        escapeHtml(paper.get("layingSection")));
                break;
            case "PLOT_RPT_4":
                template = "A copy of the {0} {1} of the {2}, in {3} for the year {4}, laid under {5}, Section {6}, under Act {7}";
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTypeTitle),
                        escapeHtml(languageType),
                        escapeHtml(paper.get("paperTiltle")),
                        escapeHtml(paper.get("bankHeadquarters")),
                        escapeHtml(paper.get("financialYear")),
                        escapeHtml(paper.get("subSection")),
                        escapeHtml(paper.get("layingSection")),
                        escapeHtml(paper.get("laidUnderAct")));
                break;
            case "PLOT_RPT_5":
                template = "A copy of the {0} {1} of the {2}, in the {3}, for the year {4}";
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTypeTitle),
                        escapeHtml(languageType),
                        escapeHtml(paper.get("paperTiltle")),
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
    private String getFormattedNotificationDetails(Map<String, Object> paper, String applicationNo,
            Integer ministryCode, String departmentName) {
        Object paperDocs = paper.get("documents");
        List<String> docLangType = new ArrayList<>();

        if (paperDocs instanceof List<?>) {
            List<?> tempList = (List<?>) paperDocs;

            // Ensure all elements are of type Map
            boolean allMaps = tempList.stream().allMatch(item -> item instanceof Map);
            if (allMaps) {
                List<Map<String, Object>> docs = tempList.stream()
                        .map(item -> (Map<String, Object>) item)
                        .toList();
                docLangType = docs.stream()
                        .map(doc -> (String) doc.get("fieldName"))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                updateAppNo(docs, applicationNo);
            }
        }
        boolean containsHindi = docLangType.stream()
                .anyMatch(name -> name.toLowerCase().contains("hindi"));
        boolean containsEnglish = docLangType.stream()
                .anyMatch(name -> name.toLowerCase().contains("english"));
        String languageType = "";
        if (containsEnglish) {
            if (languageType.length() < 1) {
                languageType += "( ";
            }
            languageType += "English";
            if (!containsHindi) {
                languageType += " Versions )";
            }
        }
        if (containsHindi) {
            if (!containsHindi) {
                languageType += "( ";
            } else {
                languageType += " And ";
            }
            languageType += "Hindi Versions )";
        }
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
        // String template = "A copy of the Notification No. {0} of the {1} {2} named
        // {3}, published in {4} {5} on {6}"
        // + " {7}, for the period of {11}, under sub-section {8} of {9} of the {10}
        // Act.";
        // Changes done by Akshay
        String template = "";
        template = "A copy in {0} ";
        String template1 = "of the {1} ({2}) ";

        String template2 = "Notification No. {3} named {4}, published in {5} on {6}"
                + ", under sub-section {7} and section {8} of the Representation of the {9}"
                + ", for {10}.";

        /*
         * 
         * if SubSection N?A
         * 
         * we need to add it as secondly
         * sub section
         */
        // Long publishedId = Long.parseLong(paper.get("publishedIn").toString());
        // Long gazetteDetailsId =
        // Long.parseLong(paper.get("gazetteDetails").toString());
        // LocalDate dateOfPublication =
        // LocalDate.parse(paper.get("dateOfPublication").toString());
        Long publishedId = null;
        if (paper.get("publishedIn") != null && !paper.get("publishedIn").toString().isBlank()) {
            try {
                publishedId = Long.parseLong(paper.get("publishedIn").toString());
            } catch (NumberFormatException e) {
                System.out.println("Invalid publishedIn value: " + paper.get("publishedIn"));
            }
        }
        // Long gazetteDetailsId = null;
        // if (paper.get("gazetteDetails") != null &&
        // !paper.get("gazetteDetails").toString().isBlank()) {
        // try {
        // gazetteDetailsId = Long.parseLong(paper.get("gazetteDetails").toString());
        // } catch (NumberFormatException e) {
        // System.out.println("Invalid gazetteDetails value: " +
        // paper.get("gazetteDetails"));
        // }
        // }
        LocalDate dateOfPublication = null;
        if (paper.get("dateOfPublication") != null && !paper.get("dateOfPublication").toString().isBlank()) {
            try {
                dateOfPublication = LocalDate.parse(paper.get("dateOfPublication").toString());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format for dateOfPublication: " + paper.get("dateOfPublication"));
            }
        }
        String publicationDate = formatDateWithSuffix(dateOfPublication);
        System.out.println(publicationDate);
        String publishedIn = "";
        // String gazetteDetails = "";
        if (publishedId != null) {
            Optional<MasterCode> publishedAbbr = codeRepository.findById(publishedId);
            if (publishedAbbr.isPresent())
                publishedIn = publishedAbbr.get().getCode();
        }
        // if (gazetteDetailsId != null) {
        // Optional<MasterCode> publishedAbbr =
        // codeRepository.findById(gazetteDetailsId);
        // if (publishedAbbr.isPresent())
        // gazetteDetails = publishedAbbr.get().getCode();
        // }
        String ministryName = "";
        if (ministryCode != null) {
            ministryName = ministriesRepository.findByMinistryCode(ministryCode)
                    .or(() -> ministriesRepository.findById(ministryCode.longValue()))
                    .map(Ministries::getMinistryName)
                    .orElse("");

            if (StringUtils.isNotBlank(departmentName)) {
                template1 = " of the Ministry of {1} ({2}) ";
            } else {
                template1 = " of the Ministry of {1} ";
            }
        } else {
            if (StringUtils.isNotBlank(departmentName)) {
                template1 = " of the Department {2} ";
            } else {
                template1 = " ";
            }
        }

        template += template1 + " " + template2;
        System.out.println();
        // template= "A copy in {0} of the {1} ({2}) "
        // +"Notification No. {3} named {4}, published in {5} on {6}"
        // + ", under sub-section {7} and section {8} of the Representation of the {9}"
        // +", {10}.";
        String formattedString = MessageFormat.format(template,
                escapeHtml(languageType),
                escapeHtml(ministryName),
                escapeHtml(departmentName),
                escapeHtml(paper.get("notificationNumber")),
                escapeHtml(paper.get("shortTitle")),
                // escapeHtml(paper.get("paperTitle")),
                escapeHtml(publishedIn),
                escapeHtml(publicationDate),
                // escapeHtml(gazetteDetails),
                escapeHtml(paper.get("issuingSection")),
                escapeHtml(paper.get("layingSection")),
                escapeHtml(paper.get("laidUnderAct")),
                escapeHtml(paper.get("periodSpecified")));
        return formattedString;
    }

    // additional method for changing date format
    public static String formatDateWithSuffix(LocalDate date) {
        int day = date.getDayOfMonth();
        String dayWithSuffix = day + getDaySuffix(day);
        DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM, yyyy");
        String monthYear = date.format(monthYearFormatter);
        return dayWithSuffix + " " + monthYear;
    }

    private static String getDaySuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    // other paper
    private String getFormattedOtherPaperDetails(Map<String, Object> paper, String applicationNo) {
        Object paperDocs = paper.get("documents");
        List<String> docLangType = new ArrayList<>();
        if (paperDocs instanceof List<?>) {
            List<?> tempList = (List<?>) paperDocs;
            // Ensure all elements are of type Map
            boolean allMaps = tempList.stream().allMatch(item -> item instanceof Map);
            if (allMaps) {
                List<Map<String, Object>> docs = tempList.stream()
                        .map(item -> (Map<String, Object>) item)
                        .toList();
                docLangType = docs.stream()
                        .map(doc -> (String) doc.get("fieldName"))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                updateAppNo(docs, applicationNo);
            }
        }
        boolean containsHindi = docLangType.stream()
                .anyMatch(name -> name.toLowerCase().contains("hindi"));
        boolean containsEnglish = docLangType.stream()
                .anyMatch(name -> name.toLowerCase().contains("english"));
        if (containsEnglish) {
            System.out.println("Document contains English field.");
            // Add your logic here
        }
        if (containsHindi) {
            System.out.println("Document contains Hindi field.");
            // Add your logic here
        }
        String languageType = "";
        if (containsEnglish) {
            if (languageType.length() < 1) {
                languageType += "( ";
            }
            languageType += "English";
            if (!containsHindi) {
                languageType += " Versions )";
            }
        }
        if (containsHindi) {
            if (!containsHindi) {
                languageType += "( ";
            } else {
                languageType += " And ";
            }
            languageType += "Hindi Versions )";
        }
        String template = "";
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
                template = "A copy of the {0} {1} of the {2}, for the year {3}";
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTitle),
                        escapeHtml(languageType),
                        escapeHtml(paper.get("title")),
                        escapeHtml(paper.get("financialYear")));
                break;
            case "PLOT_OTH_2":
                Integer ministryCode = null;
                if (paper.get("ministry") != null) {
                    ministryCode = Integer.parseInt(paper.get("ministry").toString());
                }
                System.out.println("ministryCode " + ministryCode);
                Long departmentId = null;

                if (paper.get("department") != null) {
                    departmentId = Long.parseLong(paper.get("department").toString());
                }
                String ministryName = "";
                if (ministryCode != null) {
                    try {
                        Optional<Ministries> ministry = ministriesRepository.findByMinistryCode(ministryCode);
                        ministryName = ministry.get().getMinistryName();
                    } catch (Exception e) {
                        Optional<Ministries> ministry = ministriesRepository.findById(ministryCode.longValue());
                        ministryName = ministry.get().getMinistryName();
                    }
                    if (departmentId != null) {
                        template = "A copy of the {0} {1} of the {2}, of {3} Ministry,{4} Department for the year {5}";
                    } else {
                        template = "A copy of the {0} {1} of the {2}, of {3} Ministry for the year {5}";
                    }
                } else {
                    if (departmentId != null) {
                        template = "A copy of the {0} {1} of the {2}, of {4} Department for the year {5}";
                    } else {
                        template = "A copy of the {0} {1} of the {2}, for the year {5}";
                    }
                }
                String department = "";
                if (departmentId != null) {
                    Optional<Departments> departmentOpt = departmentRepo.findById(departmentId);
                    department = departmentOpt.get().getDepartmentName();
                }
                // Map<String, Object> ministry = (Map<String, Object>)
                // paper.get("ministryDepartmentPSU");
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTitle),
                        escapeHtml(languageType),
                        escapeHtml(paper.get("title")),
                        escapeHtml(ministryName),
                        escapeHtml(department),
                        escapeHtml(paper.get("financialYear")));
                break;
            case "PLOT_OTH_3":
                Integer ministryCode3 = null;
                Long departmentId3 = null;
                if (paper.get("ministry") != null)
                    ministryCode3 = Integer.parseInt(paper.get("ministry").toString());
                System.out.println("ministryCode3 " + ministryCode3);
                System.out.println(paper.get("department"));
                if (paper.get("department") != null)
                    departmentId3 = Long.parseLong(paper.get("department").toString());
                System.out.println("departmentId3 " + departmentId3);

                String ministryName3 = "";
                if (ministryCode3 != null) {
                    try {
                        Optional<Ministries> ministry3 = ministriesRepository.findByMinistryCode(ministryCode3);
                        ministryName3 = ministry3.get().getMinistryName();
                    } catch (Exception e) {
                        Optional<Ministries> ministry3 = ministriesRepository.findById(ministryCode3.longValue());
                        ministryName3 = ministry3.get().getMinistryName();
                    }
                    if (departmentId3 != null) {
                        template = "A copy of the {0} {1} of the {2}, of {3} Ministry,{4} Department for the year {5}";
                    } else {
                        template = "A copy of the {0} {1} of the {2}, of {3} Ministry for the year {5}";
                    }
                } else {
                    if (departmentId3 != null) {
                        template = "A copy of the {0} {1} of the {2}, of {4} Department for the year {5}";
                    } else {
                        template = "A copy of the {0} {1} of the {2}, for the year {5}";
                    }
                }
                System.out.println("template " + template);
                String department3 = "";
                if (departmentId3 != null) {
                    Optional<Departments> departmentOpt3 = departmentRepo.findById(departmentId3);
                    department3 = departmentOpt3.get().getDepartmentName();
                }
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTitle),
                        escapeHtml(languageType),
                        escapeHtml(paper.get("title")),
                        escapeHtml(ministryName3),
                        escapeHtml(department3),
                        escapeHtml(paper.get("financialYear")));
                break;
            case "PLOT_OTH_4":
                Integer ministryCode4 = null;
                if (paper.get("ministry") != null)
                    ministryCode4 = Integer.parseInt(paper.get("ministry").toString());
                System.out.println("ministryCode4 " + ministryCode4);
                // Long departmentId4 = (Long) paper.get("department");
                String ministryName4 = "";
                if (ministryCode4 != null) {
                    try {
                        Optional<Ministries> ministry4 = ministriesRepository.findByMinistryCode(ministryCode4);
                        ministryName3 = ministry4.get().getMinistryName();
                    } catch (Exception e) {
                        Optional<Ministries> ministry4 = ministriesRepository.findById(ministryCode4.longValue());
                        ministryName4 = ministry4.get().getMinistryName();
                    }
                    template = "A copy of the {0} {1} of the {2}, of {3} Ministry, at {4} for the year {5}";
                } else {
                    template = "A copy of the {0} {1} of the {2}, at {4} for the year {5}";
                }
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTitle),
                        escapeHtml(languageType),
                        escapeHtml(paper.get("title")),
                        escapeHtml(ministryName4),
                        escapeHtml(paper.get("bankHeadquarters")),
                        escapeHtml(paper.get("financialYear")));
                break;
            case "PLOT_OTH_5":
                template = "A copy of the {0} {1} of the {2}, On Statement No. {3}, for the year {2}";
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTitle),
                        escapeHtml(languageType),
                        escapeHtml(paper.get("title")),
                        escapeHtml(paper.get("statementNumber")),
                        escapeHtml(paper.get("year")));
                break;
            case "PLOT_OTH_6":
                Integer ministryCode6 = null;
                Long departmentId6 = null;
                // template = "A copy of the {0} {1} of the {1}, for the year {2}";
                if (paper.get("ministry") != null)
                    ministryCode6 = Integer.parseInt(paper.get("ministry").toString());
                System.out.println("ministryCode6 " + ministryCode6);
                if (paper.get("department") != null)
                    departmentId6 = Long.parseLong(paper.get("department").toString());
                String ministryName6 = "";
                if (ministryCode6 != null) {
                    try {
                        Optional<Ministries> ministry6 = ministriesRepository.findByMinistryCode(ministryCode6);
                        ministryName6 = ministry6.get().getMinistryName();
                    } catch (Exception e) {
                        Optional<Ministries> ministry6 = ministriesRepository.findById(ministryCode6.longValue());
                        ministryName6 = ministry6.get().getMinistryName();
                    }
                    if (departmentId6 != null) {
                        template = "A copy of the {0} {1} of the {2}, of {3} Ministry,{4} Department for the year {5}";
                    } else {
                        template = "A copy of the {0} {1} of the {2}, of {3} Ministry for the year {5}";
                    }
                } else {
                    if (departmentId6 != null) {
                        template = "A copy of the {0} {1} of the {2}, of {4} Department for the year {5}";
                    } else {
                        template = "A copy of the {0} {1} of the {2}, for the year {5}";
                    }
                }
                String department6 = "";
                if (departmentId6 != null) {
                    Optional<Departments> departmentOpt6 = departmentRepo.findById(departmentId6);
                    department6 = departmentOpt6.get().getDepartmentName();
                }
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTitle),
                        escapeHtml(languageType),
                        escapeHtml(paper.get("stateUT")),
                        escapeHtml(paper.get("financialYear")));
                break;
            case "PLOT_OTH_7":
                template = "A copy of the {0} {1} of the {2}, required Under Act of {3}, {4} and for the year {5}";
                formattedString = MessageFormat.format(template,
                        escapeHtml(paperTitle),
                        escapeHtml(languageType),
                        escapeHtml(paper.get("title")),
                        escapeHtml(paper.get("requirementUnderAct")),
                        escapeHtml(paper.get("nameOfAct")),
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
        String house = houseAbbr();

        if (status != null && status.equalsIgnoreCase("draft")) {
            if (applicationId != null) {
                List<PlotInput> plotInputs = getDraftedData(applicationId, lokSabhaNo, lokSabhaSessionNo,
                        rajyaSabhaSessionNo, startDate, endDate, status, submissionDate, department, paperType,
                        paperName,
                        proposedDateOfLaying, actualDateOfLaying, ministry);
                for (PlotInput input : plotInputs) {
                    System.out.println("entity");
                    System.out.println(input);
                    if (input != null) {
                        System.out.println("@@@" + input);
                        if (RS.equalsIgnoreCase(house)) {
                            if (input.getRajyaSabhaSessionNo() == null) {
                                input.setRajyaSabhaSessionNo(mastersRSSRepository.findLatestByCreatedBy());
                            }
                            Optional<MastersRajyaSabha> session = mastersRSSRepository
                                    .findById(input.getRajyaSabhaSessionNo());
                            if (session.isPresent())
                                input.setRajyaSabhaSession(session.get().getSessionNumber());
                        }
                        if (LS.equalsIgnoreCase(house)) {
                            if (input.getLokSabhaSessionNo() == null) {
                                input.setLokSabhaSessionNo(masterLSSRepository.findLatestByCreatedBy());
                            }
                            Optional<MastersLokSabhaSession> session = masterLSSRepository
                                    .findById(input.getLokSabhaSessionNo());
                            System.out.println(":::::::" + session.get());
                            if (session.isPresent()) {
                                input.setLokSabhaSession(session.get().getLokSabha().getLokSabhaNumber());
                                input.setLokSabhaSessionNumber(session.get().getSessionNumber());
                            }
                        }
                        System.out.println("@@@" + input);
                        if (input.getMinistryId() != null) {
                            Optional<Ministries> ministriesOptional = ministriesRepository
                                    .findByMinistryCode(input.getMinistryId());
                            input.setMinistries(ministriesOptional.get());
                        }
                        if (input.getNodalOfficerId() != null) {
                            Optional<BaseUser> nodalOptional = userRepository
                                    .findById(input.getNodalOfficerId());
                            input.setNodalOfficerDetails(nodalOptional.get());
                        }
                        if (input.getAuthenticatedById() != null) {
                            Optional<MinistryAuthority> authOptional = ministriesRepository
                                    .findByMinistryAuthId(input.getAuthenticatedById());
                            input.setAuthenticatorDetails(authOptional.get());
                        }
                        if (input.getDepartmentId() != null) {
                            Optional<Departments> departmentOptional = departmentRepo.findById(input.getDepartmentId());
                            input.setDepartments(departmentOptional.get());
                        }
                        if (input.getPaperTypeId() != null) {
                            Optional<MasterCode> paperTypeOptional = codeRepository.findById(input.getPaperTypeId());
                            input.setPaperTypes(paperTypeOptional.get());
                        }
                        System.out.println("@@@" + input);
                        PlotInputDTO draftDto = plotinputMap.toDTO(input);
                        dtoList.add(draftDto);
                    }
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
            for (PlotInput entity : entitys) {
                if (RS.equalsIgnoreCase(house)) {
                    if (entity.getRajyaSabhaSessionNo() != null) {
                        if (entity.getApplicationInstanceId() != null) {
                            PlotGenerate plot = repository
                                    .findById(entity.getApplicationInstanceId()).get();
                            if (!plot.getCurrentStatus().equalsIgnoreCase("returned")
                                    || !plot.getCurrentStatus().equalsIgnoreCase("draft")) {
                                // if (plot.getCurrentStatus().equalsIgnoreCase("published")
                                // || plot.getCurrentStatus().equalsIgnoreCase("approved"))
                                if (!plot.getCurrentStatus().equalsIgnoreCase("Published")) {
                                    if (plot.getCurrentStatus().equalsIgnoreCase("Approved"))
                                        entity.setPendingAt(" ");
                                    entity.setStatus(plot.getCurrentStatus());
                                    entity.setCurrentStatus(plot.getCurrentStatus());
                                } else {
                                    if (entity.getSendToLibrary() != null && entity.getSendToLibrary()) {
                                        entity.setPendingAt(" ");
                                        entity.setStatus(plot.getCurrentStatus());
                                        entity.setCurrentStatus(plot.getCurrentStatus());
                                    }
                                }
                            }
                        }
                        entity = plotinputRepository.save(entity);
                        PlotInputDTO dto = plotinputMap.toDTO(entity);
                        dto.setRequests(getRequests(dto.getId()));
                        dtoList.add(dto);
                    }
                } else if (LS.equalsIgnoreCase(house)) {
                    if (entity.getLokSabhaSessionNo() != null) {
                        if (entity.getApplicationInstanceId() != null) {
                            PlotGenerate plot = repository
                                    .findById(entity.getApplicationInstanceId()).get();
                            if (!plot.getCurrentStatus().equalsIgnoreCase("returned")
                                    || !plot.getCurrentStatus().equalsIgnoreCase("draft")) {
                                // if (plot.getCurrentStatus().equalsIgnoreCase("published")
                                // || plot.getCurrentStatus().equalsIgnoreCase("approved"))
                                if (!plot.getCurrentStatus().equalsIgnoreCase("published")) {
                                    if (plot.getCurrentStatus().equalsIgnoreCase("Approved"))
                                        entity.setPendingAt(" ");
                                    entity.setStatus(plot.getCurrentStatus());
                                    entity.setCurrentStatus(plot.getCurrentStatus());
                                } else {
                                    if (entity.getSendToLibrary() != null && entity.getSendToLibrary()) {
                                        entity.setPendingAt(" ");
                                        entity.setStatus(plot.getCurrentStatus());
                                        entity.setCurrentStatus(plot.getCurrentStatus());
                                    }
                                }
                            }
                        }
                        entity = plotinputRepository.save(entity);
                        PlotInputDTO dto = plotinputMap.toDTO(entity);
                        dto.setRequests(getRequests(dto.getId()));
                        dtoList.add(dto);
                    }
                }
            }
            if (status != null) {
                if (!status.equalsIgnoreCase("draft")) {
                    List<PlotInput> plotInputs = getDraftedData(applicationId, lokSabhaNo,
                            lokSabhaSessionNo,
                            rajyaSabhaSessionNo, startDate, endDate, status, submissionDate, department, paperType,
                            paperName, proposedDateOfLaying, actualDateOfLaying, ministry);
                    for (PlotInput input : plotInputs) {
                        if (input != null) {
                            if (RS.equalsIgnoreCase(house)) {
                                if (input.getRajyaSabhaSessionNo() == null) {
                                    input.setRajyaSabhaSessionNo(mastersRSSRepository.findLatestByCreatedBy());
                                }
                                Optional<MastersRajyaSabha> session = mastersRSSRepository
                                        .findById(input.getRajyaSabhaSessionNo());
                                if (session.isPresent())
                                    input.setRajyaSabhaSession(session.get().getSessionNumber());
                            }
                            if (LS.equalsIgnoreCase(house)) {
                                if (input.getLokSabhaSessionNo() == null) {
                                    input.setLokSabhaSessionNo(masterLSSRepository.findLatestByCreatedBy());
                                }
                                Optional<MastersLokSabhaSession> session = masterLSSRepository
                                        .findById(input.getLokSabhaSessionNo());
                                if (session.isPresent()) {
                                    input.setLokSabhaSession(session.get().getLokSabha().getLokSabhaNumber());
                                    input.setLokSabhaSessionNumber(session.get().getSessionNumber());
                                }
                            }
                            if (input.getMinistryId() != null) {
                                Optional<Ministries> ministriesOptional = ministriesRepository
                                        .findByMinistryCode(input.getMinistryId());
                                input.setMinistries(ministriesOptional.get());
                            }
                            if (input.getDepartmentId() != null) {
                                Optional<Departments> departmentOptional = departmentRepo
                                        .findById(input.getDepartmentId());
                                input.setDepartments(departmentOptional.get());
                            }
                            if (input.getPaperTypeId() != null) {
                                Optional<MasterCode> paperTypeOptional = codeRepository
                                        .findById(input.getPaperTypeId());
                                input.setPaperTypes(paperTypeOptional.get());
                            }
                            if (input.getNodalOfficerId() != null) {
                                Optional<BaseUser> nodalOptional = userRepository
                                        .findById(input.getNodalOfficerId());
                                input.setNodalOfficerDetails(nodalOptional.get());
                            }
                            if (input.getAuthenticatedById() != null) {
                                Optional<MinistryAuthority> authOptional = ministriesRepository
                                        .findByMinistryAuthId(input.getAuthenticatedById());
                                input.setAuthenticatorDetails(authOptional.get());
                            }
                            dtoList.add(plotinputMap.toDTO(input));
                        }
                    }
                }

            } else {
                if (applicationId != null) {
                    List<PlotInput> plotInputs = getDraftedData(applicationId, lokSabhaNo,
                            lokSabhaSessionNo,
                            rajyaSabhaSessionNo, startDate, endDate, status, submissionDate, department, paperType,
                            paperName, proposedDateOfLaying, actualDateOfLaying, ministry);
                    for (PlotInput input : plotInputs) {
                        if (input != null) {
                            if (RS.equalsIgnoreCase(house)) {
                                if (input.getRajyaSabhaSessionNo() == null) {
                                    input.setRajyaSabhaSessionNo(mastersRSSRepository.findLatestByCreatedBy());
                                }
                                Optional<MastersRajyaSabha> session = mastersRSSRepository
                                        .findById(input.getRajyaSabhaSessionNo());
                                if (session.isPresent())
                                    input.setRajyaSabhaSession(session.get().getSessionNumber());
                            }
                            if (LS.equalsIgnoreCase(house)) {
                                if (input.getLokSabhaSessionNo() == null) {
                                    input.setLokSabhaSessionNo(masterLSSRepository.findLatestByCreatedBy());
                                }
                                Optional<MastersLokSabhaSession> session = masterLSSRepository
                                        .findById(input.getLokSabhaSessionNo());
                                System.out.println(":::::::" + session.get());
                                if (session.isPresent()) {
                                    input.setLokSabhaSession(session.get().getLokSabha().getLokSabhaNumber());
                                    input.setLokSabhaSessionNumber(session.get().getSessionNumber());
                                }
                            }
                            System.out.println("@@@" + input);
                            if (input.getMinistryId() != null) {
                                Optional<Ministries> ministriesOptional = ministriesRepository
                                        .findByMinistryCode(input.getMinistryId());
                                input.setMinistries(ministriesOptional.get());
                            }
                            if (input.getDepartmentId() != null) {
                                Optional<Departments> departmentOptional = departmentRepo
                                        .findById(input.getDepartmentId());
                                input.setDepartments(departmentOptional.get());
                            }
                            if (input.getPaperTypeId() != null) {
                                Optional<MasterCode> paperTypeOptional = codeRepository
                                        .findById(input.getPaperTypeId());
                                input.setPaperTypes(paperTypeOptional.get());
                            }
                            // if (input.getAuthority() != null) {
                            // Optional<MasterCode> paperTypeOptional =
                            // codeRepository.findById(input.getPaperTypeId());
                            // input.setPaperTypes(paperTypeOptional.get());
                            // }
                            dtoList.add(plotinputMap.toDTO(input));
                        }
                    }
                }
            }
        }

        return sortByLatestCreatedAt(dtoList);
    }

    @Override
    public List<PlotInputDTO> ministryInputs(Boolean isActive, Boolean draftStatus, Long lokSabhaNo,
            Long lokSabhaSessionNo,
            Long rajyaSabhaSessionNo, LocalDate startDate, LocalDate endDate, String status, LocalDate submissionDate,
            Long department, Long paperType, String paperName, LocalDate proposedDateOfLaying,
            LocalDate actualDateOfLaying, Integer ministry, Boolean sendToLibrary, Long applicationId) {
        // log.debug("Entering getAll Plots");
        List<PlotInputDTO> dtoList = new ArrayList<>();
        String house = houseAbbr();
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
        for (PlotInput entity : entitys) {
            if (RS.equalsIgnoreCase(house)) {
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

                        }
                    }
                    entity = plotinputRepository.save(entity);
                    PlotInputDTO dto = plotinputMap.toDTO(entity);
                    dto.setRequests(getRequests(dto.getId()));
                    dtoList.add(dto);
                }
            } else if (LS.equalsIgnoreCase(house)) {
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

                        }
                    }
                    entity = plotinputRepository.save(entity);
                    PlotInputDTO dto = plotinputMap.toDTO(entity);
                    dto.setRequests(getRequests(dto.getId()));
                    dtoList.add(dto);
                }
            }
        }
        return sortByApplicationNoDesc(dtoList);

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
                        PlotInput inputEntity = plotinputMap.mapDraftToEntity(dto, lokSabhaNo, lokSabhaSessionNo,
                                rajyaSabhaSessionNo, startDate, endDate, status, submissionDate,
                                department, paperType, paperName, proposedDateOfLaying,
                                actualDateOfLaying, ministry);
                        System.out.println(inputEntity);
                        if (inputEntity != null)
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

    public List<PlotInputDTO> sortByLatestCreatedAt(List<PlotInputDTO> dtoList) {
        // return dtoList.stream()
        // .sorted(Comparator.comparing(PlotInputDTO::getSubmissionDate, Comparator
        // .nullsLast(Comparator.reverseOrder())))
        // .collect(Collectors.toList());

        return dtoList.stream()
                .filter(dto -> dto.getSubmissionDate() != null && dto.getApplicationNo() != null)
                .sorted(Comparator
                        .comparing(PlotInputDTO::getSubmissionDate, Comparator.reverseOrder())
                        .thenComparing(PlotInputDTO::getApplicationNo, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public List<PlotInputDTO> sortByApplicationNoDesc(List<PlotInputDTO> dtoList) {
        return dtoList.stream()
                .sorted(Comparator.comparing(PlotInputDTO::getApplicationNo,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
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
        String house = houseAbbr();
        for (PlotInput entity : entitys) {
            if (RS.equalsIgnoreCase(house)) {
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
                        }
                    }
                    PlotInputDTO dto = plotinputMap.toDTO(entity);
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
            } else if (LS.equalsIgnoreCase(house)) {
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
                        }
                    }
                    PlotInputDTO dto = plotinputMap.toDTO(entity);
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
        PlotInputDTO dto = plotinputMap.toDTO(dBEntity);
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
        PlotInput dbEntityNew = plotinputMap.toEntity(plotinputDTO);
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
                    dbEntity.getApplicationNo(), plotinputDTO.getMinistryId());
            dbEntity.setContent(contentText);
            dbEntity.setHtmlContentEnglish(contentText);
        }
        // Save the updated PlotInput
        String house = houseAbbr();
        if (dbEntityNew.getStatus().equalsIgnoreCase("returned")) {
            Long userId = utils.getUserIdFromAuthentication();
            Optional<BaseUser> loginUser = userRepository.findById(userId);
            Request returnRequest = Request.builder()
                    .plotInputId(id)
                    .request(plotinputDTO.getRemarks())
                    .userName(loginUser.get().getUsername())
                    .isAlreadyReturned(true)
                    .build();
            // // Save all updated requests in a single batch operation
            requestRepository.save(returnRequest);
            List<Request> listOfRequests = requestRepository.findByPlotInputIdAndIsActive(id, true);
            listOfRequests.forEach(req -> {
                req.setIsAlreadyReturned(true);
            });
            requestRepository.saveAll(listOfRequests);
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
                if (RS.equalsIgnoreCase(house)) {
                    dbEntity.setPendingAt("Table Office");
                } else if (LS.equalsIgnoreCase(house)) {
                    dbEntity.setPendingAt("Table Office A");
                }
            }
        } else {

            if (RS.equalsIgnoreCase(house)) {
                dbEntity.setPendingAt("Table Office");
            } else if (LS.equalsIgnoreCase(house)) {
                dbEntity.setPendingAt("Table Office A");
            }
        }
        System.out.println("#####  " + dbEntity.getActualDateOfLaying());
        if (dbEntity.getActualDateOfLaying() != null) {
            // Get ministry based on ministry code
            Ministries ministry = ministriesRepository
                    .findByMinistryCode(dbEntity.getMinistryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No ministry found with ministry code: " + dbEntity.getMinistryId()));
            Optional<PlotDateChart> optionalDateChart;
            // Fetch PlotDateChart based on houseId
            if (RS.equalsIgnoreCase(house)) {
                optionalDateChart = plotDateChartRepository.findByMinistryIdAndRajyaSabhaSessionNo(
                        ministry.getId(), dbEntity.getRajyaSabhaSessionNo());
            } else if (LS.equalsIgnoreCase(house)) {
                optionalDateChart = plotDateChartRepository.findByMinistryIdAndLokSabhaNoAndLokSabhaSessionNo(
                        ministry.getId(), dbEntity.getLokSabhaNo(), dbEntity.getLokSabhaSessionNo());
            } else {
                throw new IllegalArgumentException("Invalid house: " + house);
            }
            if (optionalDateChart.isPresent()) {
                PlotDateChart dateChartEntity = optionalDateChart.get();
                for (Map<String, Object> date : dateChartEntity.getDates()) {
                    Object proposedDateObj = date.get("proposedDateOfLaying");
                    if (proposedDateObj != null && !proposedDateObj.toString().isEmpty()) {
                        try {
                            LocalDate proposedDate = LocalDate.parse(proposedDateObj.toString());
                            if (proposedDate.equals(dbEntity.getProposedDateOfLaying())) {
                                Object actualDateObj = date.get("actualDateOfLaying");
                                if (actualDateObj == null || actualDateObj.toString().isEmpty()) {
                                    String actualDateTOUpdate = (dbEntity.getActualDateOfLaying()).toString();
                                    date.put("actualDateOfLaying", actualDateTOUpdate);
                                }
                            }
                        } catch (DateTimeParseException e) {
                            throw new RuntimeException("Invalid proposedDateOfLaying format: " + proposedDateObj, e);
                        }
                    }
                }
                plotDateChartRepository.save(dateChartEntity);
            }
            // Save updated date chart
        }

        // for the input status
        dbEntity.setCurrentStatus(dbEntity.getStatus());

        dbEntity.setCurrentStatus(dbEntity.getStatus());
        dbEntity.setDocumentDate(dbEntity.getSubmissionDate());
        dbEntity.setHtmlContentEnglish(dbEntity.getContent());
        dbEntity.setApplicationInstanceId(dbEntity.getGeneratedPlotId());

        PlotInput savedInput = plotinputRepository.save(dbEntity);
        if (dbEntity.getApplicationInstanceId() != null
                && dbEntity.getSendToLibrary() != null && dbEntity.getSendToLibrary()) {
            PlotGenerate plot = repository
                    .findById(dbEntity.getApplicationInstanceId()).get();
            plot.setSendToLibrary(dbEntity.getSendToLibrary());
            plot = repository.save(plot);
        }
        // Prepare the response DTO
        PlotInputDTO dto = plotinputMap.toDTO(savedInput);
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
            entity.setIsAlreadyReturned(false);
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
        PlotDateChart entity = plotinputMap.dateChartMapToEntity(dto);
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
            return plotinputMap.dateChartMapToDTO(existingEntity);
        } else {

            entity = plotDateChartRepository.save(entity);
            return plotinputMap.dateChartMapToDTO(entity);
        }
    }

    @Override
    public PlotDateChartDTO getDateChartById(Long id) {
        // TODO Auto-generated method stub
        PlotDateChart savedEntity = plotDateChartRepository.findById(id).get();
        return plotinputMap.dateChartMapToDTO(savedEntity);
    }

    public String houseAbbr() {
        return plotinputRepository.fetchHouseAbbrByhouseId(getHouseId());
    }

    @Override
    public List<PlotDateChartDTO> getAllDateCharts(String lokSabhaNo, String lokSabhaSessionNo, String rajyaSabhaNo,
            Boolean isActive, LocalDate startDate, LocalDate endDate, String status, Long ministryId, String groupName,
            LocalDate dateofSubmissionOfPapers, LocalDate actualDateOfLaying) {
        String[] sessionRoman = { "0", "I", "II", "III", "IV", "V", "VI", "VII", "VIII" };
        Long lokSabhaNoId = null;
        Long lokSabhaSessionNoId = null;
        Long rajyaSabhaSessionNoId = null;

        String house = houseAbbr();
        if (isActive == null) {
            isActive = true;
        }
        Specification<PlotDateChart> spec = Specification.where(null);
        spec = spec.and(DateChartSpec.isActive(isActive));
        if (lokSabhaSessionNo == null || lokSabhaSessionNo.isEmpty() || lokSabhaSessionNo.isBlank()) {
            spec = spec.and(DateChartSpec.hasLokSabhaSessionNo(null));
        } else {
            try {
                // Long sessionIndex=Long.parseLong(lokSabhaSessionNo);
                int sessionIndex = Integer.parseInt(lokSabhaSessionNo);
                String session = sessionRoman[sessionIndex];
                System.out.println("Session no " + session);
                System.out.println("ls no " + lokSabhaNo);
                String lsno = "%" + lokSabhaNo + "%";
                System.out.println("ls no " + lsno);
                Optional<MastersLokSabha> lokSabha = lsRepository.findByLokSabhaNumber(lokSabhaNo);
                if (lokSabha.isEmpty())
                    throw new ResourceNotFoundException(
                            "Specified ls no: " + lokSabhaNo + " is not available in database ");
                System.out.println("lokSabha " + lokSabha.get());
                lokSabhaNoId = lokSabha.get().getId();
                Optional<MastersLokSabhaSession> lokSabhaSession = masterLSSRepository
                        .findBySessionNumberAndLokSabhaMasterId("%" + session + "%", lokSabha.get().getId());
                System.out.println("lokSabhaSession " + lokSabhaSession.get());
                lokSabhaSessionNoId = lokSabhaSession.get().getId();
                spec = spec.and(DateChartSpec.hasLokSabhaSessionNo(lokSabhaSession.get().getId()));
            } catch (Exception e) {
                System.out.println("Mentioned LokSabhaSession no is not in Database");
            }
        }
        if (rajyaSabhaNo == null || rajyaSabhaNo.isEmpty() || rajyaSabhaNo.isBlank()) {
            spec = spec.and(DateChartSpec.hasRajyaSabhaSessionNo(null));
        } else {
            try {
                System.out.println("rajyaSabha ::" + rajyaSabhaNo);
                Optional<MastersRajyaSabha> rajyaSabha = mastersRSSRepository.findBySessionNumber(rajyaSabhaNo);
                System.out.println("rajyaSabha ::" + rajyaSabha.get());
                spec = spec.and(DateChartSpec.hasRajyaSabhaSessionNo(rajyaSabha.get().getId()));
                rajyaSabhaSessionNoId = rajyaSabha.get().getId();
            } catch (Exception e) {
                System.out.println("Mentioned RajyaSabha no is not in Database");
            }
        }
        List<PlotDateChart> listOfDateCharts = plotDateChartRepository.findAll(spec);
        List<Long> existingMinistries = listOfDateCharts.stream()
                .map(PlotDateChart::getMinistryId)
                .collect(Collectors.toList());
        System.out.println("existingMinistries ::" + existingMinistries);
        String response = questionBranchApi(lokSabhaNo, lokSabhaSessionNo, rajyaSabhaNo, existingMinistries, house,
                lokSabhaNoId, lokSabhaSessionNoId, rajyaSabhaSessionNoId);
        if (ministryId != null) {
            spec = spec.and(DateChartSpec.hasMinistryId(ministryId));
        }
        if (groupName != null) {
            spec = spec.and(DateChartSpec.hasGroupName(groupName));
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        listOfDateCharts = plotDateChartRepository.findAll(spec);
        List<PlotDateChartDTO> dtoList = new ArrayList<>();
        for (PlotDateChart entity : listOfDateCharts) {
            boolean shouldAdd = true;
            if (RS.equalsIgnoreCase(house)) {
                if (entity.getRajyaSabhaSessionNo() == null) {
                    shouldAdd = false;
                    continue;
                }
            } else if (LS.equalsIgnoreCase(house)) {
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
                PlotDateChartDTO dto = plotinputMap.dateChartMapToDTO(entity);
                dto.setMinistryCode(getMinistryCodeById(dto.getMinistryId()));
                dtoList.add(dto);
                // Stop checking further dates for this entity once added
            }
        }
        return dtoList;
    }

    private Integer getMinistryCodeById(Long id) {
        Optional<Ministries> ministry = ministriesRepository.findById(id);
        if (ministry.isEmpty()) {
            throw new ResourceNotFoundException("Ministry Entry is not Available for id:" + id);
        }
        return ministry.get().getMinistryCode();
    }

    private String getJwtTokenOrThrow() {
        String token = getTokenFromRequest();
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("JWT Token is missing. Cannot proceed with the API request.");
        }
        return token;
    }

    private <T> List<T> fetchFromApi(String url, String token, ParameterizedTypeReference<List<T>> typeRef) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        System.out.println("url " + url);
        try {
            return restTemplate.exchange(url, HttpMethod.GET, requestEntity, typeRef).getBody();
        } catch (HttpClientErrorException e) {
            System.err.println("API Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to call external API", e);
        }
    }

    private List<Map<String, Object>> parseDates(List<String> dateStrings, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateStrings.stream()
                .map(String::trim)
                .map(date -> {
                    LocalDate proposedDate = LocalDate.parse(date, formatter);
                    LocalDate submissionDate = proposedDate.minusDays(2);
                    Map<String, Object> dateMap = new HashMap<>();
                    dateMap.put("proposedDateOfLaying", proposedDate.toString());
                    dateMap.put("dateofSubmissionOfPapers", submissionDate.toString());
                    dateMap.put("actualDateOfLaying", "");
                    return dateMap;
                })
                .collect(Collectors.toList());
    }

    private void processMinistries(List<String> ministries, String groupName, List<Long> existingMinistries,
            List<Map<String, Object>> datesList, String lsSessionNo,
            String rsSessionNo, boolean isRajyaSabha, List<PlotDateChart> entityList, Long lokSabhaNoId,
            Long lokSabhaSessionNoId, Long rajyaSabhaSessionNoId) {
        for (String ministry : ministries) {
            System.out.println("ministry " + ministry);
            ministry = ministry.trim().toUpperCase();
            if (ministry.equalsIgnoreCase("communications")) {
                ministry = "COMMUNICATION";
            }

            ministriesRepository.findByMinistryName(ministry).ifPresent(ministryEntity -> {
                Long ministryId = ministryEntity.getId();
                if (existingMinistries.contains(ministryId)) {
                    List<PlotDateChart> existingDateCharts = plotDateChartRepository.findByMinistryId(ministryId);
                    for (PlotDateChart chart : existingDateCharts) {
                        System.out.println("date from api  " + datesList);
                        System.out.println("date alreadyin Date Chart " + chart);
                        boolean isSessionNull = isRajyaSabha ? chart.getLokSabhaSessionNo() == null
                                : chart.getRajyaSabhaSessionNo() == null;
                        if (isSessionNull && chart.getDates().stream()
                                .noneMatch(d -> d.get("actualDateOfLaying") != null &&
                                        !d.get("actualDateOfLaying").toString().trim().isEmpty())) {

                            chart.setDates(updateDatesListWithLatestProposedDates(datesList, chart.getDates()));
                            entityList.add(chart);
                            return;
                        }
                    }
                    return; // Skip adding new if existing valid found
                }

                PlotDateChartDTO dto = PlotDateChartDTO.builder()
                        .dates(datesList)
                        .groupName(groupName)
                        .ministryId(ministryId)
                        .build();

                if (isRajyaSabha) {
                    dto.setRajyaSabhaSessionNo(rajyaSabhaSessionNoId);
                } else {
                    dto.setLokSabhaSessionNo(lokSabhaSessionNoId);
                    dto.setLokSabhaNo(lokSabhaNoId);

                }

                PlotDateChart entity = plotinputMap.dateChartMapToEntity(dto);
                entityList.add(entity);
            });
        }
    }

    public List<Map<String, Object>> updateDatesListWithLatestProposedDates(
            List<Map<String, Object>> datesList,
            List<Map<String, Object>> chart) {

        System.out.println("datesList  " + datesList);
        System.out.println("chart  " + chart);

        int minSize = Math.min(datesList.size(), chart.size());

        // Step 1: Compare aligned indices and prefer latest proposedDateOfLaying
        for (int i = 0; i < minSize; i++) {
            Map<String, Object> datesEntry = datesList.get(i);
            Map<String, Object> chartEntry = chart.get(i);

            Object datesPropObj = datesEntry.get("proposedDateOfLaying");
            Object chartPropObj = chartEntry.get("proposedDateOfLaying");

            String datesPropStr = datesPropObj != null ? datesPropObj.toString().trim() : "";
            String chartPropStr = chartPropObj != null ? chartPropObj.toString().trim() : "";

            if (!datesPropStr.isEmpty() && !chartPropStr.isEmpty()
                    && isValidDate(datesPropStr) && isValidDate(chartPropStr)) {

                LocalDate datesDate = LocalDate.parse(datesPropStr);
                LocalDate chartDate = LocalDate.parse(chartPropStr);

                if (chartDate.isAfter(datesDate)) {
                    datesList.set(i, new HashMap<>(chartEntry));
                }
            } else {
                if (!isValidDate(chartPropStr)) {
                    System.out.println("Invalid chart proposedDateOfLaying: " + chartPropStr);
                }
                if (!isValidDate(datesPropStr)) {
                    System.out.println("Invalid datesList proposedDateOfLaying: " + datesPropStr);
                }
            }
        }
        // Step 2: Append extra entries from chart to datesList
        for (int i = minSize; i < chart.size(); i++) {
            Map<String, Object> chartEntry = chart.get(i);
            Object chartPropObj = chartEntry.get("proposedDateOfLaying");
            String chartPropStr = chartPropObj != null ? chartPropObj.toString().trim() : "";
            if (chartPropStr.isEmpty() || isValidDate(chartPropStr)) {
                datesList.add(new HashMap<>(chartEntry));
            } else {
                System.out.println("Skipped invalid extra chart entry at index " + i + ": " + chartPropStr);
            }
        }

        return datesList;
    }

    // Helper method to validate date strings
    private boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    // public List<Map<String, Object>> updateDatesListWithLatestProposedDates(
    // List<Map<String, Object>> datesList,
    // List<Map<String, Object>> chart) {
    // System.out.println(" datesList " + datesList);
    // System.out.println(" chart " + chart);
    // int minSize = Math.min(datesList.size(), chart.size());

    // // Step 1: Compare aligned indices and prefer latest proposedDateOfLaying
    // for (int i = 0; i < minSize; i++) {
    // Map<String, Object> datesEntry = datesList.get(i);
    // Map<String, Object> chartEntry = chart.get(i);

    // Object datesPropObj = datesEntry.get("proposedDateOfLaying");
    // Object chartPropObj = chartEntry.get("proposedDateOfLaying");

    // if (datesPropObj != null && chartPropObj != null &&
    // !datesPropObj.toString().isBlank() && !chartPropObj.toString().isBlank()) {

    // LocalDate datesDate = LocalDate.parse(datesPropObj.toString());
    // LocalDate chartDate = LocalDate.parse(chartPropObj.toString());

    // if (chartDate.isAfter(datesDate)) {
    // datesList.set(i, new HashMap<>(chartEntry));
    // }
    // }
    // }

    // // Step 2: Append extra entries from chart to datesList
    // for (int i = minSize; i < chart.size(); i++) {
    // datesList.add(new HashMap<>(chart.get(i)));
    // }

    // return datesList;
    // }

    public String questionBranchApi(String lokSabhaNo, String lokSabhaSessionNo, String rajyaSabhaNo,
            List<Long> existingMinistries, String house, Long lokSabhaNoId, Long lokSabhaSessionNoId,
            Long rajyaSabhaSessionNoId) {
        List<PlotDateChart> entityList = new ArrayList<>();
        String token = getJwtTokenOrThrow();

        if (RS.equalsIgnoreCase(house)) {
            // Rajya Sabha
            String url = sansadUrl + "/api_rs/question/ministry-group-sitting?session=" + rajyaSabhaNo;
            List<QuesBranchApiDtoRS> rsDtos = fetchFromApi(url, token, new ParameterizedTypeReference<>() {
            });
            for (QuesBranchApiDtoRS dto : rsDtos) {
                List<Map<String, Object>> datesList = parseDates(dto.getSittingDates(), "yyyy-MM-dd HH:mm:ss.S");
                processMinistries(dto.getMinistries(), dto.getGroup(), existingMinistries, datesList,
                        null, rajyaSabhaNo, true, entityList, lokSabhaNoId, lokSabhaSessionNoId, rajyaSabhaSessionNoId);
            }
        } else {
            // Lok Sabha
            String url = sansadUrl + "/integration-gateway/question-branch-api/get-question-chart?lsNo="
                    + lokSabhaNo + "&sesNo=" + lokSabhaSessionNo;
            System.out.println("url " + url);
            List<QuesBranchApiDto> lsDtos = fetchFromApi(url, token, new ParameterizedTypeReference<>() {
            });
            for (QuesBranchApiDto dto : lsDtos) {
                List<Map<String, Object>> datesList = parseDates(
                        Arrays.asList(dto.getDateOfAnsweringQuestions().split(",")),
                        "dd MMMM yyyy");
                processMinistries(Arrays.asList(dto.getMinistriesDepartments().split(";")), dto.getGroup(),
                        existingMinistries, datesList, lokSabhaSessionNo, null, false, entityList, lokSabhaNoId,
                        lokSabhaSessionNoId, rajyaSabhaSessionNoId);
            }
        }
        System.out.println("entity list " + entityList);
        plotDateChartRepository.saveAll(entityList);
        return "saved";
    }

    // delete draft entry

    public String deleteDraftEntry(String applicationNo) {
        String url = draftUrl + "/delete-save-as-draft?applicationNo=" + applicationNo;
        // String url = "http://localhost:8087/delete-save-as-draft?applicationNo=" +
        // applicationNo;

        String token = getTokenFromRequest();
        if (token == null) {
            throw new IllegalStateException("Authorization token is missing");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers); // No body needed for DELETE request
        try {
            ResponseEntity<GenericResponse> response = restTemplate.exchange(url, HttpMethod.DELETE, entity,
                    GenericResponse.class);

            System.out.println("@@@");
            System.out.println(response);
            if (response.getBody() != null && response.getBody().isSuccess()) {
                return response.getBody().getMessage(); // Return success message
            } else {
                return "Failed to delete draft entry";
            }
        } catch (Exception e) {
            System.err.println("Error during API call: " + e.getMessage());
            // throw new RuntimeException("Failed to delete draft entry", e);
            return "Error in Deleting Draft Entry ";
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
