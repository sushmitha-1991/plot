package com.dsp.plot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dsp.plot.entity.PlotDateChart;
import com.dsp.plot.entity.PlotInput;

public interface PlotDateChartRepository extends JpaRepository <PlotDateChart, Long> ,JpaSpecificationExecutor<PlotDateChart>{

        Optional<PlotDateChart> findByMinistryId(Long ministryId);
        Optional<PlotDateChart> findByMinistryIdAndLokSabhaNoAndLokSabhaSessionNo(
    Long ministryId, 
    Integer lokSabhaNo, 
    Integer lokSabhaSessionNo
);
        Optional<PlotDateChart> findByMinistryIdAndRajyaSabhaSessionNo(
            Long ministryId, 
            Integer rajyaSabhaSessionNo
        );
        Optional<PlotDateChart> findByMinistryIdAndLokSabhaNoAndLokSabhaSessionNo(Long ministryId, Long lokSabhaNo,
                Long lokSabhaSessionNo);
        Optional<PlotDateChart> findByMinistryIdAndRajyaSabhaSessionNo(Long ministryId, Long rajyaSabhaSessionNo);
    
}
