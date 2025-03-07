package com.dsp.plot.controller;

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

import com.dsp.plot.dto.PlotDateChartDTO;
import com.dsp.plot.dto.PlotInputDTO;
import com.dsp.plot.dto.RequestDTO;
import com.dsp.plot.response.GenericResponse; // TODO: Set exact file location
import com.dsp.plot.service.PlotInputService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/plot-input")
public class PlotInputController {

        @Autowired
        private PlotInputService plotinputService;

        @Operation(summary = "Create a new PlotInput", description = "Creates a new PlotInput and returns the created PlotInput details.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "PlotInput created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
                        @ApiResponse(responseCode = "500", description = "Internal server error") })
        @PostMapping("/create")
        public ResponseEntity<GenericResponse<PlotInputDTO>> createPlotInput(
                        @RequestBody PlotInputDTO plotinputDTO) {

                PlotInputDTO savedEntity = plotinputService.save(plotinputDTO);

                GenericResponse<PlotInputDTO> response = new GenericResponse<>(HttpStatus.CREATED.value(), true,
                                "PlotInput created successfully", savedEntity);

                return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        @Operation(summary = "Get all PlotInputs", description = "Retrieves a list of all PlotInputs.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "PlotInputs retrieved successfully"),
                        @ApiResponse(responseCode = "500", description = "Internal server error") })
        @GetMapping("/getAll")
        public ResponseEntity<GenericResponse<List<PlotInputDTO>>> getAllPlotInputs(
                        @RequestParam(required = false) Boolean isActive,
                        @RequestParam(required = false) Boolean draftStatus,
                        @RequestParam(required = false) Long lokSabhaNo,
                        @RequestParam(required = false) Long lokSabhaSessionNo,
                        @RequestParam(required = false) Long rajyaSabhaSessionNo,
                        @RequestParam(required = false) LocalDate startDate,
                        @RequestParam(required = false) LocalDate endDate,
                        @RequestParam(required = false) String status) {

                List<PlotInputDTO> fectchedEntities = plotinputService.getAll(isActive, draftStatus, lokSabhaNo,
                                lokSabhaSessionNo,
                                rajyaSabhaSessionNo, startDate, endDate, status);

                GenericResponse<List<PlotInputDTO>> response = new GenericResponse<>(HttpStatus.OK.value(), true,
                                "PlotInputs retrieved successfully", fectchedEntities);

                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @Operation(summary = "Get all PlotInputs", description = "Retrieves a list of all PlotInputs.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "PlotInputs retrieved successfully"),
                        @ApiResponse(responseCode = "500", description = "Internal server error") })
        @GetMapping("/get-plot-inputs")
        public ResponseEntity<GenericResponse<List<PlotInputDTO>>> getAllPlotInputsHistories(
                        @RequestParam(required = false) Boolean isActive,
                        @RequestParam(required = false) Boolean draftStatus,
                        @RequestParam(required = false) Long lokSabhaNo,
                        @RequestParam(required = false) Long lokSabhaSessionNo,
                        @RequestParam(required = false) Long rajyaSabhaSessionNo,
                        @RequestParam(required = false) LocalDate startDate,
                        @RequestParam(required = false) LocalDate endDate,
                        @RequestParam(required = false) String status) {

                List<PlotInputDTO> fectchedEntities = plotinputService.getHistories(isActive, draftStatus, lokSabhaNo,
                                lokSabhaSessionNo,
                                rajyaSabhaSessionNo, startDate, endDate, status);

                GenericResponse<List<PlotInputDTO>> response = new GenericResponse<>(HttpStatus.OK.value(), true,
                                "PlotInputs retrieved successfully", fectchedEntities);

                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @Operation(summary = "Get PlotInput By Id", description = "Retrieve PlotInput by its Id.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "PlotInput retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "PlotInput not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error") })
        @GetMapping("/get/{id}")
        public ResponseEntity<GenericResponse<PlotInputDTO>> getPlotInputById(@PathVariable Long id) {

                PlotInputDTO fetchedEntity = plotinputService.getById(id);

                GenericResponse<PlotInputDTO> response = new GenericResponse<>(HttpStatus.OK.value(), true,
                                "Retrieved PlotInput By Id", fetchedEntity);

                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @Operation(summary = "Update PlotInput", description = "Updates a PlotInput by its ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "PlotInput updated successfully"),
                        @ApiResponse(responseCode = "404", description = "PlotInput not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error") })
        @PutMapping("/update/{id}")
        public ResponseEntity<GenericResponse<PlotInputDTO>> updatePlotInput(@PathVariable Long id,
                        @RequestBody PlotInputDTO plotinputDTO) {

                PlotInputDTO updatedEntity = plotinputService.update(id, plotinputDTO);

                GenericResponse<PlotInputDTO> response = new GenericResponse<>(HttpStatus.OK.value(), true,
                                "PlotInput updated successfully", updatedEntity);

                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @Operation(summary = "Update PlotInput", description = "Updates a PlotInput by its ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "PlotInput updated successfully"),
                        @ApiResponse(responseCode = "404", description = "PlotInput not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error") })
        @PutMapping("/updatedAtCreator/{id}")
        public ResponseEntity<GenericResponse<PlotInputDTO>> updateAtCreator(@PathVariable Long id,
                        @RequestBody PlotInputDTO plotinputDTO) {
                if (plotinputDTO.getStatus().equalsIgnoreCase("returned")) {

                        PlotInputDTO updatedEntity = plotinputService.update(id, plotinputDTO);

                        GenericResponse<PlotInputDTO> response = new GenericResponse<>(HttpStatus.OK.value(), true,
                                        "PlotInput updated successfully", updatedEntity);

                        return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                        PlotInputDTO updatedEntity = plotinputService.update(id, plotinputDTO);
                        GenericResponse<PlotInputDTO> response = new GenericResponse<>(HttpStatus.OK.value(), true,
                                        "PlotInput updated successfully", updatedEntity);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                }

        }

        @Operation(summary = "Delete a PlotInput by ID", description = "Deletes a specific PlotInput by its ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "PlotInput deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "PlotInput not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error") })
        @DeleteMapping("/delete/{id}")
        public ResponseEntity<GenericResponse<String>> deletePlotInput(@PathVariable Long id) {

                plotinputService.delete(id);

                GenericResponse<String> response = new GenericResponse<>(HttpStatus.OK.value(), true,
                                "PlotInput deleted successfully", null);

                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @Operation(summary = "save request", description = "Raise Requests.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "raised request successfully"),
                        @ApiResponse(responseCode = "500", description = "Internal server error") })
        @PostMapping("/raise-request")
        public ResponseEntity<GenericResponse<RequestDTO>> createRequest(@RequestBody RequestDTO dto) {
                RequestDTO response = plotinputService.saveRequest(dto);
                GenericResponse<RequestDTO> responseDTO = new GenericResponse<>(HttpStatus.OK.value(),
                                true, "Request raised successfully.", response);
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }

        @GetMapping("/getRequest/{id}")
        public ResponseEntity<GenericResponse<RequestDTO>> getRequestById(@PathVariable Long id) {

                RequestDTO fetchedEntity = plotinputService.getRequestById(id);

                GenericResponse<RequestDTO> response = new GenericResponse<>(HttpStatus.OK.value(), true,
                                "Retrieved PlotInput By Id", fetchedEntity);

                return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @Operation(summary = "Save Date Chart ", description = "Adding Date Chart in Plot")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Date Chart Saved Properly"),
                        @ApiResponse(responseCode = "404", description = "PlotDoc not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error") })
        @PostMapping("/addDateChart")
        public ResponseEntity<GenericResponse<PlotDateChartDTO>> addDateChart(@RequestBody PlotDateChartDTO dto) {
                PlotDateChartDTO response = plotinputService.addDateChart(dto);
                GenericResponse<PlotDateChartDTO> responseDTO = new GenericResponse<>(HttpStatus.OK.value(),
                                true, " Manage Date Chart submitted successfully.", response);
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }

        @Operation(summary = "Get all request ", description = "Retrieve request by its id.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "PlotDoc retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "PlotDoc not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error") })
        @GetMapping("/getDateChart/{id}")
        public ResponseEntity<GenericResponse<PlotDateChartDTO>> getDateChartById(@PathVariable Long id) {
                PlotDateChartDTO response = plotinputService.getDateChartById(id);
                GenericResponse<PlotDateChartDTO> responseDTO = new GenericResponse<>(HttpStatus.OK.value(),
                                true, "Requests retrieved successfully.", response);
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }

        @Operation(summary = "Get all request ", description = "Retrieve request by its id.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "PlotDoc retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "PlotDoc not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error") })
        @GetMapping("/getAllRequests/{plotId}")
        public ResponseEntity<GenericResponse<List<RequestDTO>>> getAllRequests(@PathVariable Long plotId) {
                List<RequestDTO> response = plotinputService.getAllRequestsByPlotId(plotId);
                GenericResponse<List<RequestDTO>> responseDTO = new GenericResponse<>(HttpStatus.OK.value(),
                                true, "Requests retrieved successfully.", response);
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }

        @GetMapping("/getDateCharts")
        public ResponseEntity<GenericResponse<List<PlotDateChartDTO>>> getAllDateCharts(
                        @RequestParam(required = false) String lokSabhaNo,
                        @RequestParam(required = false) String lokSabhaSessionNo,
                        @RequestParam(required = false) String rajyaSabhaNo,
                        @RequestParam(required = false) Boolean isActive,
                        @RequestParam(required = false) LocalDate startDate,
                        @RequestParam(required = false) LocalDate endDate,
                        @RequestParam(required = false) String status) {
                List<PlotDateChartDTO> response = plotinputService.getAllDateCharts(lokSabhaNo, lokSabhaSessionNo,rajyaSabhaNo, isActive, startDate, endDate,status);
                GenericResponse<List<PlotDateChartDTO>> responseDTO = new GenericResponse<>(HttpStatus.OK.value(),
                                true, "dateCharts retrieved successfully.", response);
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }
}
