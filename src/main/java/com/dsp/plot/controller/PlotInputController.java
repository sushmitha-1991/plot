package com.dsp.plot.controller;

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
import org.springframework.web.bind.annotation.RestController;

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
        public ResponseEntity<GenericResponse<List<PlotInputDTO>>> getAllPlotInputs() {

                List<PlotInputDTO> fectchedEntities = plotinputService.getAll();

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

        // @Operation(summary = "Generate Application No", description = "Generate Application No.")
        // @ApiResponses(value = {
        //                 @ApiResponse(responseCode = "200", description = "ApplicationNo generated successfully"),
        //                 @ApiResponse(responseCode = "404", description = "Id not found"),
        //                 @ApiResponse(responseCode = "500", description = "Internal server error") })
        // @PostMapping("/generateNo")
        // public String generateApplicationNo(@RequestBody GenerateDTO dto) {
        //         if (dto.getDraftFlag() == null || (!dto.getDraftFlag())) {
        //                 return "PLOT" + dto.getId() + "";
        //         } else {
        //                 return "DPLOT" + dto.getId() + "";
        //         }
        // }

        @Operation(summary = "save request", description = "Raise Requests.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "raised request successfully"),
                        @ApiResponse(responseCode = "500", description = "Internal server error") })
        @PostMapping("/raise-request")
        public ResponseEntity<GenericResponse<RequestDTO>> createRequest(@RequestBody RequestDTO dto) {
                RequestDTO response=plotinputService.saveRequest(dto);
                GenericResponse<RequestDTO> responseDTO = new GenericResponse<>(HttpStatus.OK.value(), 
                true,"Request raised successfully.", response);
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }

        @Operation(summary = "Get all request ", description = "Retrieve request by its id.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "PlotDoc retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "PlotDoc not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error") })
        @GetMapping("/getAllRequests")
        public ResponseEntity<GenericResponse<List<RequestDTO>>> getAllRequests() {
                List<RequestDTO> response=plotinputService.getAllRequests();
                GenericResponse<List<RequestDTO>> responseDTO = new GenericResponse<>(HttpStatus.OK.value(), 
                true,"Requests retrieved successfully.", response);
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }
}
