package com.dsp.plot.controller;

import com.dsp.plot.dto.PlotHistoryDTO;
import com.dsp.plot.service.PlotHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plot-history")
public class PlotHistoryController {

    @Autowired
    private PlotHistoryService plotHistoryService;

    @PostMapping("/create")
    public ResponseEntity<List<PlotHistoryDTO>> createPlotHistory(@RequestBody List<PlotHistoryDTO> dto) {
        return ResponseEntity.ok(plotHistoryService.createPlotHistory(dto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<PlotHistoryDTO>> getAllPlotHistories() {
        return ResponseEntity.ok(plotHistoryService.getAllPlotHistories());
    }
    @GetMapping("/get-history/{PlotInputId}")
    public ResponseEntity<List<PlotHistoryDTO>> getHistories(@PathVariable Long PlotInputId) {
        return ResponseEntity.ok(plotHistoryService.getHistories(PlotInputId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlotHistoryDTO> getPlotHistoryById(@PathVariable Long id) {
        return ResponseEntity.ok(plotHistoryService.getPlotHistoryById(id));
    }

    // @PutMapping("/update/{id}")
    // public ResponseEntity<PlotHistoryDTO> updatePlotHistory(@PathVariable Long id, @RequestBody PlotHistoryDTO dto) {
    //     return ResponseEntity.ok(plotHistoryService.updatePlotHistory(id, dto));
    // }

    // @DeleteMapping("/delete/{id}")
    // public ResponseEntity<Void> deletePlotHistory(@PathVariable Long id) {
    //     plotHistoryService.deletePlotHistory(id);
    //     return ResponseEntity.noContent().build();
    // }
}
