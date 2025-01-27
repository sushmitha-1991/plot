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
    @Table(name ="lok_sabha_masters",schema = "master")
    @Entity
    public class MastersLokSabha extends Auditable {
    
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lok_sabha_seq")
        @SequenceGenerator(name = "lok_sabha_seq", sequenceName = "lok_sabha_seq", allocationSize = 1)
        private Long id;

        @Column(name = "lok_sabha_number", nullable = false, length = 50)
        private String lokSabhaNumber;

    }
   
