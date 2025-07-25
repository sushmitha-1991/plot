package com.dsp.plot.dsp1integration;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsp.plot.dto.PlotLaidOnTableDTO;
import com.dsp.plot.dto.PlotLtnoDto;
import com.dsp.plot.response.GenericResponse;
import com.dsp.plot.service.PlotLtnoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/ltno")
public class LtnoResponseController {
    @Autowired
private LtnoResponseService service;
 @GetMapping("/getAll")
        public ResponseEntity<GenericResponse<List<LtnoResponseDTO>>> getAll(
                        @RequestParam(required = false) String loksabha,
                        @RequestParam(required = false) String session,
                        @RequestParam(required = false) LocalDate paperDateFrom,
                        @RequestParam(required = false) LocalDate paperDateTo,
                        @RequestParam(required = false) String locale,
                        @RequestParam(required = false) Integer minCode,
                        @RequestParam(required = false) String ministryNames,
                        @RequestParam(required = false) String reportType,
                        @RequestParam(required = false) String house) {
                List<LtnoResponseDTO> fectchedEntities = service.getAll(loksabha,session,
                paperDateFrom,paperDateTo,locale,minCode,ministryNames,reportType,house);
                GenericResponse<List<LtnoResponseDTO>> response = new GenericResponse<>(HttpStatus.OK.value(), true,
                                "Plot retrieved successfully", fectchedEntities);
                return new ResponseEntity<>(response, HttpStatus.OK);
        }


}
