package com.dsp.plot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsp.plot.entity.Ministries;
@Repository
public interface MinistriesRepository extends JpaRepository<Ministries,Long>{
    Optional<Ministries> findByMinistryName(String ministryName);
    @Query(value = "SELECT m.* FROM master.ministries m WHERE m.ministry_code = :code", nativeQuery = true)
    Optional<Ministries> findByMinistryCode(@Param("code") Integer code);
}

