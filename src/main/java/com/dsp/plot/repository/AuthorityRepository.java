package com.dsp.plot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import com.dsp.plot.entity.Ministries;
import com.dsp.plot.entity.MinistryAuthority;
@Repository
public interface AuthorityRepository extends JpaRepository<MinistryAuthority,Long>{
    
}
