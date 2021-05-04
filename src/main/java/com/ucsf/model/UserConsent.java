package com.ucsf.model;

import com.ucsf.auth.model.User;
import com.ucsf.model.ConsentForms.ConsentType;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "user_consent")
@Getter
@Setter
public class UserConsent extends Auditable<String> {
	
	public enum FormType {
		CONSENT, ASSENT
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "consent_id")
	private Long id;
		
	@Column(name = "parent_name")
	private String parentName;
	
	@Column(name = "adolescent_name")
	private String adolescentName;
	
	@Column(name = "patient_name")
	private String patientName;
	
	@Column(name = "user_id")
	private Long userId;

	@OneToOne(targetEntity = User.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;

	@Column(name = "consent_type")
	private ConsentType consentType;
	
	@Column(name = "type")
	private FormType type;
}
