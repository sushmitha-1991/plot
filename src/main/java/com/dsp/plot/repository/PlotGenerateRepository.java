package com.dsp.plot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.dsp.plot.entity.PlotGenerate;


@Repository
public interface PlotGenerateRepository extends JpaRepository <PlotGenerate, Long>,JpaSpecificationExecutor<PlotGenerate> {

        Optional<PlotGenerate> findByIdAndIsActive(Long id, boolean isActive);
        // Optional<List<PlotInput>> findByHouseIdAndIsActive(Long houseId, boolean isActive);
        Optional<List<PlotGenerate>> findByIsActive(boolean isActive);
}

