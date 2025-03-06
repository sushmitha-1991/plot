package com.dsp.plot.dto;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuesBranchApiDtoRS {

    private String group;
	private List<String> ministries;
	// private List<Date> sittingDates;
    private List<String> sittingDates;
}
