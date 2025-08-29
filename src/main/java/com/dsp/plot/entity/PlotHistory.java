package com.dsp.plot.entity;

import com.dsp.plot.config.XSSConfig.AllowHtml;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plot_history", schema = "plot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PlotHistory extends Auditable {

      @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plot_history_seq")
    @SequenceGenerator(name = "plot_history_seq", sequenceName = "plot.plot_history_seq", allocationSize = 1)
    private Long id;


    @Column(name = "plot_input_id", nullable = false)
    private Long plotInputId;

    @Column(name = "ministry_id")
    private Long ministryId;
    
    @Column(name = "status")
    private String status;

    @AllowHtml
    @Column(name = "added_text", columnDefinition = "TEXT")
    private String addedText;
}
