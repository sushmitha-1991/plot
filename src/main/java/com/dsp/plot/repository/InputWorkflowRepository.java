/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.plot.repository;


import com.dsp.plot.entity.InputWorkflowHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * 
 */
@Repository
public interface InputWorkflowRepository extends JpaRepository<InputWorkflowHistory, Long>{

}
