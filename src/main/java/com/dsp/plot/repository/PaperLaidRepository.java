package com.dsp.plot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.dsp.plot.entity.PaperLaidOnTable;

@Repository
public interface PaperLaidRepository extends JpaRepository<PaperLaidOnTable,Long>,JpaSpecificationExecutor<PaperLaidOnTable>{

    Optional<PaperLaidOnTable> findByIdAndIsActive(Long id, boolean b);
    
}