package com.dsp.plot.entity;

import org.hibernate.envers.Audited;

import com.dsp.plot.entity.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@Audited
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "plot_index", schema = "plot")
public class PlotIndex extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plotindex_seq")
    @SequenceGenerator(name = "plotindex_seq", sequenceName = "plotindex_seq", allocationSize = 1)
    private Long id;

    @Column(name = "lok_sabha_no")
    private Long lokSabhaNo;

    @Column(name = "lok_sabha_session_no")
    private Long lokSabhaSessionNo;

    @Column(name = "rajya_sabha_session_no")
    private Long rajyaSabhaSessionNo;

    @Column(name = "draft_flag", columnDefinition = "boolean default false")
    private Boolean draftFlag;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "ref_no")
    private String refNo;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

}
