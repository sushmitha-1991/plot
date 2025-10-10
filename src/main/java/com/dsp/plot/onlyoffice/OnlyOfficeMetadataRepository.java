/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.plot.onlyoffice;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


/**
 * 
 */
@Repository
public interface OnlyOfficeMetadataRepository extends JpaRepository<OnlyOfficeMetadata, Long> {

	/**
	 * @param documentKey
	 * @return
	 */
	Optional<OnlyOfficeMetadata> findByDocumentKey(String documentKey);

	/**
	 * @param key
	 * @return
	 */
	Optional<OnlyOfficeMetadata> findByDsp2Key(String key);
	
	@Query(value="select id FROM corrected_debate.history where allocation_input_id=:refId",nativeQuery=true)
	Long getHistoryId(String refId);

}