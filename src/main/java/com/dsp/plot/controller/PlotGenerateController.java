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

import com.dsp.plot.dto.DocumentDTO;
import com.dsp.plot.dto.PlotGenerateDTO;
import com.dsp.plot.response.GenericResponse;
import com.dsp.plot.service.PlotGenerateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
@RestController
@RequestMapping("/plot-generate")
public class PlotGenerateController {
    
    @Autowired
    private PlotGenerateService plotService;

    @Operation(summary = "Create a new PlotGenerate", description = "Creates a new PlotGenerate and returns the created PlotGenerate details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "PlotGenerate created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "500", description = "Internal server error") })
    @PostMapping("/create")
    public ResponseEntity<GenericResponse<PlotGenerateDTO>> createPlotGenerate(
            @RequestBody PlotGenerateDTO plotGenerateDTO) {

     
        PlotGenerateDTO savedEntity = plotService.save(plotGenerateDTO);
        
        GenericResponse<PlotGenerateDTO> response = new GenericResponse<>(HttpStatus.CREATED.value(), true,
                "PlotGenerate created successfully", savedEntity);
       
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

                
    @Operation(summary = "Get all PlotGenerates", description = "Retrieves a list of all PlotGenerates.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PlotGenerates retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error") })
    @GetMapping("/getAll")
    public ResponseEntity<GenericResponse<List<PlotGenerateDTO>>> getAllPlotGenerates() {
        
        List<PlotGenerateDTO> fectchedEntities = plotService.getAll();
  
        GenericResponse<List<PlotGenerateDTO>> response = new GenericResponse<>(HttpStatus.OK.value(), true,
                "PlotGenerates retrieved successfully", fectchedEntities);        

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "Get PlotGenerate By Id", description = "Retrieve PlotGenerate by its Id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PlotGenerate retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "PlotGenerate not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error") })
    @GetMapping("/get/{id}")
    public ResponseEntity<GenericResponse<PlotGenerateDTO>> getPlotGenerateById(@PathVariable Long id) {
       
        PlotGenerateDTO fetchedEntity = plotService.getById(id);
        
        GenericResponse<PlotGenerateDTO> response = new GenericResponse<>(HttpStatus.OK.value(), true,
                "Retrieved PlotGenerate By Id", fetchedEntity);             
  
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "Update PlotGenerate", description = "Updates a PlotGenerate by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PlotGenerate updated successfully"),
            @ApiResponse(responseCode = "404", description = "PlotGenerate not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error") })
    @PutMapping("/update/{id}")
    public ResponseEntity<GenericResponse<PlotGenerateDTO>> updatePlotGenerate(@PathVariable Long id,
            @RequestBody PlotGenerateDTO plotGenerateDTO) {
        
        PlotGenerateDTO updatedEntity = plotService.update(id, plotGenerateDTO);
        
        GenericResponse<PlotGenerateDTO> response = new GenericResponse<>(HttpStatus.OK.value(), true,
                "PlotGenerate updated successfully", updatedEntity);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

   
    @Operation(summary = "Delete a PlotGenerate by ID", description = "Deletes a specific PlotGenerate by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PlotGenerate deleted successfully"),
            @ApiResponse(responseCode = "404", description = "PlotGenerate not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error") })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<GenericResponse<String>> deletePlotGenerate(@PathVariable Long id) {
        
        plotService.delete(id);
        
        GenericResponse<String> response = new GenericResponse<>(HttpStatus.OK.value(), true, 
                                            "PlotGenerate deleted successfully", null);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//     @Operation(summary = "Generate Application No", description = "Generate Application No.")
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "200", description = "ApplicationNo generated successfully"),
//             @ApiResponse(responseCode = "404", description = "Id not found"),
//             @ApiResponse(responseCode = "500", description = "Internal server error") })
//     @PostMapping("/generateNo")
//     public String generateApplicationNo(@RequestBody GenerateDTO dto) 
//     {
//         if(dto.getDraftFlag()==null || (!dto.getDraftFlag()))
//         {
//                 return "PLOT"+dto.getId()+"";
//         }
//         else{
//                 return "DPLOT"+dto.getId()+"";
//         }
//     }
    
    @Operation(summary = "Get PlotDocument By Date", description = "Retrieve PlotDoc by its Date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PlotDoc retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "PlotDoc not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error") })
    @GetMapping("/getDocuments")
        public ResponseEntity<GenericResponse<DocumentDTO>> getDocumentByDate(
                        @RequestParam("date") LocalDate dateOfDocument,
                        @RequestParam("doc") String doc) {
                System.out.println("############");
                DocumentDTO docDTO = plotService.getDocumentByDate(dateOfDocument, doc);
                System.out.println(docDTO);
                GenericResponse<DocumentDTO> response = new GenericResponse<>(HttpStatus.OK.value(), true,
                                "List of Business Input retrieved successfully.", docDTO);
                return new ResponseEntity<>(response, HttpStatus.OK);
        }
}
