package com.dsp.plot.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.dsp.plot.util.JsonMapConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Audited(targetAuditMode =RelationTargetAuditMode.NOT_AUDITED )
@Table(name = "plot", schema = "plot")
public class PlotGenerate extends ApplicationInstance {

    @Column(name = "lok_sabha_no")
    private Long lokSabhaNo;

    @Column(name = "lok_sabha_session_no")
    private Long lokSabhaSessionNo;

    @Column(name = "rajya_sabha_session_no")
    private Long rajyaSabhaSessionNo;

    @Column(name = "plot_date", nullable = false)
    private LocalDate plotDate;

    @Column(name = "plot_Time", nullable = false)
    private LocalTime plotTime;

    @Column(name = "approver_authority")
    private Long approverAuthority;

    @Column(name = "designation")
    private Long designation;
    @Column(name = "plot_parent_id")
    private Long plotParentId;
    @Column(name = "location")
    private String location;
    @Column(name = "draft_flag", columnDefinition = "boolean default false")
    private Boolean draftFlag;
    @Column(name = "status")
    private String status;
    @Column(name = "content", columnDefinition = "text")
    private String content;
    @Column(name = "foot_note", columnDefinition = "text")
    private String footNote;
    @Convert(converter = JsonMapConverter.class)
    @Column(name = "reference_files", columnDefinition="jsonb")
    private List<Map<String, Object>> referenceFiles;

    @Column(name = "paper_type", length = 255)
    private String paperType;
    @Column(name = "paper_name", length = 255)
    private String paperName;
    @Column(name = "remarks", length = 200)
    private String remarks;
    // @Column(name = "remarks", length = 200)
    // private String remarks;
     @ManyToOne
    @JoinColumn(name = "lok_sabha_session_no", referencedColumnName = "id",insertable=false,updatable=false)
    private MastersLokSabhaSession lokSabhSessionDetails;
    @ManyToOne
    @JoinColumn(name = "rajya_sabha_session_no", referencedColumnName = "id",insertable=false,updatable=false)
    private MastersRajyaSabha rajyaSabhaSessionDetails;
    @ManyToOne
    @JoinColumn(name = "approver_authority", referencedColumnName = "id",insertable=false,updatable=false)
    private BaseUser approverDetails;


}
