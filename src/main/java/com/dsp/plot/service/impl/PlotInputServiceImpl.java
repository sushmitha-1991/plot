package com.dsp.plot.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.dsp.plot.dto.PlotDateChartDTO;
import com.dsp.plot.dto.PlotInputDTO;
import com.dsp.plot.dto.RequestDTO;
import com.dsp.plot.dto.SerialNumberRequest;
import com.dsp.plot.dto.SerialNumberResponse;
import com.dsp.plot.dto.UserDetailsDTO;
import com.dsp.plot.entity.MastersLokSabhaSession;
import com.dsp.plot.entity.MastersRajyaSabha;
import com.dsp.plot.entity.PlotDateChart;
import com.dsp.plot.entity.PlotGenerate;
import com.dsp.plot.entity.PlotInput;
import com.dsp.plot.entity.Request;
import com.dsp.plot.entity.feignclient.BearerClient;
import com.dsp.plot.entity.specification.PlotGenerateSpec;
import com.dsp.plot.entity.specification.PlotInputSpec;
import com.dsp.plot.exception.ResourceNotFoundException; // TODO: Correct package may need to be set
import com.dsp.plot.mapper.PlotGenerateMapper;
import com.dsp.plot.mapper.PlotInputMapper;
import com.dsp.plot.mapper.RequestMapper;
import com.dsp.plot.repository.MasterLSSRepository;
import com.dsp.plot.repository.MastersRSSRepository;
import com.dsp.plot.repository.MyUserRepository;
import com.dsp.plot.repository.PlotDateChartRepository;
import com.dsp.plot.repository.PlotInputRepository;
import com.dsp.plot.repository.RequestRepository;
import com.dsp.plot.service.PlotInputService;

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
    private MasterLSSRepository masterLSSRepository;

    @Autowired
    private PlotDateChartRepository plotDateChartRepository;
    @Autowired
    private MyUserRepository userRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private BearerClient bearerClient;
    private static final Logger log = LoggerFactory.getLogger(PlotInputServiceImpl.class);
    // private final PlotInputMapper mapper;
    @Autowired
    private PlotInputRepository plotinputRepository;
    @Autowired
    private RequestRepository requestRepository;

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
                MastersRajyaSabha session = mastersRSSRepository.findById(entity.getRajyaSabhaSessionNo()).get();
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
                MastersLokSabhaSession session = masterLSSRepository.findById(entity.getLokSabhaSessionNo()).get();
                entity.setLokSabhaSessionNumber(session.getSessionNumber());
                entity.setLokSabhaNo(session.getLokSabha().getId());
                entity.setLokSabhaSession(session.getLokSabha().getLokSabhaNumber());
                entity.setRajyaSabhaSessionNo(null);
                entity.setPendingAt("Table Office A");
                serialNumberRequest.setSessionId(entity.getLokSabhaSessionNo());
            }
            System.out.println("apppppppppppppppppppp");
            System.out.println(serialNumberRequest);
            if (StringUtils.isBlank(plotinputDTO.getApplicationNo())) {
                SerialNumberResponse applicationNo = generateApplicationNo(serialNumberRequest);
                System.out.println(applicationNo.getId());
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

    // public String generateApplicationNo(Long id, Long session, Boolean saveAsDraft) {
    //     if (saveAsDraft) {
    //         return "Ref-DPLOT" + id;
    //     } else {
    //         return "Ref-PLOT" + id;
    //     }
    // }
    @Override
    public List<RequestDTO> getAllRequests() {
        List<RequestDTO> dtoList = new ArrayList<>();
        try {
            List<Request> entities = requestRepository.findAll();
            for (Request dbEntity : entities) {
                PlotInput input = plotinputRepository.findById(dbEntity.getPlotInputId()).orElse(null);
                if (input != null) {
                    PlotInputDTO plotDto = PlotInputMapper.toDTO(input);
                    RequestDTO dto = RequestMapper.toDTO(dbEntity);
                    dto.setPlotDetails(plotDto);
                    dtoList.add(dto);
                } else {
                    log.warn("No PlotInput found for PlotInputId: {}", dbEntity.getPlotInputId());
                }

            }
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
        //     draftStatus = false;
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
        // entitys=plotinputRepository.findAll();
        System.out.println("@@@@@@@");
        System.out.println(entitys);
        Long houseId = getHouseId();
        for (PlotInput entity : entitys) {
            System.out.println("########");
            System.out.println(entity);
            if (houseId == 1) {
                System.out.println("!!!!!!! " + houseId);
                System.out.println(entity);
                if (entity.getRajyaSabhaSessionNo() != null) {
                    PlotInputDTO dto = PlotInputMapper.toDTO(entity);
                    dtoList.add(dto);
                }
            } else if (houseId == 2) {
                System.out.println("!!!!!! " + houseId);
                System.out.println(entity);
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
    public PlotInputDTO getById(Long id) {
        log.debug("Entering getById with id: {}", id);
        PlotInput dBEntity = plotinputRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlotInput not found with id: " + id));
        log.debug("Fetched PlotInput by id: {}", dBEntity);
        PlotInputDTO dto = PlotInputMapper.toDTO(dBEntity);
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
        dbEntity.setId(id);

        // Save the updated PlotInput
        dbEntityNew = plotinputRepository.save(dbEntityNew);
        // Prepare the response DTO
        return (PlotInputMapper.toDTO(dbEntityNew));
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
        System.out.println("@@@@@@@@@@");
        System.out.println();
        return PlotInputMapper.dateChartMapToDTO(savedEntity);
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
        }
        return houseId;
    }

}
