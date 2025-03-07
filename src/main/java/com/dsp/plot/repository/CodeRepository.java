package com.dsp.plot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import com.dsp.plot.entity.MasterCode;
import com.dsp.plot.entity.Ministries;
@Repository
public interface CodeRepository extends JpaRepository<MasterCode,Long>{
    // Optional<Ministries> findByMinistryName(String ministryName);
}

