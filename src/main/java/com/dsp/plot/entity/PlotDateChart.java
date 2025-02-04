package com.dsp.plot.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.hibernate.envers.Audited;

import com.dsp.plot.util.JsonMapConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "plot_date_chart", schema = "plot")
public class PlotDateChart extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plot_chart_id_seq")
    @SequenceGenerator(name = "plot_chart_id_seq", sequenceName = "plot.plot_chart_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "lok_sabha_no")
    private Long lokSabhaNo;

    @Column(name = "lok_sabha_session_no")
    private Long lokSabhaSessionNo;

    @Column(name = "rajya_sabha_session_no")
    private Long rajyaSabhaSessionNo;

    @Column(name = "ministry_id")
    private Long ministryId;

    @Column(name = "group_name")
    private String groupName;

// @Column(name = "proposed_date_of_laying")
// private List<LocalDate> proposedDateOfLyingList;
// @Column(name = "date_of_submission_of_papers")
// private List<LocalDate> dateOfSubmissionOfPapers;
// @Column(name = "actual_date_of_lying")
// private List<LocalDate> actualDateOfLying;

    @Column(name = "input_id")
    private Long inputId;

    @Convert(converter = JsonMapConverter.class)
    @Column(name = "dates", columnDefinition = "JSONB")
    private List<Map<String, Object>> dates;
    @ManyToOne
    @JoinColumn(name = "lok_sabha_session_no", referencedColumnName = "id", insertable = false, updatable = false)
    private MastersLokSabhaSession lokSabhSessionDetails;
    @ManyToOne
    @JoinColumn(name = "rajya_sabha_session_no", referencedColumnName = "id", insertable = false, updatable = false)
    private MastersRajyaSabha rajyaSabhaSessionDetails;
    @ManyToOne
    @JoinColumn(name = "ministry_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Ministries ministries;

}
