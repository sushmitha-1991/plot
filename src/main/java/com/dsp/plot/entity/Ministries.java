package com.dsp.plot.entity;

import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Audited
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "master")
public class Ministries {

    @Id
    private Long id;
    @Column(name = "ministry_name")
    private String ministryName;
}
