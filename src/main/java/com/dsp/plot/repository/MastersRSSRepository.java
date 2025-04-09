package com.dsp.plot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import com.dsp.plot.entity.MastersRajyaSabha;
@Repository
public interface MastersRSSRepository extends JpaRepository<MastersRajyaSabha,Long>{
     @Query(value = "SELECT id FROM master.rajya_sabha_session_master ORDER BY created_by DESC LIMIT 1",
     nativeQuery = true)
    Long findLatestByCreatedBy();
    @Query(value = "SELECT mr.* FROM master.rajya_sabha_session_master mr where mr.session_number=:sessionNumber",
    nativeQuery = true)
   Optional<MastersRajyaSabha> findBySessionNumber(String sessionNumber);
}
