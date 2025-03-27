package com.dsp.plot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinistryAuthorityDTO {
    private Long id;
	
    private Long ministryId;

    private Integer ministryCode;

    private String authorityName;
}
