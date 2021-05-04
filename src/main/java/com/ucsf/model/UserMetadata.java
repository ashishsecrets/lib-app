package com.ucsf.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.ucsf.auth.model.User;

import lombok.Data;

@Entity
@Table(name = "user_metadata")
@Data
public class UserMetadata extends Auditable<String> {

	public enum StudyAcceptanceNotification {
		NOT_APPROVED,NOTIFIED_BY_EMAIL,NOTIFIED_BY_SMS, NOTIFIED_BY_PUSH
	}

	public enum StudyStatus {
		NEWLY_ADDED,APPROVED,DISAPPROVED, ENROLLED,DISQUALIFIED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "metadata_id")
	private Long metadataId;

	@Column
	private String race;

	@Column
	private String age;

	@Column(name = "zip_code")
	private String zipCode;

	@Column(name = "is_consent_accepted")
	private boolean isConsentAccepted;

	@Column(name = "consent_acceptance_date")
	private Date consentAcceptanceDate;

	@Column(name = "notified_by")
	private StudyAcceptanceNotification notifiedBy;

	@Column(name = "study_status")
	private StudyStatus studyStatus;
	
	@Column(name = "user_id")
	private Long userId;
	
	@OneToOne(targetEntity = User.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "user_id",insertable = false,updatable = false)
	private User user;

}
