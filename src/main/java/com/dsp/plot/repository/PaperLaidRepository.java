package com.dsp.plot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.dsp.plot.entity.PaperLaidOnTable;

@Repository
public interface PaperLaidRepository extends JpaRepository<PaperLaidOnTable,Long>,JpaSpecificationExecutor<PaperLaidOnTable>{
    
}