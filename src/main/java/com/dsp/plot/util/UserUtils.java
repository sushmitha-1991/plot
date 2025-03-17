package com.dsp.plot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.dsp.plot.dto.UserDetailsDTO;
import com.dsp.plot.entity.BaseUser;
import com.dsp.plot.exception.BadRequest;
import com.dsp.plot.repository.MyUserRepository;

@Component
public class UserUtils {
    @Autowired
    private MyUserRepository myUserRepository;
    public Long getUserId() {
        Long userId;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsDTO userDetailsDTO) {
            userId =userDetailsDTO.getId();
        } else {
            throw new BadRequest("Bad Authentication Could not not find userId");
        }
        return userId;
    }

    public Long getHouseId() {
        Long houseId;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsDTO userDetailsDTO) {
            houseId =userDetailsDTO.getHouseId();
        } else {
            throw new BadRequest("Bad Authentication Could not find house id");
        }
        return houseId;
    }

    public String getUserAbbr() {
        String houseId;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsDTO userDetailsDTO) {
            houseId =userDetailsDTO.getAbbr();
        } else {
            throw new BadRequest("Bad Authentication Could not find user abbr");
        }
        return houseId;
    }
    //copied from template Engine
	public Long getUserIdFromAuthentication() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("No authenticated user found");
		}

		String username = authentication.getName();
		BaseUser baseUser = myUserRepository.findByUsername(username);
		if (baseUser == null) {
			throw new UsernameNotFoundException("Authenticated user not found in the database");
		}

		return baseUser.getId();
	}
}
