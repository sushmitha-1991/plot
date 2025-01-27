package com.dsp.plot.entity;
import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
    
    @Entity
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Audited
    @Builder
    @Table(name ="offices",schema = "master")
public class MastersBranch  extends Auditable
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "branch_seq")
    @SequenceGenerator(name = "branch_seq", sequenceName = "branch_seq", allocationSize = 1)
    private Long id;

    @Column(name = "office_name", nullable = false, length = 255)
    private String officeName;

    @Column(name = "office_code", nullable = false, length = 50)
    private String officeCode;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "house_id", nullable = false)
    private Long houseId;
  
}
