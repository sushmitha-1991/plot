package com.dsp.plot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsp.plot.entity.Departments;

    
@Repository
public interface DepartmentRepository extends JpaRepository<Departments,Long>{
    
}
