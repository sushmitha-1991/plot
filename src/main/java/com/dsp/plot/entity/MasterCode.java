package com.dsp.plot.entity;
import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
    
    @Entity
    @Data
    @Audited
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Table(name ="codes",schema = "master")
    public class MasterCode extends Auditable {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        @Column(name = "display_sequence")
        private Long displaySequence;
    
        @Column(name = "abbr", nullable = false)
        private String abbr;
    
        @Column(name = "code")
        private String code;
    
        @Column(name = "description")
        private String description;
    
    
    }
    
