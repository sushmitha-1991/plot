package com.dsp.plot.entity;

import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Audited
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "master")
public class Departments extends Auditable {
    @Id
    private Long id;
    @Column(name = "ministry_id")
    private Long ministryId; // Assuming Ministries is the related entity for 'ministry_id'
    @Column(name = "department_name")
    private String departmentName;
    @Column(name = "department_code")
    private Integer departmentCode;
    @Column(name = "abbr")
    private String abbr;
}
