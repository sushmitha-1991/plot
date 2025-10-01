package com.dsp.plot.onlyoffice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dsp.plot.dto.SerialNumberRequest;
import com.dsp.plot.dto.SerialNumberResponse;
import com.dsp.plot.entity.PlotHistory;
import com.dsp.plot.entity.PlotInput;
import com.dsp.plot.onlyoffice.DocumentConfigDTO;
import com.dsp.plot.onlyoffice.DocumentRequestDTO;
import com.dsp.plot.repository.MasterLSSRepository;
import com.dsp.plot.repository.MastersRSSRepository;
import com.dsp.plot.repository.MyUserRepository;
import com.dsp.plot.repository.PlotGenerateRepository;
import com.dsp.plot.repository.PlotHistoryRepository;
import com.dsp.plot.repository.PlotIndexRepository;
import com.dsp.plot.repository.PlotInputRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class OnlyOfficeService {

    @Value("${workflow.service.url}")
    private String draftUrl;
    @Value("${feign.client.bearer.url}")
    private String serviceUrl;

    private static final String LS = "LS";
    private static final String RS = "RS";

    private static final Logger log = LoggerFactory.getLogger(OnlyOfficeService.class);
    @Autowired
    private HttpServletRequest httpRequest;

    @Autowired
    private PlotInputRepository inputRepository;
    @Autowired
    private PlotHistoryRepository historyRepository;
    @Autowired
    private RestTemplate restTemplate;

    public String getTokenFromRequest() {
        String authorizationHeader = httpRequest.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    public DocumentConfigDTO getOnlyOfficeConfig(DocumentRequestDTO requestDTO) {
        // String url = serviceUrl + "/only-office/config";
        String url =   "http://localhost:8222/only-office/config";
        String token = getTokenFromRequest();
        if (token == null) {
            throw new IllegalStateException("Authorization token is missing");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<DocumentRequestDTO> entity = new HttpEntity<>(requestDTO, headers);

        System.out.println("url "+url);
        System.out.println("entity "+entity);
        try {
            return restTemplate.postForObject(url, entity, DocumentConfigDTO.class);
        } catch (Exception e) {
            System.err.println("Error during API call: " + e.getMessage());
            throw e;
        }

    }

    public Map<String, Object> getOnlyOfficeInput(String refId) {
        // TODO Auto-generated method stub
        Map<String, Object> res = new HashMap<>();
        Long id = Long.parseLong(refId);
        Optional<PlotInput> input = inputRepository.findById(id);
        if (input.isEmpty()) {
            throw new RuntimeException("No data found with shared ref id " + refId);
        }

        PlotInput inputContent = input.get();
        res.put("input-content", inputContent.getContent());
        return res;

    }

    public Map<String, Object> getOnlyOfficeInputForGeneration(String refId) {
        // TODO Auto-generated method stub
Map<String, Object> res = new HashMap<>();
        Long id = Long.parseLong(refId);
        Optional<PlotHistory> history = historyRepository.findPlotHistory(id);
        if (history.isEmpty()) {
            throw new RuntimeException("No data found with shared ref id " + refId);
        }

        PlotHistory historyInput = history.get();
        res.put("input-content", historyInput.getAddedText());
        return res;    }

}