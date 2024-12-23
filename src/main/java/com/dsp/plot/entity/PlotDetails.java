// package com.dsp.plot.entity;

// import java.time.LocalDate;
// import java.util.List;
// import java.util.Map;

// import org.hibernate.envers.Audited;

// import com.dsp.plot.util.JsonMapConverter;

// import jakarta.persistence.Column;
// import jakarta.persistence.Convert;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.SequenceGenerator;
// import jakarta.persistence.Table;
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.experimental.SuperBuilder;

// @Entity
// @Data
// @Audited
// @NoArgsConstructor
// @AllArgsConstructor
// @SuperBuilder
// @Table(name = "plot_details", schema = "plot")
// public class PlotDetails extends PlotInput 
// {
// @Id
// @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plotinput_seq")
// @SequenceGenerator(name = "plotreport_seq", sequenceName = "plotreport_seq", allocationSize = 1)
// private Long id;

// @Column(name = "title", nullable = false, columnDefinition = "text")
// private String title;

// @Column(name = "article", nullable = false, columnDefinition = "text")
// private String article;

// @Column(name = "section",  columnDefinition = "text")
// private String section;

// @Column(name = "constitutional_body",  columnDefinition = "text")
// private String constitutionalBody;

// @Column(name = "head_quarters",  columnDefinition = "text")
// private String headQuaters;

// @Column(name = "year")
// private Long year;

// @Convert(converter = JsonMapConverter.class)
// @Column(name = "documents", columnDefinition = "JSONB")
// private List<Map<String, Object>> documents;

// @Column(name = "report_number")
// private Long reportNumber;

// @Column(name = "user_name")
// private String nameOfUser;

// @Column(name = "report_type")
// private Integer reportType;

// @Column(name = "preset_in_rs")
// private Boolean presentInRS;
// @Column(name="date_RS")
// private LocalDate dateOfRS;
// @Column(name = "informed_date")
// private LocalDate informedDate;
// }
