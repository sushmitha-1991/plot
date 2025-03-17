package com.dsp.plot.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dsp.plot.entity.PlotInput;
import com.dsp.plot.entity.PlotLtno;
public interface PlotLtnoRepository extends JpaRepository <PlotLtno, Long>,JpaSpecificationExecutor<PlotLtno>{

    Optional<PlotLtno> findByIdAndIsActive(Long id, boolean b);

}