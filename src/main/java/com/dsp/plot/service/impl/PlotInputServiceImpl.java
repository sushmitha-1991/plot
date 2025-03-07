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
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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

import com.dsp.plot.dto.PlotDateChartDTO;
import com.dsp.plot.dto.PlotInputDTO;
import com.dsp.plot.dto.QuesBranchApiDto;
import com.dsp.plot.dto.QuesBranchApiDtoRS;
import com.dsp.plot.dto.RequestDTO;
import com.dsp.plot.dto.SerialNumberRequest;
import com.dsp.plot.dto.SerialNumberResponse;
import com.dsp.plot.dto.UserDetailsDTO;
import com.dsp.plot.entity.BaseUser;
import com.dsp.plot.entity.MasterCode;
import com.dsp.plot.entity.MastersLokSabhaSession;
import com.dsp.plot.entity.MastersRajyaSabha;
import com.dsp.plot.entity.Ministries;
import com.dsp.plot.entity.PlotDateChart;
import com.dsp.plot.entity.PlotHistory;
import com.dsp.plot.entity.PlotInput;
import com.dsp.plot.entity.Request;
import com.dsp.plot.entity.specification.DateChartSpec;
import com.dsp.plot.entity.specification.PlotInputSpec;
import com.dsp.plot.exception.ResourceNotFoundException;
import com.dsp.plot.filter.JwtFilter;
import com.dsp.plot.mapper.PlotInputMapper;
import com.dsp.plot.mapper.RequestMapper;
import com.dsp.plot.repository.CodeRepository;
import com.dsp.plot.repository.MasterLSSRepository;
import com.dsp.plot.repository.MastersRSSRepository;
import com.dsp.plot.repository.MinistriesRepository;
import com.dsp.plot.repository.MyUserRepository;
import com.dsp.plot.repository.PlotDateChartRepository;
import com.dsp.plot.repository.PlotHistoryRepository;
import com.dsp.plot.repository.PlotInputRepository;
import com.dsp.plot.repository.RequestRepository;
import com.dsp.plot.service.PlotInputService;
import com.dsp.plot.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PlotInputServiceImpl implements PlotInputService {

    @Value("${master.service.url}")
    private String masterServiceUrl;
    @Autowired
    private HttpServletRequest httpRequest;
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
    private JwtUtil jwtUtil;
    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private PlotInputRepository plotinputRepository;
    @Autowired
    private RequestRepository requestRepository;

    @Transactional
    @Override
    public PlotInputDTO save(PlotInputDTO plotinputDTO) {
        if (plotinputDTO.getStatus() == null) {
            plotinputDTO.setStatus("In Progress");
        }
        log.debug("Entering savePlotInput with PlotInputDTO: {}", plotinputDTO);
        if (plotinputDTO.getSubmissionDate() != null && plotinputDTO.getProposedDateOfLaying() != null) {
            long daysDifference = ChronoUnit.DAYS.between(plotinputDTO.getSubmissionDate(),
                    plotinputDTO.getProposedDateOfLaying());
            if (daysDifference < 2) {
                log.debug("Date of Submission must be at least 2 days before the Proposed Date Of Lying", plotinputDTO);
                log.info("Date of Submission must be at least 2 days before the Proposed Date Of Lying", plotinputDTO);
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
            Optional<BaseUser> optionalUser = userRepository.findById(entity.getAuthenticatedById());
            Optional<MasterCode> paperAbbr = codeRepository.findById(entity.getPaperTypeId());
            String abbr = "";
            if (paperAbbr.isPresent())
                abbr = paperAbbr.get().getAbbr();
            String contentText = formatPaperDetails(entity.getPaperDetails(), optionalUser.get(), abbr);
            entity.setContent(contentText);
            entity.setHtmlContentEnglish(contentText);
            entity = plotinputRepository.save(entity);
            entity.setRefId(entity.getId());
            log.info("Saved Bulletin1 attributes: ID={},  Status={}", entity.getId(), entity.getStatus());
            entity = plotinputRepository.save(entity);
            // Convert saved entity back to DTO
            PlotInputDTO responseDTO = PlotInputMapper.toDTO(entity);
            log.debug("Returning PlotInputDTO: {}", responseDTO);
            return responseDTO;
        } catch (Exception e) {
            log.error("Error in savePlotInput: ", e);
            throw e;
        }
    }

    private String formatPaperDetails(List<Map<String, Object>> paperDetails, BaseUser authenticatorDTO, String abbr) {
        if (authenticatorDTO == null) {
            throw new IllegalArgumentException("Authenticator details cannot be null.");
        }
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("<html><body>");
        // Authenticator details (always included)
        contentBuilder.append("<b>")
                .append(escapeHtml(authenticatorDTO.getUsername()))
                .append(" (")
                .append(escapeHtml(authenticatorDTO.getDesignation().getAbbr()))
                .append(")</b> to lay on the Table:<br>");
              
        // Check if paper details exist
        if (paperDetails == null || paperDetails.isEmpty()) {
            System.out.println("<b>No paper details available.</b> Details");
            contentBuilder.append("<b>No paper details available.</b>");
        } else {
           
            int i = 1; // Initialize numbering for list items
            for (Map<String, Object> paper : paperDetails) {
                
                if (paper == null || paper.isEmpty())
                    continue; // Skip null/empty entries
                // Create formatted output for each paper
                
                // Reports
                if (abbr.equals("PLOT_REPORT")) {
                    String formattedString = getFormattedReportDetails(paper);
                    // Append numbered list item
                    if(StringUtils.isBlank(formattedString))
                    {
                        contentBuilder.append(formattedString);
                    }
                    contentBuilder.append(i++).append(") ").append(formattedString).append("<br>");
                }
                
                // notifications
                else if (abbr.equals("PLOT_NOTIFICATION")) {
                    String formattedString = getFormattedNotificationDetails(paper);
                    if(StringUtils.isBlank(formattedString))
                    {
                        contentBuilder.append(formattedString);
                    }
                    // Append numbered list item
                    contentBuilder.append(i++).append(") ").append(formattedString).append("<br>");
                }
                // others
                else if (abbr.equals("PLOT_OTHER")) {
                    String formattedString = getFormattedOtherPaperDetails(paper);
                    // Append numbered list item
                    if(StringUtils.isBlank(formattedString))
                    {
                        contentBuilder.append(formattedString);
                    }
                    contentBuilder.append(i++).append(") ").append(formattedString).append("<br>");
                }

            }
        }
        contentBuilder.append("</body></html>");
        return contentBuilder.toString();
    }

    private String getFormattedReportDetails(Map<String, Object> paper) {
        String template = "A copy of the {0} (Hindi and English versions) of the {1}, for the year {2}";
        // Long paperType = (paper.get("paperNameId") instanceof Long)
        // ? (Long) paper.get("paperNameId")
        // : null;
        Object paperNameId = paper.get("paperNameId");
       
        Long paperType = null;
        if (paperNameId instanceof Long) {
            paperType = (Long) paperNameId;
        } else if (paperNameId instanceof Integer) {
            paperType = ((Integer) paperNameId).longValue(); // Convert Integer to Long
        } else if (paperNameId instanceof String) {
            try {
                paperType = Long.parseLong((String) paperNameId); // Convert String to Long
            } catch (NumberFormatException e) {
                paperType = null; // Handle invalid string cases
            }
        }
        
        String formattedString = "";
        MasterCode codeAbbr;
        try{
            codeAbbr = codeRepository.findById(paperType).get();
        }
        catch(Exception e)
        {
            return "";
        }
        String abbr = codeAbbr.getAbbr();
        switch (abbr) {
            case "PLOT_RPT_1":
                String nameAndPlace = escapeHtml(paper.get("constitutionalBody")) + ", "
                        + escapeHtml(paper.get("headquarters"));
                formattedString = MessageFormat.format(template,
                        escapeHtml(paper.get("titleOfReport")),
                        nameAndPlace,
                        escapeHtml(paper.get("year")));
                break;
            case "PLOT_RPT_2":
                Map<String, Object> ministry = (Map<String, Object>) paper.get("ministryDepartmentPSU");
                formattedString = MessageFormat.format(template,
                        escapeHtml(paper.get("reportType")),
                        escapeHtml(ministry.get("name")) + ", " + escapeHtml(ministry.get("headquarters")),

                        escapeHtml(paper.get("yearOrTimePeriod")));
                break;
            case "PLOT_RPT_3":
                formattedString = MessageFormat.format(template,
                        escapeHtml(paper.get("titleOfReport")),
                        escapeHtml(paper.get("companyHeadquarters")),
                        escapeHtml(paper.get("financialYear")));
                break;
            case "PLOT_RPT_4":
                formattedString = MessageFormat.format(template,
                        escapeHtml(paper.get("titleOfReport")),
                        escapeHtml(paper.get("bankHeadquarters")),
                        escapeHtml(paper.get("financialYear")));
                break;
            case "PLOT_RPT_5":
                formattedString = MessageFormat.format(template,
                        escapeHtml(paper.get("titleOfReport")),
                        escapeHtml(paper.get("headquarters")),
                        escapeHtml(paper.get("financialYear")));
                break;
            default:
                formattedString = ""; // Default empty value if paper type is not recognized
                break;
        }
        return formattedString;
    }

    private String getFormattedNotificationDetails(Map<String, Object> paper) {
        String template = "A copy of the {0} (Hindi and English versions) of the {1}, for the year {2}";
        // Long paperType = (paper.get("notificationId") instanceof Long) ? (Long)
        // paper.get("notificationId") : null;
        Object paperNameId = paper.get("notificationId");
       
        Long paperType = null;
        if (paperNameId instanceof Long) {
            paperType = (Long) paperNameId;
        } else if (paperNameId instanceof Integer) {
            paperType = ((Integer) paperNameId).longValue(); // Convert Integer to Long
        } else if (paperNameId instanceof String) {
            try {
                paperType = Long.parseLong((String) paperNameId); // Convert String to Long
            } catch (NumberFormatException e) {
                paperType = null; // Handle invalid string cases
            }
        }
       
        String formattedString = "";
        MasterCode codeAbbr; try{
            codeAbbr = codeRepository.findById(paperType).get();
        }
        catch(Exception e)
        {
            return "";
        }
        String abbr = codeAbbr.getAbbr();
        switch (abbr) {
            case "PLOT_NF_GEN":
                System.out.println("###  " + abbr);
                formattedString = MessageFormat.format(template,
                        escapeHtml(paper.get("notificationType")),
                        escapeHtml(paper.get("publishedIn")),
                        escapeHtml(paper.get("actUnderWhichLaid")));

                break;
            case "PLOT_NF_SON":
                System.out.println("###  " + abbr);
                formattedString = MessageFormat.format(template,
                        escapeHtml(paper.get("notificationType")),
                        escapeHtml(paper.get("publishedIn")),
                        escapeHtml(paper.get("actUnderWhichLaid")));
                break;
            case "PLOT_NF_NO":
                System.out.println("###  " + abbr);

                formattedString = MessageFormat.format(template,
                        escapeHtml(paper.get("notificationType")),
                        escapeHtml(paper.get("publishedIn")),
                        escapeHtml(paper.get("actUnderWhichLaid")));
                break;
            case "PLOT_NF_ON":
                System.out.println("###  " + abbr);

                formattedString = MessageFormat.format(template,
                        escapeHtml(paper.get("notificationType")),
                        escapeHtml(paper.get("publishedIn")),
                        escapeHtml(paper.get("actUnderWhichLaid")));
                break;
            default:
                formattedString = ""; // Default empty value if paper type is not recognized
                break;
        }
        System.out.println(formattedString);
        return formattedString;
    }

    private String getFormattedOtherPaperDetails(Map<String, Object> paper) {
        String template = "A copy of the {0} (Hindi and English versions) of the {1}, for the year {2}";

        // Long paperType = (paper.get("otherPaperId") instanceof Long) ? (Long)
        // paper.get("otherPaperId") : null;
        Object paperNameId = paper.get("otherPaperId");
        Long paperType = null;
      
        if (paperNameId instanceof Long) {
            paperType = (Long) paperNameId;
        } else if (paperNameId instanceof Integer) {
            paperType = ((Integer) paperNameId).longValue(); // Convert Integer to Long
        } else if (paperNameId instanceof String) {
            try {
                paperType = Long.parseLong((String) paperNameId); // Convert String to Long
            } catch (NumberFormatException e) {
                paperType = null; // Handle invalid string cases
            }
        }
       
        String formattedString = "";
        MasterCode codeAbbr; try{
            codeAbbr = codeRepository.findById(paperType).get();
        }
        catch(Exception e)
        {
            return "";
        }
        String abbr = codeAbbr.getAbbr();

        switch (abbr) {
            case "PLOT_OTH_1":
                formattedString = MessageFormat.format(template,
                        escapeHtml(paper.get("title")),
                        escapeHtml(paper.get("")),
                        escapeHtml(paper.get("year")));
                break;
            case "PLOT_OTH_2":
                // Map<String, Object> ministry = (Map<String, Object>)
                // paper.get("ministryDepartmentPSU");
                formattedString = MessageFormat.format(template,
                        escapeHtml(paper.get("title")),
                        escapeHtml(paper.get("departmentName")),
                        escapeHtml(paper.get("financialYear")));
                break;
            case "PLOT_OTH_3":
                formattedString = MessageFormat.format(template,
                        escapeHtml(paper.get("title")),
                        escapeHtml(paper.get("departmentName")),
                        escapeHtml(paper.get("financialYear")));
                break;
            case "PLOT_OTH_4":
                formattedString = MessageFormat.format(template,
                        escapeHtml(paper.get("titleOfReport")),
                        escapeHtml(paper.get("bankHeadquarters")),
                        escapeHtml(paper.get("financialYear")));
                break;
            case "PLOT_OTH_5":
                formattedString = MessageFormat.format(template,
                        escapeHtml(paper.get("title")),
                        escapeHtml(paper.get("statementNumber")),
                        escapeHtml(paper.get("year")));
                break;
            case "PLOT_OTH_6":
                formattedString = MessageFormat.format(template,
                        escapeHtml(paper.get("title")),
                        escapeHtml(paper.get("stateOrUT")),
                        escapeHtml(paper.get("financialYear")));
                break;
            case "PLOT_OTH_7":
                formattedString = MessageFormat.format(template,
                        escapeHtml(paper.get("title")),
                        escapeHtml(paper.get("requirementUnder")),
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
            dtoList = requestRepository.findByPlotInputId(plotId)
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
            Long rajyaSabhaSessionNo, LocalDate startDate, LocalDate endDate, String status, LocalDate dateOfSubmission,
            Long department, Long paperType, String paperName, LocalDate proposedDateOfLaying,
            LocalDate actualDateOfLaying, Long ministry) {
        log.debug("Entering getAll Plots");
        List<PlotInputDTO> dtoList = new ArrayList<>();
        List<PlotInput> entitys;
        if (isActive == null) {
            isActive = true;
        }
        Specification<PlotInput> spec = Specification.where(null);
        spec = spec.and(PlotInputSpec.isActive(isActive));
        // if (draftStatus != null) {
        // spec = spec.and(PlotInputSpec.hasDraftStatus(draftStatus));
        // }
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
        entitys = plotinputRepository.findAll(spec);
        Long houseId = getHouseId();
        for (PlotInput entity : entitys) {
            if (houseId == 1) {
                if (entity.getRajyaSabhaSessionNo() != null) {
                    PlotInputDTO dto = PlotInputMapper.toDTO(entity);
                    dtoList.add(dto);
                }
            } else if (houseId == 2) {
                if (entity.getLokSabhaSessionNo() != null) {
                    PlotInputDTO dto = PlotInputMapper.toDTO(entity);
                    dtoList.add(dto);
                }
            }
        }
        log.debug("Returning list of PlotDTOs: {}", dtoList);
        log.info("Returning list of PlotDTOs: {}", dtoList);
        log.error("Returning list of PlotDTOs: {}", dtoList);
        return dtoList;
    }

    @Override
    public List<PlotInputDTO> getHistories(Boolean isActive, Boolean draftStatus, Long lokSabhaNo,
            Long lokSabhaSessionNo, Long rajyaSabhaSessionNo, LocalDate startDate, LocalDate endDate, String status,
            LocalDate dateOfSubmission, Long department, Long paperType, String paperName,
            LocalDate proposedDateOfLaying, LocalDate actualDateOfLaying, Long ministry) {
        log.debug("Entering getAll Plots");
        List<PlotInputDTO> dtoList = new ArrayList<>();
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

        entitys = plotinputRepository.findAll(spec);
        Long houseId = getHouseId();
        for (PlotInput entity : entitys) {
            if (houseId == 1) {
                if (entity.getRajyaSabhaSessionNo() != null) {
                    PlotInputDTO dto = PlotInputMapper.toDTO(entity);
                    Optional<PlotHistory> history = plotHistoryRepository.findPlotHistory(dto.getId());
                    if (history.isPresent()) {
                        dto.setContent(history.get().getAddedText());
                    }
                    dtoList.add(dto);
                }
            } else if (houseId == 2) {
                if (entity.getLokSabhaSessionNo() != null) {
                    PlotInputDTO dto = PlotInputMapper.toDTO(entity);
                    Optional<PlotHistory> history = plotHistoryRepository.findPlotHistory(dto.getId());
                    if (history.isPresent()) {
                        dto.setContent(history.get().getAddedText());
                    }
                    dtoList.add(dto);
                }
            }
        }
        return dtoList;
    }

    @Override
    public PlotInputDTO getById(Long id) {
        log.debug("Entering getById with id: {}", id);
        PlotInput dBEntity = plotinputRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlotInput not found with id: " + id));
        log.debug("Fetched PlotInput by id: {}", dBEntity);
        PlotInputDTO dto = PlotInputMapper.toDTO(dBEntity);
        return dto;
    }

    @Override
    public RequestDTO getRequestById(Long id) {
        log.debug("Entering getById with id: {}", id);
        Request dBEntity = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + id));
        log.debug("Fetched Request by id: {}", dBEntity);
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

        // Save the updated PlotInput
        dbEntity = plotinputRepository.save(dbEntity);
        // Prepare the response DTO
        return (PlotInputMapper.toDTO(dbEntity));
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
            log.info("Saved request attributes: ID={},  plot id={}", entity.getId(), entity.getPlotInputId());
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
        if (optionalEntity.isPresent()) {
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
        List<PlotDateChart> listOfDateCharts = plotDateChartRepository.findAll(spec);
        List<Long> existingMinistries = listOfDateCharts.stream()
                .map(PlotDateChart::getMinistryId)
                .collect(Collectors.toList());
        String response = questionBranchApi(lokSabhaNo, lokSabhaSessionNo, rajyaSabhaNo, existingMinistries, houseId);
        listOfDateCharts = plotDateChartRepository.findAll();
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
            List<Map<String, Object>> dateList = entity.getDates(); // Extracts list of date mappings
            for (Map<String, Object> dateEntry : dateList) {
                String existingdateofSubmissionOfPapers = (String) dateEntry.get("dateofSubmissionOfPapers");
                String existingactualDateOfLaying = (String) dateEntry.get("actualDateOfLaying");

                if (dateofSubmissionOfPapers != null
                        && !dateofSubmissionOfPapers.equals(existingdateofSubmissionOfPapers)) {
                    shouldAdd = false;
                    continue;
                }
                if (actualDateOfLaying != null && !actualDateOfLaying.equals(existingactualDateOfLaying)) {
                    shouldAdd = false;
                    continue;
                }
                if (shouldAdd) {
                    dtoList.add(PlotInputMapper.dateChartMapToDTO(entity));
                    break; // Stop checking further dates for this entity once added
                }
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
                    String[] ministries = quesDto.getMinistriesDepartments().split(";");
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
       String token= getTokenFromRequest();
    
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       
        if (principal instanceof UserDetailsDTO) {
            houseId = ((UserDetailsDTO) principal).getHouseId();
            System.out.println("houseId "+houseId);
        }
        return houseId;
    }
    @Override
    public List<PlotInputDTO> getAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }
}
