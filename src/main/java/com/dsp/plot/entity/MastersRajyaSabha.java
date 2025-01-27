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
    @Table(name ="rajya_sabha_session_master",schema = "master")
    @Entity
    public class MastersRajyaSabha extends Auditable {
    
        @Id
        private Long id;
        @Column(name = "session_number")
        private String sessionNumber;


    }
   

