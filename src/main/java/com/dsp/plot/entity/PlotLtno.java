package com.dsp.plot.entity;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.hibernate.envers.Audited;

import com.dsp.plot.entity.enums.Status;
import com.dsp.plot.util.JsonMapConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@Setter
@Getter
@Audited
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "plot_ltno", schema = "plot")
public class PlotLtno extends ApplicationInstance{
    // @Id
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plot_ltno_seq")
    // @SequenceGenerator(name = "plot_ltno_seq", sequenceName = "plot_ltno_seq", allocationSize = 1)
    // private Long id;

    @Column(name = "lok_sabha_no")
    private Long lokSabhaNo;
    @Column(name = "lok_sabha_session_no")
    private Long lokSabhaSessionNo;
    @Column(name = "rajya_sabha_session_no")
    private Long rajyaSabhaSessionNo;
    @Column(name = "paper_type_id")
    private Long paperTypeId;
    @Column(name = "ltno_date")
    private LocalDate ltnoDate;
    @Column(name = "paper_name")
    private String paperName;
    @Column(name = "ministry_id")
    private Long ministryId;
    @Column(name = "ltno_no")
    private String ltnoNo;
    @Column(name = "title",columnDefinition="text",nullable=false)
    private String title;
    @Convert(converter = JsonMapConverter.class)
    @Column(name = "documents", columnDefinition = "JSONB")
    private List<Map<String, Object>> documents;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "remarks")
    private String remarks;
    @Column(name = "ref_no")
    private String refNo;
    @ManyToOne
    @JoinColumn(name = "lok_sabha_session_no", referencedColumnName = "id",insertable=false,updatable=false)
    private MastersLokSabhaSession lokSabhSessionDetails;
    @ManyToOne
    @JoinColumn(name = "rajya_sabha_session_no", referencedColumnName = "id",insertable=false,updatable=false)
    private MastersRajyaSabha rajyaSabhaSessionDetails;
    @ManyToOne
    @JoinColumn(name = "ministry_id", referencedColumnName = "id",insertable=false,updatable=false)
    private Ministries ministries;
}
