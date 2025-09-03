/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dsp.plot.entity;


import com.dsp.plot.util.JsonMapConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "input_workflow_history", schema = "workflow")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InputWorkflowHistory extends Auditable { 

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "input_instance_id", nullable = false)
	private Long inputInstanceId;
	
	@Column(name = "application_id", nullable = false)
	private Long applicationId;

	@Column(name = "application_record_id", nullable = false)
	private Long applicationRecordId;

	@Column(name = "action_taken")
	private String actionTaken;

	@Column(name = "status")
	private String status;

	@Column(name = "action_taken_by")
	private Long actionTakenBy;

	@Column(name = "action_taken_by_name")
	private String actionTakenByName;

	@Column(name = "action_taken_on")
	private LocalDateTime actionTakenOn;

	@Column(name = "pending_with")
	private Long pendingWith;

	@Column(name = "pending_with_name")
	private String pendingWithName;

	@Column(name = "comment_for_display")
	private String commentForDisplay;

	@Column(name = "note", columnDefinition = "TEXT")
	private String note;

	@Column(name = "ip_address") 
	private String ipAddress;
	
	@Convert(converter = JsonMapConverter.class)
    @Column(name = "reference_files", columnDefinition = "JSONB")
    private List<Map<String,Object>> referenceFiles;	

}
