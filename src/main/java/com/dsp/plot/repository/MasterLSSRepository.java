package com.dsp.plot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsp.plot.entity.MastersLokSabha;
import com.dsp.plot.entity.MastersLokSabhaSession;

@Repository
public interface MasterLSSRepository extends JpaRepository<MastersLokSabhaSession, Long> {
    @Query(value = "SELECT id FROM master.lok_sabha_session_masters ORDER BY created_by DESC LIMIT 1", nativeQuery = true)
    Long findLatestByCreatedBy();

    @Query(value = "SELECT * FROM master.lok_sabha_session_masters ls WHERE ls.session_number LIKE :lokSabhaSession", nativeQuery = true)
    Optional<MastersLokSabhaSession> findBySessionNumber(@Param("lokSabhaSession") String lokSabhaSession);

    @Query(value = "SELECT * FROM master.lok_sabha_masters ls WHERE ls.lok_sabha_number LIKE :lokSabha", nativeQuery = true)
    Optional<MastersLokSabha> findLokSabhaByLokSabha(@Param("lokSabha") String lokSabha);

    @Query(value = "SELECT * FROM master.lok_sabha_session_masters ls WHERE ls.lok_sabha_master_id = :lokSabhaNo AND ls.session_number LIKE :lokSabhaSession", nativeQuery = true)
    Optional<MastersLokSabhaSession> findBySessionNumberAndLokSabhaMasterId(
            @Param("lokSabhaSession") String lokSabhaSession,
            @Param("lokSabhaNo") Long lokSabhaNo);
}
