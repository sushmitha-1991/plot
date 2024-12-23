package com.dsp.plot.util;

import com.dsp.plot.dto.HouseDTO;
import com.dsp.plot.dto.UserDetailsDTO;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author
 */
public class Utility {
    
    public static HouseDTO getHouse() {
        
	HouseDTO hdto = new HouseDTO(); 
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsDTO) {
            long houseId = ((UserDetailsDTO) principal).getHouseId();
            //String abbr = ((UserDetailsDTO) principal).getHouseAbbr();
            hdto.setId(houseId);
            //h.setAbbr(abbr);
        }
        else {
            throw new RuntimeException("Bad Request");
        }
        return hdto;
    }
}
