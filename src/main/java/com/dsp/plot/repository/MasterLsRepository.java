
package com.dsp.plot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsp.plot.entity.MastersLokSabha;
import com.dsp.plot.entity.MastersLokSabhaSession;

@Repository
public interface MasterLsRepository extends JpaRepository<MastersLokSabha, Long> {
    Optional<MastersLokSabha> findByLokSabhaNumber(String lokSabhaNo);
    
}
