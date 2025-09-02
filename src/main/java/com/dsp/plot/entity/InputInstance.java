package com.dsp.plot.entity;

import java.time.LocalDate;

import org.hibernate.envers.Audited;

import com.dsp.plot.config.XSSConfig.AllowHtml;
import com.dsp.plot.config.XSSConfig.NoEscape;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
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
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "input_instance",schema = "master")
@Inheritance(strategy = InheritanceType.JOINED)
public class InputInstance  extends Auditable{

    @Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "input_instance_seq")
	@SequenceGenerator(name = "input_instance_seq", sequenceName = "master.input_instance_seq", allocationSize = 1)
	private Long id;

    @Column(name = "application_no", nullable = false, length = 255)
    private String applicationNo;

    @Column(name = "current_status", nullable = false, length = 100)
    private String currentStatus;

    @Column(name = "process_instance_id", length = 255)
    private String processInstanceId;

    @NoEscape
    @Column(name = "published_document_url", length = 255)
    private String publishedDocumentUrl;

    @Column(name = "ref_id")
    private Long refId;

    @Column(name = "application_id", nullable = false)
    private Long applicationId;

    @Column(name = "task_id", length = 255)
    private String taskId;

    @Column(name = "is_published")
    private Boolean isPublished;

    @Column(name = "lok_sabha_session", length = 30)
    private String lokSabhaSession;

    @Column(name = "lok_sabha_session_number", length = 30)
    private String lokSabhaSessionNumber;

    @Column(name = "other_info", columnDefinition = "jsonb")
    private String otherInfo;

    @Column(name = "rajya_sabha_session", length = 30)
    private String rajyaSabhaSession;

    @Column(name = "additional_details", columnDefinition = "text")
    private String additionalDetails;

    @Column(name = "draft", columnDefinition = "boolean default true")
    private Boolean draft;

    @Column(name = "document_date")
    private LocalDate documentDate;

    @Column(name = "pending_at",length = 50)
    private String pendingAt;

    @Column(name = "application_instance_id")
    private Long applicationInstanceId;
    
    @AllowHtml
    @Column(name = "html_content_en", columnDefinition = "text")
	private String htmlContentEnglish;
	
    @AllowHtml
    @Column(name = "html_content_hi", columnDefinition = "text")
	private String htmlContentHindi;

}
