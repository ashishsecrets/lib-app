package com.ucsf.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "consent_sections")
@Getter
@Setter
public class ConsentSection extends Auditable<String> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "consent_section_id")
	private Long id;

	@Column(name = "file_path")
	private String filePath;

	@Lob
	@Column(name = "content")
	private String content;
	
	@Column(name = "consent_form_id")
	private Long consentFormId;
	
	@Column(name = "section_number")
	private Integer sectionNumber;
}
