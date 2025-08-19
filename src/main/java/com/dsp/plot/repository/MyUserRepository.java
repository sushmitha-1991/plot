/**
 * 
 * @author 
 * @version 
 */
 
package com.dsp.plot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dsp.plot.entity.BaseUser;

@Repository
public interface MyUserRepository extends JpaRepository<BaseUser, Long> {
	
	BaseUser findByUsername(String username);

	BaseUser findByEmail(String email);

	BaseUser findByPhone(String phone);
	@Query(value="SELECT abbr FROM master.houses where id=:id and is_active =true limit 1 ",nativeQuery=true)
	Optional<String> findHouseAbbrById(Long id);
}
