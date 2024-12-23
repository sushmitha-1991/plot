package com.dsp.plot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsp.plot.entity.Request;


@Repository
public interface RequestRepository extends JpaRepository <Request, Long>{

}
