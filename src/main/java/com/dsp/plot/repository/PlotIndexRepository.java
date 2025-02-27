package com.dsp.plot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.dsp.plot.dto.PlotIndexDTO;
import com.dsp.plot.entity.PlotGenerate;
import com.dsp.plot.entity.PlotIndex;

@Repository
public interface PlotIndexRepository extends JpaRepository<PlotIndex, Long>, JpaSpecificationExecutor<PlotIndex>  {


}
