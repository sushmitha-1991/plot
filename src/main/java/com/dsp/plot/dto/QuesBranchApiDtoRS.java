package com.dsp.plot.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class QuesBranchApiDtoRS {

    private String group;
    private List<String> ministries;
    private List<String> sittingDates;
}
