package com.dsp.plot.service.impl;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import com.dsp.plot.dto.RequestDTO;
import com.dsp.plot.dto.SerialNumberRequest;
import com.dsp.plot.dto.SerialNumberResponse;
import com.dsp.plot.dto.UserDetailsDTO;
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
import com.dsp.plot.mapper.PlotInputMapper;
import com.dsp.plot.mapper.RequestMapper;
import com.dsp.plot.repository.MasterLSSRepository;
import com.dsp.plot.repository.MinistriesRepository;
import com.dsp.plot.repository.MastersRSSRepository;
import com.dsp.plot.repository.MyUserRepository;
import com.dsp.plot.repository.PlotDateChartRepository;
import com.dsp.plot.repository.PlotHistoryRepository;
import com.dsp.plot.repository.PlotInputRepository;
import com.dsp.plot.repository.RequestRepository;
import com.dsp.plot.service.PlotInputService;
import com.dsp.plot.util.JwtUtil;
import com.dsp.plot.filter.JwtFilter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class PlotInputServiceImpl implements PlotInputService {

    @Value("${master.service.url}")
    private String masterServiceUrl;
    @Autowired
    private HttpServletRequest httpRequest;
    @Autowired
    private MastersRSSRepository mastersRSSRepository;

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
    public List<PlotInputDTO> getAll(Boolean isActive, Boolean draftStatus, Long lokSabhaNo,
            Long lokSabhaSessionNo, Long rajyaSabhaSessionNo, LocalDate startDate, LocalDate endDate,
            String status) {
        log.debug("Entering getAll Plots");
        List<PlotInputDTO> dtoList = new ArrayList<>();
        List<PlotInput> entitys;
        // if (draftStatus == null) {
        // draftStatus = false;
        // }
        if (isActive == null) {
            isActive = true;
        }
        Specification<PlotInput> spec = Specification.where(null);
        spec = spec.and(PlotInputSpec.isActive(isActive));
        if (draftStatus != null) {
            spec = spec.and(PlotInputSpec.hasDraftStatus(draftStatus));
        }
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
            Long lokSabhaSessionNo, Long rajyaSabhaSessionNo, LocalDate startDate, LocalDate endDate,
            String status) {
        log.debug("Entering getAll Plots");
        List<PlotInputDTO> dtoList = new ArrayList<>();
        List<PlotInput> entitys;
        // if (draftStatus == null) {
        // draftStatus = false;
        // }
        if (isActive == null) {
            isActive = true;
        }
        Specification<PlotInput> spec = Specification.where(null);
        spec = spec.and(PlotInputSpec.isActive(isActive));
        if (draftStatus != null) {
            spec = spec.and(PlotInputSpec.hasDraftStatus(draftStatus));
        }
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
        PlotDateChart savedEntity = plotDateChartRepository.save(PlotInputMapper.dateChartMapToEntity(dto));
        PlotInputMapper.dateChartMapToDTO(savedEntity);
        return PlotInputMapper.dateChartMapToDTO(savedEntity);
    }

    @Override
    public PlotDateChartDTO getDateChartById(Long id) {
        // TODO Auto-generated method stub
        PlotDateChart savedEntity = plotDateChartRepository.findById(id).get();
        return PlotInputMapper.dateChartMapToDTO(savedEntity);
    }

    @Override
    public List<PlotDateChartDTO> getAllDateCharts(String lokSabhaNo, String lokSabhaSessionNo, String rajyaSabhaNo,
    Boolean isActive, LocalDate startDate, LocalDate endDate, String status) {
        List<PlotDateChartDTO> dtoList = new ArrayList<>();
        List<PlotDateChart> entitys;
        if (isActive == null) {
            isActive = true;
        }
        Specification<PlotDateChart> spec = Specification.where(null);
        spec = spec.and(DateChartSpec.isActive(isActive));
        // if (lokSabhaSessionNo != null) {
        //     spec = spec.and(DateChartSpec.hasLokSabhaSessionNo(lokSabhaSessionNo));
        // }
        // if (rajyaSabhaNo != null) {
        //     spec = spec.and(DateChartSpec.hasRajyaSabhaSessionNo(rajyaSabhaNo));
        // }
        if (startDate != null || endDate != null) {
            spec = spec.and(DateChartSpec.hasDateBetween(startDate, endDate));
        }
        if (status != null) {
            spec = spec.and(DateChartSpec.hasStatus(status));
        }
        List<PlotDateChartDTO> listOfDateCharts = plotDateChartRepository.findAll()
                .stream()
                .map(PlotInputMapper::dateChartMapToDTO)
                .collect(Collectors.toList());
        if (listOfDateCharts == null || listOfDateCharts.isEmpty()) {
            String response = questionBranchApi(lokSabhaNo, lokSabhaSessionNo, rajyaSabhaNo);
            listOfDateCharts = plotDateChartRepository.findAll()
                    .stream()
                    .map(PlotInputMapper::dateChartMapToDTO)
                    .collect(Collectors.toList());
            // System.out.println("::::: After");
            // System.out.println(listOfDateCharts);
            return listOfDateCharts;
        }
        // questionBranchApi
        return listOfDateCharts;
    }

    // question api
    public String questionBranchApi(String lokSabhaNo, String lokSabhaSessionNo, String rajyaSabhaNo) {
        String url = sansadUrl + "/integration-gateway/question-branch-api/get-question-chart?lsNo="
                + lokSabhaNo + "&sesNo=" + lokSabhaSessionNo;
        List<PlotDateChartDTO> responseDtos = new ArrayList<>();

        // System.out.println("Requesting URL: " + url);
        // Get the JWT token from request/session (implement this method as per your app
        // logic)
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
        // System.out.println("Request Headers: " + headers);
        try {
            ResponseEntity<List<QuesBranchApiDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<List<QuesBranchApiDto>>() {
                    });
            // System.out.println("API Response: " + response.getBody());
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
                            dateMap.put("proposedDateOfLying", proposedDate.toString());
                            dateMap.put("dateofSubmissionOfPapers", submissionDate.toString());
                            dateMap.put("actualDateOfLying", "");
                            return dateMap;
                        })
                        .collect(Collectors.toList());
                String[] ministries = quesDto.getMinistriesDepartments().split(";");
                for (String ministry : ministries) {
                    PlotDateChartDTO dto = PlotDateChartDTO.builder()
                            .build();
                    dto.setDates(datesList);
                    dto.setGroupName(quesDto.getGroup());
                    // System.out.println("@@@@@@@@@ ministry before " + ministry);
                    // System.out.println(dto);
                    if (lokSabhaSessionNo != null) {
                        Optional<MastersLokSabhaSession> optionalSession = masterLSSRepository
                                .findById(Long.parseLong(lokSabhaSessionNo));
                        if (optionalSession.isPresent()) {
                            MastersLokSabhaSession session = optionalSession.get();
                            dto.setLokSabhaNo(session.getLokSabha().getId());
                            dto.setLokSabhaSessionNo(session.getId());
                        }
                    } else if (rajyaSabhaNo != null) {
                        Optional<MastersRajyaSabha> optionalSession = mastersRSSRepository
                                .findBySessionNumber(rajyaSabhaNo);
                        if (optionalSession.isPresent()) {
                            MastersRajyaSabha session = optionalSession.get();
                            dto.setRajyaSabhaSessionNo(session.getId());
                        }
                    }
                    // System.out.println("@@@@@@@@@ ministry after ls adding " + ministry);
                    // System.out.println(dto);
                    ministry = ministry.toUpperCase();
                    ministry = ministry.trim();
                    // System.out.println("ministry " + ministry);
                    Optional<Ministries> optionalMinistries = ministriesRepository
                            .findByMinistryName(ministry);
                    // System.out.println("optionalMinistries " + optionalMinistries);
                    if (optionalMinistries.isPresent()) {
                        dto.setMinistryId(optionalMinistries.get().getId());
                        // System.out.println("@@@@@@@@@ dto " + optionalMinistries);
                        // System.out.println(dto);
                        PlotDateChart entity = PlotInputMapper.dateChartMapToEntity(dto);
                        // System.out.println("@@@@@@@@ dto to entity " + optionalMinistries);
                        // System.out.println(entity);
                        entity = plotDateChartRepository.save(entity);
                        // System.out.println("after saving");
                        // System.out.println(entity);
                        // System.out.println("saved Successfully");
                    }

                }
            }
            return "saved";
        } catch (HttpClientErrorException e) {
            System.err.println("API Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to call external API", e);
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
        System.out.println("httpRequest");
        System.out.println(httpRequest);
        String authorizationHeader = httpRequest.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
    private Long getHouseId() {
        Long houseId = null;
       String token= getTokenFromRequest();
       System.out.println("token "+token);
       Long house = jwtUtil.extractHouseId(token);
       System.out.println("house "+house);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("!!!!");
        System.out.println(principal);
        if (principal instanceof UserDetailsDTO) {
            houseId = ((UserDetailsDTO) principal).getHouseId();
            System.out.println("houseId "+houseId);
        }
        return house;
    }
    @Override
    public List<PlotInputDTO> getAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }
}
