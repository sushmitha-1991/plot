package com.dsp.plot.service.impl;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

import com.dsp.plot.dto.PlotLtnoDto;
import com.dsp.plot.dto.SerialNumberRequest;
import com.dsp.plot.dto.SerialNumberResponse;
import com.dsp.plot.dto.UserDetailsDTO;
import com.dsp.plot.entity.MastersLokSabhaSession;
import com.dsp.plot.entity.MastersRajyaSabha;
import com.dsp.plot.entity.PlotLtno;
import com.dsp.plot.exception.ResourceNotFoundException;
import com.dsp.plot.mapper.LtnoMapper;
import com.dsp.plot.repository.MasterLSSRepository;
import com.dsp.plot.repository.MastersRSSRepository;
import com.dsp.plot.repository.PlotLtnoRepository;
import com.dsp.plot.service.PlotLtnoService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class PlotLtnoServiceImpl implements PlotLtnoService {

    @Value("${master.service.url}")
    private String masterServiceUrl;
    @Autowired
    private HttpServletRequest httpRequest;
    @Autowired
    private PlotLtnoRepository ltnoRepo;
    @Autowired
    private MastersRSSRepository mastersRSSRepository;
    @Autowired
    private MasterLSSRepository masterLSSRepository;
    @Autowired
    private RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(PlotInputServiceImpl.class);

    @Transactional
    @Override
    public PlotLtnoDto save(PlotLtnoDto plotDTO) {
        if (plotDTO.getStatus() == null) {
            plotDTO.setStatus("In Progress");
        }
        log.debug("Entering savePlot with plotDTO: {}", plotDTO);
        try {
            // Convert DTO to entity
            PlotLtno entity = LtnoMapper.toEntity(plotDTO);
            log.debug("Converted PlotDTO to entity: {}", entity);
            // Save Plot entity
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
                serialNumberRequest.setSessionId(entity.getLokSabhaSessionNo());
            }
            if (StringUtils.isBlank(plotDTO.getApplicationNo())) {
                SerialNumberResponse applicationNo = generateApplicationNo(serialNumberRequest);
                entity.setApplicationNo(applicationNo.getId());
                entity.setRefNo(applicationNo.getId());
            }
            entity.setCurrentStatus(entity.getStatus());
            entity.setDocumentDate(entity.getLtnoDate());
            entity = ltnoRepo.save(entity);
            entity.setRefId(entity.getId());
            log.info("Saved Bulletin1 attributes: ID={},  Status={}", entity.getId(), entity.getStatus());
            // entity = plotRepository.save(entity);
            // Convert saved entity back to DTO
            PlotLtnoDto responseDTO = LtnoMapper.toDTO(entity);
            log.debug("Returning PlotInputDTO: {}", responseDTO);
            return responseDTO;
        } catch (Exception e) {
            log.error("Error in savePlotInput: ", e);
            throw e;
        }
    }

    @Override
    public List<PlotLtnoDto> getAll(Boolean isActive, Long lokSabhaNo,
            Long lokSabhaSessionNo, Long rajyaSabhaSessionNo, LocalDate startDate, LocalDate endDate,
            String status) {
        log.debug("Entering getAll Plots");
        List<PlotLtnoDto> dtoList = new ArrayList<>();
        List<PlotLtno> entitys;
        // if (draftStatus == null) {
        // draftStatus = false;
        // }
        if (isActive == null) {
            isActive = true;
        }
        Specification<PlotLtno> spec = Specification.where(null);
        // spec = spec.and(PlotInputSpec.isActive(isActive));
        // if (draftStatus != null) {
        // spec = spec.and(PlotInputSpec.hasDraftStatus(draftStatus));
        // }
        // if (lokSabhaSessionNo != null) {
        // spec = spec.and(PlotInputSpec.hasLokSabhaSessionNo(lokSabhaSessionNo));
        // }
        // if (rajyaSabhaSessionNo != null) {
        // spec = spec.and(PlotInputSpec.hasRajyaSabhaSessionNo(rajyaSabhaSessionNo));
        // }
        // if (startDate != null || endDate != null) {
        // spec = spec.and(PlotInputSpec.hasDateBetween(startDate, endDate));
        // }
        // if (status != null) {
        // spec = spec.and(PlotInputSpec.hasStatus(status));
        // }
        entitys = ltnoRepo.findAll();
        // entitys = ltnoRepo.findAll(spec);
        Long houseId = getHouseId();
        for (PlotLtno entity : entitys) {
            if (houseId == 1) {
                if (entity.getRajyaSabhaSessionNo() != null) {
                    PlotLtnoDto dto = LtnoMapper.toDTO(entity);
                    dtoList.add(dto);
                }
            } else if (houseId == 2) {
                if (entity.getLokSabhaSessionNo() != null) {
                    PlotLtnoDto dto = LtnoMapper.toDTO(entity);
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
    public PlotLtnoDto getById(Long id) {
        log.debug("Entering getById with id: {}", id);
        PlotLtno dBEntity = ltnoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plot Ltno not found with id: " + id));
        log.debug("Fetched Plot LTNO by id: {}", dBEntity);
        PlotLtnoDto dto = LtnoMapper.toDTO(dBEntity);
        return dto;
    }

    @Transactional
    @Override
    public PlotLtnoDto update(Long id, PlotLtnoDto plotDTO) {
        log.debug("Starting update for Plot with ID: {}", id);
        // Fetch the existing PlotInput
        PlotLtno dbEntity = ltnoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plot Ltno not found with id: " + id));
        // Update only the fields you want to update: TODO. Default -> All parameters
        // are updated
        PlotLtno dbEntityNew = LtnoMapper.toEntity(plotDTO);
        
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
        dbEntity.setDocumentDate(plotDTO.getLtnoDate());
        dbEntity.setCurrentStatus(plotDTO.getStatus());
        // Save the updated PlotInput
        dbEntity = ltnoRepo.save(dbEntity);
        // Prepare the response DTO
        return (LtnoMapper.toDTO(dbEntity));
    }

    @Override
    public List<PlotLtnoDto> getAll() {
        // TODO Auto-generated method stub
        return ltnoRepo.findAll().stream().map(LtnoMapper::toDTO).collect(Collectors.toList());

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

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}
