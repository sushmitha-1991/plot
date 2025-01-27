package com.dsp.plot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dsp.plot.entity.MastersLokSabhaSession;
@Repository
public interface MasterLSSRepository extends JpaRepository<MastersLokSabhaSession,Long>{
@Query(value = "SELECT id FROM master.lok_sabha_session_masters ORDER BY created_by DESC LIMIT 1", nativeQuery = true)
    Long findLatestByCreatedBy();
}
