package com.dsp.plot.repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.dsp.plot.entity.PlotInput;


@Repository
public interface PlotInputRepository extends JpaRepository <PlotInput, Long> ,JpaSpecificationExecutor<PlotInput>{

        Optional<PlotInput> findByIdAndIsActive(Long id, boolean isActive);
        // Optional<List<PlotInput>> findByHouseIdAndIsActive(Long houseId, boolean isActive);
        Optional<List<PlotInput>> findByIsActive(boolean isActive);
}
