package com.ucsf.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "consent_forms")
@Getter
@Setter
public class ConsentForms extends Auditable<String> {

	public enum ConsentType {
		ASSENT_FORM, CONSENT_FORM_FOR_BELOW_18, CONSENT_FORM_FOR_ABOVE_18
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "consent_form_id")
	private Long id;

	@Column(name = "file_path")
	private String filePath;

	@Column(name = "consent_type")
	private ConsentType consentType;

	@Lob
	@Column(name = "content")
	private String content;

	@OneToMany(targetEntity = ConsentSection.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "consent_form_id", insertable = false, updatable = false)
	private List<ConsentSection> sections;

}
