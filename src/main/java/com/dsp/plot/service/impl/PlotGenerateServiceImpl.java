package com.dsp.plot.service.impl;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.dsp.plot.dto.DocumentDTO;
import com.dsp.plot.dto.PlotGenerateDTO;
import com.dsp.plot.dto.PlotIndexDTO;
import com.dsp.plot.dto.SerialNumberRequest;
import com.dsp.plot.dto.SerialNumberResponse;
import com.dsp.plot.dto.UserDetailsDTO;
import com.dsp.plot.entity.MastersLokSabhaSession;
import com.dsp.plot.entity.MastersRajyaSabha;
import com.dsp.plot.entity.PlotGenerate;
import com.dsp.plot.entity.PlotIndex;
import com.dsp.plot.entity.PlotInput;
import com.dsp.plot.entity.specification.PlotGenerateSpec;
import com.dsp.plot.entity.specification.PlotIndexSpec;
import com.dsp.plot.exception.CustomBadRequestException;
import com.dsp.plot.exception.CustomDatabaseException;
import com.dsp.plot.exception.NoDataFoundException;
import com.dsp.plot.exception.ResourceNotFoundException;
import com.dsp.plot.mapper.PlotGenerateMapper;
import com.dsp.plot.repository.MasterLSSRepository;
import com.dsp.plot.repository.MastersRSSRepository;
import com.dsp.plot.repository.PlotGenerateRepository;
import com.dsp.plot.repository.PlotIndexRepository;
import com.dsp.plot.repository.PlotInputRepository;
import com.dsp.plot.service.PlotGenerateService;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Service
public class PlotGenerateServiceImpl implements PlotGenerateService {

    @Value("${workflow.service.url}")
    private String draftUrl;
    private static final Logger log = LoggerFactory.getLogger(PlotInputServiceImpl.class);
    // private final PlotInputMapper mapper;
    @Value("${master.service.url}")
    private String masterServiceUrl;
    @Autowired
    private HttpServletRequest httpRequest;
    @Autowired
    private MastersRSSRepository mastersRSSRepository;

    @Autowired
    private MasterLSSRepository masterLSSRepository;
    @Autowired
    private PlotGenerateRepository plotRepository;
    @Autowired
    private PlotIndexRepository plotIndexRepo;
    @Autowired
    private PlotInputRepository inputRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public DocumentDTO getDocumentByDate(LocalDate dateOfDoc, String doc) {
        try {
            List<PlotGenerate> entitys = new ArrayList<>();
            Specification<PlotGenerate> spec = Specification.where(null);
            spec = spec.and(PlotGenerateSpec.isActive(true));
            spec = spec.and(PlotGenerateSpec.hasDraftStatus(false));
            System.out.println("@@@@@@@@");
            System.out.println(dateOfDoc);
            if (dateOfDoc == null) {
                dateOfDoc = LocalDate.now();
            }
            if (doc.equalsIgnoreCase("PLOT") ) {
                entitys = plotRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "id"));
                log.info(" Doc Found on Specified Date:{}", entitys);
            }
            if (entitys.isEmpty()) {
                log.info("No Doc Found on Specified Date");
                throw new NoDataFoundException("No Doc Found On Specified Date");
            }
            System.out.println("&&&&&&&&&&&&");
            System.out.println(entitys);
            PlotGenerate entity = entitys.get(0);
            System.out.println(entity);
            log.info("Doc Found on Specified Date:{}", entity);
            return DocumentDTO.builder()
                    .id(entity.getId())
                    .date(entity.getPlotDate())
                    .applicationId(entity.getApplicationId())
                    .applicationNo(entity.getApplicationNo())
                    .currentStatus(entity.getCurrentStatus())
                    .note(entity.getContent())
                    .footNote(entity.getFootNote())
                    .build();
        } catch (IllegalArgumentException e) {
            // Handle cases where Sort property is invalid or not found
            throw new CustomBadRequestException("Invalid sorting field: id", e);
        } catch (Exception e) {
            // General exception handling
            throw new CustomDatabaseException("Failed to retrieve records", e);
        }
    }

    @Override
    public PlotGenerateDTO save(PlotGenerateDTO plotDTO) {
        if (plotDTO.getStatus() == null) {
            plotDTO.setStatus("In Progress");
        }

        log.debug("Entering savePlotGenerate with PlotGenerate DTO: {}", plotDTO);
        try {
            // Convert DTO to entity
            PlotGenerate entity = PlotGenerateMapper.toEntity(plotDTO);
            entity.setDocumentDate(entity.getPlotDate());
            entity.setCurrentStatus(entity.getStatus());
            log.debug("Converted PlotGenerateDTO to entity: {}", entity);
            // Save PlotGenerate entity
            // generating application no
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
                            "Session No is not valid");
                }
                MastersRajyaSabha session = optionalSession.get();
                entity.setRajyaSabhaSession(session.getSessionNumber());
                entity.setLokSabhaSessionNo(null);
                entity.setLokSabhaNo(null);
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
                            "Session Number is not valid");
                }
                MastersLokSabhaSession session = optionalSession.get();
                entity.setLokSabhaSessionNumber(session.getSessionNumber());
                entity.setLokSabhaNo(session.getLokSabha().getId());
                entity.setLokSabhaSession(session.getLokSabha().getLokSabhaNumber());
                entity.setRajyaSabhaSessionNo(null);
                serialNumberRequest.setSessionId(entity.getLokSabhaSessionNo());
            }
            if (StringUtils.isBlank(entity.getApplicationNo())) {
                SerialNumberResponse applicationNo = generateApplicationNo(serialNumberRequest);
                entity.setApplicationNo(applicationNo.getId());
            }
            entity = plotRepository.save(entity);
            entity.setRefId(entity.getId());
            log.info("Saved PLOT attributes: ID={},  Status={}", entity.getId(), entity.getStatus());
            entity = plotRepository.save(entity);
            if (plotDTO.getPlotInputs() != null) {
                for (Long id : plotDTO.getPlotInputs()) {
                    if (!inputRepository.existsById(id)) {
                        log.error("plot input not found with ID: ", id);
                        // throw new UserNotFoundException(" not found with ID: " + id);
                    } else {
                        log.info("plot input found with ID: ", id);
                        log.info("Attempting to fetch and update with ID: {}", id);
                        PlotInput existingInput = inputRepository.findById(id).get();
                        log.info("Successfully fetched List Of Business with ID: {}", id);
                        existingInput.setId(id);
                        existingInput.setGeneratedPlotId(entity.getId());
                        existingInput.setApplicationInstanceId(entity.getId());
                        try {
                            inputRepository.save(existingInput);
                        } catch (Exception e) {
                            log.error("Error in updating plot id in lob input", existingInput);

                        }
                    }
                }

            } else {
                log.info("plot input need to be provided");
            }
            if (!StringUtils.isBlank(plotDTO.getApplicationNo())) {
                // Get the Draft ID from draft table using appNo
                String res = deleteDraftEntry(plotDTO.getApplicationNo());
                // /delete-save-as-draft
            }

            // Convert saved entity back to DTO
            PlotGenerateDTO responseDTO = PlotGenerateMapper.toDTO(entity);
            log.debug("Returning PlotGenerateDTO: {}", responseDTO);
            return responseDTO;
        } catch (Exception e) {
            log.error("Error in savePlotInput: ", e);
            throw e;
        }
    }

    @Override
    public List<PlotGenerateDTO> getAll(Boolean isActive, Boolean draftStatus, Long lokSabhaNo,
            Long lokSabhaSessionNo, Long rajyaSabhaSessionNo, LocalDate startDate, LocalDate endDate,
            String status) {
        log.info("Entering getAll Plots");

        List<PlotGenerateDTO> dtoList = new ArrayList<>();
        List<PlotGenerate> entitys;
        if (draftStatus == null) {
            draftStatus = false;
        }
        if (isActive == null) {
            isActive = true;
        }
        Specification<PlotGenerate> spec = Specification.where(null);
        spec = spec.and(PlotGenerateSpec.isActive(isActive));
        spec = spec.and(PlotGenerateSpec.hasDraftStatus(draftStatus));
        if (lokSabhaSessionNo != null) {
            spec = spec.and(PlotGenerateSpec.hasLokSabhaSessionNo(lokSabhaSessionNo));
        }
        if (rajyaSabhaSessionNo != null) {
            spec = spec.and(PlotGenerateSpec.hasRajyaSabhaSessionNo(rajyaSabhaSessionNo));
        }
        if (startDate != null || endDate != null) {
            spec = spec.and(PlotGenerateSpec.hasDateBetween(startDate, endDate));
        }
        if (status != null) {
            spec = spec.and(PlotGenerateSpec.hasStatus(status));
        }
        entitys = plotRepository.findAll(spec);
        Long houseId = getHouseId();
        for (PlotGenerate entity : entitys) {
            if (houseId == 1) {
                if (entity.getRajyaSabhaSessionNo() != null) {
                    PlotGenerateDTO dto = PlotGenerateMapper.toDTO(entity);
                    dtoList.add(dto);
                }
            } else if (houseId == 2) {
                if (entity.getLokSabhaSessionNo() != null) {
                    PlotGenerateDTO dto = PlotGenerateMapper.toDTO(entity);
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
    public PlotGenerateDTO getById(Long id) {
        log.debug("Entering getById with id: {}", id);
        PlotGenerate dBEntity = plotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plot not found with id: " + id));
        log.debug("Fetched Plot by id: {}", dBEntity);
        PlotGenerateDTO dto = PlotGenerateMapper.toDTO(dBEntity);
        return dto;
    }
    

    @Transactional
    @Override
    public PlotGenerateDTO update(Long id, PlotGenerateDTO plotDTO) {
        log.debug("Starting update for Plot with ID: {}", id);
        // Fetch the existing Plot
        PlotGenerate dbEntity = plotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plot not found with id: " + id));
        // Update only the fields you want to update: TODO. Default -> All parameters
        // are updated
        PlotGenerate dbEntityNew = PlotGenerateMapper.toEntity(plotDTO);
        dbEntityNew.setId(dbEntity.getId());
        if (dbEntityNew.getStatus() == null) {
            dbEntityNew.setStatus("In Progress");
        }
        dbEntityNew.setCurrentStatus("" + dbEntity.getStatus());
        if (dbEntityNew.getPlotDate() != null) {
            dbEntityNew.setDocumentDate(dbEntityNew.getPlotDate());
        }
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
        // Save the updated Plot
        dbEntity=plotRepository.save(dbEntity);
        // Prepare the response DTO
        return (PlotGenerateMapper.toDTO(dbEntity));
    }

    @Override
    public void delete(Long id) {
        Optional<PlotGenerate> dBEntryOptional = plotRepository.findByIdAndIsActive(id, true);
        if (dBEntryOptional.isEmpty()) {
            throw new ResourceNotFoundException("Already Deleted or does not exists");
        }
        PlotGenerate dBEntry = dBEntryOptional.get();
        dBEntry.setIsDeleted(true);
        dBEntry.setIsActive(false);
        plotRepository.saveAndFlush(dBEntry);
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
    // private String deleteDraftEntry(String applicationNo)
    // {
    // String url = draftUrl+"/delete-save-as-draft/applicationNo="+applicationNo;
    // String token = getTokenFromRequest();
    // if (token == null) {
    // throw new IllegalStateException("Authorization token is missing");
    // }
    // HttpHeaders headers = new HttpHeaders();
    // headers.set("Authorization", "Bearer " + token);
    // headers.setContentType(MediaType.APPLICATION_JSON);

    // HttpEntity<SerialNumberRequest> entity = new HttpEntity<>(request, headers);
    // try {
    // // RestTemplate restTemplate = new RestTemplate();
    // return restTemplate.postForObject(url, entity, SerialNumberResponse.class);
    // } catch (Exception e) {
    // System.err.println("Error during API call: " + e.getMessage());
    // throw e;
    // }
    // return "";
    // }
    private String deleteDraftEntry(String applicationNo) {
        String url = draftUrl + "/delete-save-as-draft?applicationNo=" + applicationNo;
        String token = getTokenFromRequest();
        if (token == null) {
            throw new IllegalStateException("Authorization token is missing");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers); // No body needed for DELETE request

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return "Draft deleted successfully";
            } else {
                return "Failed to delete draft";
            }
        } catch (Exception e) {
            System.err.println("Error during API call: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<PlotIndexDTO> getIndexes(Long lokSabhaNo, Long lokSabhaSessionNo, Long rajyaSabhaSessionNo,
            LocalDate startDate, LocalDate endDate, String status, String reportName, Long createdBy) {
        Specification<PlotIndex> spec = Specification.where(PlotIndexSpec.isActive(true));
        if (startDate != null || endDate != null) {
            // spec = spec.and(PlotIndexSpec.hasDateBetween(startDate, endDate));
        }
        if (reportName != null) {
            spec = spec.and(PlotIndexSpec.hasReportName(reportName));
        }
        if (createdBy != null) {
            spec = spec.and(PlotIndexSpec.hasCreatedBy(createdBy));
        }
        if (lokSabhaNo != null) {
            spec = spec.and(PlotIndexSpec.hasLokSabhaNo(lokSabhaNo));
        }
        if (lokSabhaSessionNo != null) {
            spec = spec.and(PlotIndexSpec.hasLokSabhaSessionNo(lokSabhaSessionNo));
        }
        if (rajyaSabhaSessionNo != null) {
            spec = spec.and(PlotIndexSpec.hasRajyaSabhaSessionNo(rajyaSabhaSessionNo));
        }
        List<PlotIndex> response = plotIndexRepo.findAll(spec);
        
        List<PlotIndexDTO> responseDTO=new ArrayList<>();
        for(PlotIndex entity:response)
        {
            responseDTO.add(PlotGenerateMapper.toIndexDTO(entity));
        }
        return responseDTO;
    }

    @Override
    public PlotIndexDTO saveIndex(PlotIndexDTO dto) {
        // TODO Auto-generated method stub
        PlotIndex entity = PlotGenerateMapper.toEntityIndex(dto);
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
                        "Session No is not valid");
            }
            MastersRajyaSabha session = optionalSession.get();
            entity.setLokSabhaSessionNo(null);
            entity.setLokSabhaNo(null);
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
                        "Session Number is not valid");
            }
            MastersLokSabhaSession session = optionalSession.get();
            entity.setLokSabhaNo(session.getLokSabha().getId());
            entity.setRajyaSabhaSessionNo(null);
            serialNumberRequest.setSessionId(entity.getLokSabhaSessionNo());
        }

        SerialNumberResponse applicationNo = generateApplicationNo(serialNumberRequest);
        entity.setRefNo(applicationNo.getId());
        entity = plotIndexRepo.save(entity);
        PlotIndexDTO responseDto = PlotGenerateMapper.toIndexDTO(plotIndexRepo.findById(entity.getId()).get());
        return responseDto;
    }

    @Override
    public PlotIndexDTO getIndexById(Long id) {
        // TODO Auto-generated method stub
        PlotIndexDTO dto = PlotGenerateMapper.toIndexDTO(plotIndexRepo.findById(id).get());
        return dto;
    }
    // @Override
    // public List<PlotIndexDTO> getIndexByReportName(String reportName) {
    // // TODO Auto-generated method stub
    // List<PlotIndexDTO> dto =
    // PlotGenerateMapper.toIndexDTO(plotIndexRepo.findByReportName(reportName));
    // return dto;
    // }

    @Override
    public List<PlotGenerateDTO> getAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }

}
