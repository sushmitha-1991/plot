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
        @Query(value = "Select abbr from master.houses where id=:id",nativeQuery = true)
        String fetchHouseAbbrByhouseId(Long id);

        @Query(value="select m.ministryName,d.departmentName from PlotInput pi "+
        "join Ministries m on m.id=pi.ministryId "+
        "join Departments d on d.id=pi.departmentId "+
        "where pi.id=:id")
       Map<String,Object> findMinisterAndDepartmentById(Long id);
       @Query("SELECT m.ministryName, d.departmentName " +
       "FROM PlotInput pi " +
       "JOIN Ministry m ON m.id = pi.ministryId " +
       "JOIN Department d ON d.id = pi.departmentId " +
       "WHERE pi.id = :id")
List<Object[]> findMinistryAndDepartmentByPlotId(@Param("id") Long id);

}
