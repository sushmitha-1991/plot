package com.dsp.plot.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class DepartmentDTO {
    private String departmentName;
    private Integer departmentCode;
    private String abbr;
}
