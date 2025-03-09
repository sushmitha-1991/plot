package com.dsp.plot.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Table(name = "paper_laid_on_table", schema = "plot")
public class PaperLaidOnTable extends Auditable {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plot_laid_seq")
  @SequenceGenerator(name = "plot_laid_seq", sequenceName = "plot_laid_seq", allocationSize = 1)
  private Long id;

  @Column(name = "lok_sabha_no")
  private Long lokSabhaNo;
  @Column(name = "lok_sabha_session_no")
  private Long lokSabhaSessionNo;
  @Column(name = "rajya_sabha_session_no")
  private Long rajyaSabhaSessionNo;
  @Column(name = "plot_laid_date")
  private LocalDate plotLaidDate;
  @Column(name = "status", nullable = false)
  private String status;
  @Column(name = "ministry_id")
  private Long ministryId;
  @Column(name = "title", columnDefinition = "text", nullable = false)
  private String title;
  @Convert(converter = JsonMapConverter.class)
  @Column(name = "reference_docs", columnDefinition = "JSONB")
  private List<Map<String, Object>> documents;

  @ManyToOne
  @JoinColumn(name = "ministry_id", referencedColumnName = "id", insertable = false, updatable = false)
  private Ministries ministries;
  @ManyToOne
  @JoinColumn(name = "lok_sabha_session_no", referencedColumnName = "id", insertable = false, updatable = false)
  private MastersLokSabhaSession lokSabhSessionDetails;
  @ManyToOne
  @JoinColumn(name = "rajya_sabha_session_no", referencedColumnName = "id", insertable = false, updatable = false)
  private MastersRajyaSabha rajyaSabhaSessionDetails;
}
