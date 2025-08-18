package com.dsp.plot.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.dsp.plot.entity.PlotInput;
import com.dsp.plot.entity.PlotLtno;
public interface PlotLtnoRepository extends JpaRepository <PlotLtno, Long>,JpaSpecificationExecutor<PlotLtno>{

    Optional<PlotLtno> findByIdAndIsActive(Long id, boolean b);
 @Query(value = "Select h.abbr from master.houses where h.id=:id",nativeQuery = true)
        String fetchHouseAbbrByhouseId(Long id);
}