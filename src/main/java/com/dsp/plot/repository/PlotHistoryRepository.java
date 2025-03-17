package com.dsp.plot.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsp.plot.dto.PlotHistoryDTO;
import com.dsp.plot.entity.PlotHistory;
import com.dsp.plot.entity.PlotInput;

@Repository
public interface PlotHistoryRepository extends JpaRepository<PlotHistory, Long>, JpaSpecificationExecutor<PlotHistory> {
    @Query(value = "SELECT * FROM plot.plot_history WHERE plot_input_id = :plotInputId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<PlotHistory> findPlotHistory(@Param("plotInputId") Long plotInputId);

    List<PlotHistory> findAllByPlotInputId(Long plotInputId);

    List<PlotHistory> findAllByPlotInputIdOrderByCreatedAtDesc(Long id);

    List<PlotHistory> findAllByPlotInputIdOrderByCreatedAtAsc(Long id);

}
