package com.dsp.plot.entity;


import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
@Table(name = "ministry_authority", schema = "master")
public class MinistryAuthority extends Auditable{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auth_min_seq")
	@SequenceGenerator(name = "auth_min_seq", sequenceName = "auth_min_seq", allocationSize = 1)
	private Long id;
	
	@Column(name = "ministry_id", nullable = false, length = 255)
    private Long ministryId;

    @Column(name = "ministry_code", nullable = false)
    private Integer ministryCode;

    @Column(name="authority_name",nullable = false, unique = true)
    private String authorityName;
    

}
