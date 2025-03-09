package com.dsp.plot.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.hibernate.envers.Audited;

import com.dsp.plot.util.JsonMapConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "plot_input", schema = "plot")
public class PlotInput extends InputInstance {
    // @Id
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
    // "plotinput_seq")
    // @SequenceGenerator(name = "plotinput_seq", sequenceName = "plotinput_seq",
    // allocationSize = 1)
    // private Long id;

    @Column(name = "lok_sabha_no")
    private Long lokSabhaNo;

    @Column(name = "lok_sabha_session_no")
    private Long lokSabhaSessionNo;

    @Column(name = "rajya_sabha_session_no")
    private Long rajyaSabhaSessionNo;

    @Column(name = "submission_date", nullable = false)
    private LocalDate submissionDate;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "authenticated_by_id")
    private Long authenticatedById;

    @Column(name = "paper_type_id")
    private Long paperTypeId;

    @Column(name = "paper_name")
    private String paperName;

    @Column(name = "ministry_id")
    private Long ministryId;

    @Column(name = "generated_plot_id")
    private Long generatedPlotId;
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Convert(converter = JsonMapConverter.class)
    @Column(name = "paper_details", columnDefinition = "JSONB")
    private List<Map<String, Object>> paperDetails;

    @Convert(converter = JsonMapConverter.class)
    @Column(name = "documents", columnDefinition = "JSONB")
    private List<Map<String, Object>> documents;

    @Column(name = "digitally_signed")
    private Boolean digitallySigned;

    @Column(name = "act_id")
    private Long actId;

    @Column(name = "draft_flag", columnDefinition = "boolean default false")
    private Boolean draftFlag;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "ref_no")
    private String refNo;

    @Column(name = "proposed_date_of_laying")
    private LocalDate proposedDateOfLaying;
    @Column(name = "actual_date_of_laying")
    private LocalDate actualDateOfLaying;

    @Column(name = "prev_paper_laid_date")
    private LocalDate prevPaperLaidDate;

    @Column(name = "nodal_officer_id")
    private Long nodalOfficerId;

    @Column(name = "nodal_officer_designation", length = 30)
    private String nodalOfficerDesignation;

    @Column(name = "nodal_officer_phone_no", length = 13)
    private String nodalOfficerPhoneNo;

    @Column(name = "nodal_officer_mobile_no", length = 13)
    private String nodalOfficerMobileNo;
    @ManyToOne
    @JoinColumn(name = "generated_plot_id", referencedColumnName = "id", insertable = false, updatable = false)
    private PlotGenerate plotGenerate;
    @ManyToOne
    @JoinColumn(name = "authenticated_by_id", referencedColumnName = "id", insertable = false, updatable = false)
    private BaseUser authenticatorDetails;
    @ManyToOne
    @JoinColumn(name = "nodal_officer_id", referencedColumnName = "id", insertable = false, updatable = false)
    private BaseUser nodalOfficerDetails;
    @ManyToOne
    @JoinColumn(name = "lok_sabha_no", referencedColumnName = "id", insertable = false, updatable = false)
    private MastersLokSabha lokSabhaDetails;
    @ManyToOne
    @JoinColumn(name = "lok_sabha_session_no", referencedColumnName = "id", insertable = false, updatable = false)
    private MastersLokSabhaSession lokSabhaSessionDetails;
    @ManyToOne
    @JoinColumn(name = "rajya_sabha_session_no", referencedColumnName = "id", insertable = false, updatable = false)
    private MastersRajyaSabha rajyaSabhaSessionDetails;
    @ManyToOne
    @JoinColumn(name = "ministry_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Ministries ministries;
    @ManyToOne
    @JoinColumn(name = "paper_type_id", referencedColumnName = "id", insertable = false, updatable = false)
    private MasterCode paperTypes;
    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Departments departments;

}
