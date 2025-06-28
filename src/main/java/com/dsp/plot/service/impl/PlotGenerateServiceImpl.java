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
import com.dsp.plot.dto.PlotContentDTO;
import com.dsp.plot.dto.PlotGenerateDTO;
import com.dsp.plot.dto.PlotIndexDTO;
import com.dsp.plot.dto.SerialNumberRequest;
import com.dsp.plot.dto.SerialNumberResponse;
import com.dsp.plot.dto.UserDetailsDTO;
import com.dsp.plot.entity.BaseUser;
import com.dsp.plot.entity.MastersLokSabhaSession;
import com.dsp.plot.entity.MastersRajyaSabha;
import com.dsp.plot.entity.Ministries;
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
import com.dsp.plot.repository.*;
import com.dsp.plot.service.PlotGenerateService;
import com.fasterxml.jackson.core.base.ParserMinimalBase;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class PlotGenerateServiceImpl implements PlotGenerateService {

    private final MinistriesRepository ministriesRepository;

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
    private MyUserRepository myUserRepository;
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

    PlotGenerateServiceImpl(MinistriesRepository ministriesRepository) {
        this.ministriesRepository = ministriesRepository;
    }

    @Override
    public DocumentDTO getDocumentByDate(LocalDate dateOfDoc, String doc) {
        try {
            List<PlotGenerate> entitys = new ArrayList<>();
            Specification<PlotGenerate> spec = Specification.where(null);
            spec = spec.and(PlotGenerateSpec.isActive(true));
            spec = spec.and(PlotGenerateSpec.hasDraftStatus(false));

            if (dateOfDoc == null) {
                dateOfDoc = LocalDate.now();
            }
            if (doc.equalsIgnoreCase("PLOT")) {
                entitys = plotRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "id"));
                log.info(" Doc Found on Specified Date:{}", entitys);
            }
            if (entitys.isEmpty()) {
                log.info("No Doc Found on Specified Date");
                throw new NoDataFoundException("No Doc Found On Specified Date");
            }

            PlotGenerate entity = entitys.get(0);
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
            entity.setHtmlContentEnglish(entity.getContent());
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
            if (plotDTO.getPlots() != null) {
                for (Long id : plotDTO.getPlots()) {
                    if (!plotRepository.existsById(id)) {
                        log.error("plot  not found with ID: ", id);
                        // throw new UserNotFoundException(" not found with ID: " + id);
                    } else {
                        log.info("plot  found with ID: ", id);
                        log.info("Attempting to fetch and update with ID: {}", id);
                        PlotGenerate existingEntity = plotRepository.findById(id).get();
                        log.info("Successfully fetched List Of Business with ID: {}", id);
                        existingEntity.setId(id);
                        existingEntity.setPlotParentId(entity.getId());
                        existingEntity.setParentInstanceId(entity.getId());
                        try {
                            plotRepository.save(existingEntity);
                        } catch (Exception e) {
                            log.error("Error in updating plot id in ", existingEntity);

                        }
                    }
                }

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
            String status, Boolean sendToLibrary) {
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
        if (sendToLibrary != null) {
            spec = spec.and(PlotGenerateSpec.hasSendToLibrary(sendToLibrary));
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
        dbEntity.setCurrentStatus(dbEntity.getStatus());
        dbEntity.setDocumentDate(dbEntity.getPlotDate());
        dbEntity.setHtmlContentEnglish(dbEntity.getContent());
        dbEntity = plotRepository.save(dbEntity);
        // Prepare the response DTO
        return (PlotGenerateMapper.toDTO(dbEntity));
    }

    @Override
    public PlotIndexDTO updateIndexById(Long id, PlotIndexDTO plotIndexDTO) {
        log.debug("Starting update for Plot with ID: {}", id);
        // Fetch the existing Plot
        PlotIndex dbEntity = plotIndexRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plot not found with id: " + id));
        // Update only the fields you want to update: TODO. Default -> All parameters
        // are updated
        PlotIndex dbEntityNew = PlotGenerateMapper.toEntityIndex(plotIndexDTO);
        dbEntityNew.setId(dbEntity.getId());
        if (dbEntityNew.getStatus() == null) {
            dbEntityNew.setStatus("In Progress");
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

        dbEntity = plotIndexRepo.save(dbEntity);
        // Prepare the response DTO
        return (PlotGenerateMapper.toIndexDTO(dbEntity));
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

    @Override
    public void deleteIndex(Long id) {
        Optional<PlotIndex> dBEntryOptional = plotIndexRepo.findByIdAndIsActive(id, true);
        if (dBEntryOptional.isEmpty()) {
            throw new ResourceNotFoundException("Already Deleted or does not exists");
        }
        PlotIndex dBEntry = dBEntryOptional.get();
        dBEntry.setIsDeleted(true);
        dBEntry.setIsActive(false);
        plotIndexRepo.saveAndFlush(dBEntry);
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
        Long houseId = getHouseId();
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
        // sorting
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<PlotIndex> response = plotIndexRepo.findAll(spec, sort);
        // List<PlotIndex> response = plotIndexRepo.findAll(spec);
        List<PlotIndexDTO> responseDTO = new ArrayList<>();
        for (PlotIndex entity : response) {
            Optional<BaseUser> username = myUserRepository.findById(entity.getCreatedBy());
            PlotIndexDTO dto = PlotGenerateMapper.toIndexDTO(entity);
            if (username.isPresent()) {
                BaseUser user = username.get();
                dto.setCreatedBy(user.getFirstName() + " " + user.getLastName());
            }
            if (houseId == 1) {
                if (dto.getRajyaSabhaSessionNo() != null)
                    responseDTO.add(dto);
            }
            if (houseId == 2) {
                if (dto.getLokSabhaSessionNo() != null)
                    responseDTO.add(dto);
            }

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
        if (dto.getRefNo() == null) {
            SerialNumberResponse applicationNo = generateApplicationNo(serialNumberRequest);
            entity.setRefNo(applicationNo.getId());
        }
        entity = plotIndexRepo.save(entity);
        PlotIndexDTO responseDto = PlotGenerateMapper.toIndexDTO(plotIndexRepo.findById(entity.getId()).get());
        Optional<BaseUser> username = myUserRepository.findById(entity.getCreatedBy());
        if (username.isPresent()) {
            BaseUser user = username.get();
            responseDto.setCreatedBy(user.getFirstName() + " " + user.getLastName());
        }

        if (!StringUtils.isBlank(dto.getRefNo())) {
            // Get the Draft ID from draft table using appNo
            String res = deleteDraftEntry(dto.getRefNo());
            System.out.println("res:::::::" + res);
            // /delete-save-as-draft
        }
        return responseDto;
    }

    @Override
    public PlotIndexDTO getIndexById(Long id) {
        // TODO Auto-generated method stub
        PlotIndex entity = plotIndexRepo.findById(id).get();
        PlotIndexDTO dto = PlotGenerateMapper.toIndexDTO(entity);
        Optional<BaseUser> username = myUserRepository.findById(entity.getCreatedBy());
        if (username.isPresent()) {
            BaseUser user = username.get();
            dto.setCreatedBy(user.getFirstName() + " " + user.getLastName());
        }
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

    // @Override
    // public String generateReorderedContent(List<PlotContentDTO> contents) {
    //
    // if (contents == null || contents.isEmpty()) {
    // return null;
    // }
    //
    // // String lastMinisterName = null;
    // List<String> ministryList = new ArrayList<>();
    // int seqNo = 0;
    // for (int i = 0; i < contents.size(); i++) {
    // PlotContentDTO x = contents.get(i);
    // Optional<Ministries>
    // ministry=ministriesRepository.findByMinistryCode(x.getMinstryId());
    // System.out.println("@@ x @@@ " + x);
    // System.out.println("&&&&&&"+ministry.get().getMinistryName());
    // // Long inputId = x.getPlotInputId();
    // // PlotInput input = inputRepository.getById(inputId);
    // // get minister name from input or parse minister name from text
    // // OR parse the text
    // String content = x.getAddedText();
    // System.out.println("@@ content @@@ " + content);
    // String ministerName = parseMinisterName(content);
    // System.out.println("@@ ministerName @@@ " + ministerName);
    // if (ministryList.contains(ministerName)) {
    // seqNo++; // increase seq no
    // // remove 'To Lay on the table'.
    // // newContent = generateTrimmedContent(inputId, seqNo);
    // String newContent = removeContentBetweenBodyAndFirstBr(content);
    // System.out.println("@@ newContent @@@ " + newContent);
    // if (seqNo > 1)
    // newContent = replaceNumber(newContent, seqNo);
    // x.setAddedText(newContent);
    // } else {
    // ministryList.add(ministerName);
    // seqNo = 0;
    // }
    // System.out.println("@@@ ministryList @@ " + ministryList);
    // // ministryList.add(ministerName);
    // }
    // return generateCombinedContent(contents);
    // }

    @Override
    public String generateReorderedContent(List<PlotContentDTO> contents) {

        if (contents == null || contents.isEmpty()) {
            System.out.println("Number of Inputs = 0");
            return null;
        }
        System.out.println("Number of Inputs: " + contents.size());
        // String lastMinisterName = null;
        int seqNo = 0;
        List<String> ministryList = new ArrayList<>();
        for (int i = 0; i < contents.size(); i++) {
            PlotContentDTO x = contents.get(i);
            // OR parse the text
            String content = x.getAddedText();
            String ministerName = parseMinisterName(content);
            // if (ministerName.equals(lastMinisterName)) {
            if (ministryList.contains(ministerName)) {
                ++seqNo; // increase seq no
                System.out.println("Seq no: " + seqNo);
                // remove 'To Lay on the table'.
                // newContent = generateTrimmedContent(inputId, seqNo);
                String newContent = removeContentBetweenBodyAndFirstBr(content);
                if (seqNo > 1)
                    newContent = replaceNumber(newContent, seqNo);
                x.setAddedText(newContent);
            } else {
                seqNo = 0;
                content.replace("</ol>", "");
            }
            // lastMinisterName = ministerName;
            ministryList.add(ministerName);
            System.out.println("ministerName  " + ministerName);

            System.out.println("ministryList  " + ministryList);
            x.setAddedText(removeBodyHtmlTags(x.getAddedText()));

        }

        return generateCombinedContent(contents, ministryList);
    }

    private static String generateCombinedContent(List<PlotContentDTO> contents, List<String> ministryList) {
        StringBuilder finalContent = new StringBuilder();
        contents.forEach(x -> {
            finalContent.append(x.getAddedText());
            finalContent.append("<br>");
        });
        finalContent.append("</body></html>");

        log.info("final Content  {}", finalContent);

        String finalResponse = finalContent.toString();
        for (String ministryName : ministryList) {
            System.out.println("minister in content " + ministryName);

            finalResponse.replace("<br><b>" + ministryName + "", "</ol><br><b>" + ministryName).replace("</li><br></body>", "</li></ol><br></body>");
        }
        return finalResponse;
    }

    private static String parseMinisterName(String htmlString) {
        System.out.println("Input: " + htmlString);
        String plainText = htmlString.replaceAll("<[^>]*>", "");
        System.out.println("Plain text: " + plainText);
        String targetPhrase = "to lay on the Table:";
        int index = plainText.indexOf(targetPhrase);
        System.out.println("Match index: " + index);
        String mname = null;
        if (index != -1) {
            mname = plainText.substring(0, index).trim();
        }
        System.out.println("Min Name: " + mname);
        return mname;
    }

    private static String removeContentBetweenBodyAndFirstBr(String html) {
        log.info("html " + html);
        int bodyStart = html.indexOf("<body>");
        int firstBr = html.indexOf("<br>", bodyStart);

        if (bodyStart == -1 || firstBr == -1) {
            return html;
        }

        String beforeBody = html.substring(0, bodyStart + 6); // Keep opening <body> tag
        String afterBr = html.substring(firstBr); // Keep content from the first <br> onwards
        log.info("html " + html);
        afterBr = afterBr.replace("<ol>", "").replace("</ol>", "")
        .replace("</li></ol><br><br><li>", "</li><br><br><li>");
        
        log.info("html " + html);

        return beforeBody + afterBr;
    }

    private static String replaceNumber(String html, int number) {
        // Replace "1)" with the input number followed by ")"
        String afterReplacement = html.replace("1)", number + "");
        System.out.println("after no replacement: " + afterReplacement);
        return afterReplacement;
    }

    private static String removeBodyHtmlTags(String input) {
        String cleaned = input
                .replace("<html>", "")
                .replace("</html>", "")
                .replace("<body>", "")
                .replace("</body>", "");
        System.out.println("Cleaned: " + cleaned);
        return cleaned;
    }

}
