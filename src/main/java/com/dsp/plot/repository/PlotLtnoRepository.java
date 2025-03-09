package com.dsp.plot.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dsp.plot.entity.PlotLtno;
public interface PlotLtnoRepository extends JpaRepository <PlotLtno, Long>,JpaSpecificationExecutor<PlotLtno>{

}