package com.dsp.plot.onlyoffice.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsp.plot.dto.PlotGenerateDTO;
import com.dsp.plot.onlyoffice.DocumentConfigDTO;
import com.dsp.plot.onlyoffice.DocumentRequestDTO;
import com.dsp.plot.onlyoffice.service.OnlyOfficeService;
import com.dsp.plot.response.GenericResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/only-office")
public class OnlyOfficeController {
@Autowired
private OnlyOfficeService onlyOfficeService;


    @PostMapping("/config")
        public ResponseEntity<GenericResponse<DocumentConfigDTO>> createPlotGenerate(
                        @RequestBody DocumentRequestDTO docuReq) {
              DocumentConfigDTO   configRes = onlyOfficeService.getOnlyOfficeConfig(docuReq);

                GenericResponse<DocumentConfigDTO> response = new GenericResponse<>(HttpStatus.CREATED.value(), true,
                                "config only office", configRes);

                return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        @GetMapping("/get-input-doc/{refId}")
        public ResponseEntity<GenericResponse<Map<String,Object>>> getOnlyOfficeInput(@PathVariable String refId) {
            Map<String,Object>  input=onlyOfficeService.getOnlyOfficeInput(refId);
            
                GenericResponse<Map<String,Object>> response = new GenericResponse<>(HttpStatus.CREATED.value(), true,
                                "plot input content", input);

                return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
         @GetMapping("/get-input-doc-for-generation/{refId}")
        public ResponseEntity<GenericResponse<Map<String,Object>>> getOnlyOfficeInputForGeneration(@PathVariable String refId) {
            Map<String,Object>  input=onlyOfficeService.getOnlyOfficeInputForGeneration(refId);
            
                GenericResponse<Map<String,Object>> response = new GenericResponse<>(HttpStatus.CREATED.value(), true,
                                "plot input content", input);

                return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        
}
//getOnlyOfficeConfig