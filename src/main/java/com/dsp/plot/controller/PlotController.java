package com.dsp.plot.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/plot-ltno")
public class PlotController {
    @Autowired
    private PlotLtnoService service;

    @PostMapping("/paper-title/create")
    public ResponseEntity<GenericResponse<PlotLaidOnTableDTO>> createTitle(@RequestBody PlotLaidOnTableDTO plotLaidOnTableDTO) {
        PlotLaidOnTableDTO savedEntity = service.saveTitle(plotLaidOnTableDTO);

        GenericResponse<PlotLaidOnTableDTO> response = new GenericResponse<>(HttpStatus.CREATED.value(), true,
                "PlotLtno created successfully", savedEntity);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PostMapping("/create")
    public ResponseEntity<GenericResponse<PlotLtnoDto>> create(@RequestBody PlotLtnoDto plotLtnoDto) {
        PlotLtnoDto savedEntity = service.save(plotLtnoDto);

        GenericResponse<PlotLtnoDto> response = new GenericResponse<>(HttpStatus.CREATED.value(), true,
                "PlotLtno created successfully", savedEntity);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/getAll")
    public ResponseEntity<GenericResponse<List<PlotLtnoDto>>> getAll(
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Long lokSabhaNo,
            @RequestParam(required = false) Long lokSabhaSessionNo,
            @RequestParam(required = false) Long rajyaSabhaSessionNo,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long ministryId,
            @RequestParam(required = false) String ltnoNo,
            @RequestParam(required = false) Long paperTypeId) {
        List<PlotLtnoDto> fectchedEntities = service.getAll(isActive, lokSabhaNo, lokSabhaSessionNo,
                rajyaSabhaSessionNo, startDate, endDate, status,ministryId,ltnoNo,paperTypeId);

        GenericResponse<List<PlotLtnoDto>> response = new GenericResponse<>(HttpStatus.OK.value(), true,
                "Plot retrieved successfully", fectchedEntities);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getTitles")
    public ResponseEntity<GenericResponse<List<PlotLaidOnTableDTO>>> getTitles(
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Long lokSabhaNo,
            @RequestParam(required = false) Long lokSabhaSessionNo,
            @RequestParam(required = false) Long rajyaSabhaSessionNo,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long ministryId) {
        List<PlotLaidOnTableDTO> fectchedEntities = service.getTitles(isActive, lokSabhaNo, lokSabhaSessionNo,
                rajyaSabhaSessionNo, startDate, endDate, status,ministryId);

        GenericResponse<List<PlotLaidOnTableDTO>> response = new GenericResponse<>(HttpStatus.OK.value(), true,
                "Plot retrieved successfully", fectchedEntities);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<GenericResponse<PlotLtnoDto>> getById(@PathVariable Long id) {

        PlotLtnoDto fetchedEntity = service.getById(id);

        GenericResponse<PlotLtnoDto> response = new GenericResponse<>(HttpStatus.OK.value(), true,
                "Retrieved Plot By Id", fetchedEntity);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GenericResponse<PlotLtnoDto>> update(@PathVariable Long id,
            @RequestBody PlotLtnoDto plotLtnoDto) {

        PlotLtnoDto updatedEntity = service.update(id, plotLtnoDto);

        GenericResponse<PlotLtnoDto> response = new GenericResponse<>(HttpStatus.OK.value(), true,
                "Plot updated successfully", updatedEntity);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/getTitle/{id}")
    public ResponseEntity<GenericResponse<PlotLaidOnTableDTO>> getTitleById(@PathVariable Long id) {

        PlotLaidOnTableDTO fetchedEntity = service.getTitleById(id);

        GenericResponse<PlotLaidOnTableDTO> response = new GenericResponse<>(HttpStatus.OK.value(), true,
                "Retrieved Plot By Id", fetchedEntity);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/updateTitle/{id}")
    public ResponseEntity<GenericResponse<PlotLaidOnTableDTO>> updateTitle(@PathVariable Long id,
            @RequestBody PlotLaidOnTableDTO plotDto) {
                PlotLaidOnTableDTO updatedEntity = service.updateTitle(id, plotDto);
        GenericResponse<PlotLaidOnTableDTO> response = new GenericResponse<>(HttpStatus.OK.value(), true,
                "Plot updated successfully", updatedEntity);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
