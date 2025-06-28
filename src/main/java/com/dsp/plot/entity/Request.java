package com.dsp.plot.entity;

import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Audited
@Entity
@Table(name = "plot_request", schema = "plot")
public class Request extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plotrequest_seq")
    @SequenceGenerator(name = "plotrequest_seq", sequenceName = "plotrequest_seq", allocationSize = 1)
    private Long id;
    @Column(name = "plot_input_id", nullable = false)
    private Long plotInputId;
    @Column(name = "request_data")
    private String request;
    @Column(name = "user_name")
    private String userName;
    @Column(name="is_already_returned")
    private Boolean isAlreadyReturned;

    @ManyToOne
    @JoinColumn(name = "plot_input_id", referencedColumnName = "id",updatable=false,insertable=false)
    private PlotInput plotInput;

}
