/**
 * 
 * @author 
 * @version 
 */
 
package com.dsp.plot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsp.plot.entity.BaseUser;

@Repository
public interface MyUserRepository extends JpaRepository<BaseUser, Long> {
	
	BaseUser findByUsername(String username);

	BaseUser findByEmail(String email);

	BaseUser findByPhone(String phone);
}
