package com.dsp.plot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsp.plot.entity.PlotInput;

import jakarta.transaction.Transactional;

@Repository
public interface PlotInputRepository extends JpaRepository<PlotInput, Long>, JpaSpecificationExecutor<PlotInput> {

        Optional<PlotInput> findByIdAndIsActive(Long id, boolean isActive);

        // Optional<List<PlotInput>> findByHouseIdAndIsActive(Long houseId, boolean
        // isActive);
        Optional<List<PlotInput>> findByIsActive(boolean isActive);

        @Query(value = "SELECT COUNT(*) FROM dms.document_metadata WHERE uuid = :uuid", nativeQuery = true)
        int checkUuidExists(@Param("uuid") String uuid);

        @Modifying
        @Transactional
        @Query(value = "UPDATE dms.document_metadata SET application_no = :applicationNo WHERE uuid = :uuid", nativeQuery = true)
        void updateApplicationNoByUUID(@Param("uuid") String uuid, @Param("applicationNo") String applicationNo);

        Optional<PlotInput> findByRefNo(String applicationNo);
        @Query(value = "Select h.abbr from master.houses where h.id=:id")
        String fetchHouseAbbrByhouseId(Long id);

}
