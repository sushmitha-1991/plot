package com.dsp.plot.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuesBranchApiDto {

    private String group;
	private String ministriesDepartments;
	private String dateOfAnsweringQuestions;
}
