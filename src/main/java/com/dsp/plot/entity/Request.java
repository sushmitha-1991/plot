package com.dsp.plot.entity;

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

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "plot_request",schema = "plot")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plotrequest_seq")
    @SequenceGenerator(name = "plotrequest_seq", sequenceName = "plotrequest_seq", allocationSize = 1)
    private Long id;
    @Column(name = "plot_input_id", nullable = false)
    private Long plotInputId;
    @Column(name = "request_data")
    private String request;

}
