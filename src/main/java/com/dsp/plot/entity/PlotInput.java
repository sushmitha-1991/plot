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
@Table(name = "plot_input", schema = "plot")
public class PlotInput extends InputInstance {
    // @Id
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plotinput_seq")
    // @SequenceGenerator(name = "plotinput_seq", sequenceName = "plotinput_seq", allocationSize = 1)
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
    
    // @ManyToOne
    // @JoinColumn(name = "generated_plot_id", referencedColumnName = "id")
    // private PlotGenerate plotGenerate; 

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

    // @ManyToOne
    // @JoinColumn(name = "plot_id", referencedColumnName = "id")
    // private PlotGenerate plot;

   }
