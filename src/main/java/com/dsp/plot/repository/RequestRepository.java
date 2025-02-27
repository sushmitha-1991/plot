package com.dsp.plot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dsp.plot.entity.MastersRajyaSabha;
import com.dsp.plot.entity.Request;


@Repository
public interface RequestRepository extends JpaRepository <Request, Long>{

   List<Request> findByPlotInputId(Long plotId);

}
