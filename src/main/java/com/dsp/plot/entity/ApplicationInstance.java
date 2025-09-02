package com.dsp.plot.entity;

import java.time.LocalDate;
import java.util.Map;

import org.hibernate.envers.Audited;

import com.dsp.plot.config.XSSConfig.AllowHtml;
import com.dsp.plot.util.MapJsonConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
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
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Setter
@Getter
@Audited
@Table(name = "application_instance", schema = "master")
@Inheritance(strategy = InheritanceType.JOINED)
public class ApplicationInstance extends Auditable{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_instance_seq")
	@SequenceGenerator(name = "application_instance_seq", sequenceName = "master.application_instance_seq", allocationSize = 1)
	private Long id;

	@JoinColumn(name = "application_id", nullable = true)
	private Long applicationId;

	@Column(name = "applicationNo", nullable = true, length = 255)
	private String applicationNo;

	@Column(name = "ref_Id")
	private Long refId;

	@Column(name = "currentStatus", nullable = true, length = 100)
	private String currentStatus;

	@Column(name = "published_documentUrl", length = 255)
	private String publishedDocumentUrl;

	@Column(name = "process_Instance_Id", length = 255)
	private String processInstanceId;

	@Column(name = "is_published")
	private Boolean isPublished;

	@Column(name = "lok_sabha_session", length = 30)
	private String lokSabhaSession;

	@Column(name = "lok_sabha_session_number", length = 30)
	private String lokSabhaSessionNumber;

	@Column(name = "rajya_sabha_session", length = 30)
	private String rajyaSabhaSession;

	@Column(name = "document_date")
	private LocalDate documentDate;

	@Convert(converter = MapJsonConverter.class)
	@Column(name = "other_info", columnDefinition = "jsonb")
	private Map<String, Object> otherInfo;

	@AllowHtml
	@Column(name = "html_content_en", columnDefinition = "text")
	private String htmlContentEnglish;
	
	@AllowHtml
	@Column(name = "html_content_hi", columnDefinition = "text")
	private String htmlContentHindi;
	@Column(name = "parent_instance_id")
	private Long parentInstanceId;
	@Column(name = "starting_page_no")
    private Long startingPageNo;
	

}
