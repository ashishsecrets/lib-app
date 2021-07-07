package com.ucsf.model;

import java.util.Date;

import javax.persistence.*;

import com.ucsf.auth.model.User;

import com.ucsf.entityListener.TasksEntityListener;
import com.ucsf.entityListener.UserMetadataEntityListener;
import lombok.Data;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "user_metadata")
@EntityListeners(UserMetadataEntityListener.class)
@Data
public class UserMetadata extends Auditable<String> implements Diffable<UserMetadata> {

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
	private Long age;

	@Column(name = "zip_code")
	private String zipCode;
	
	@Column(name = "date_of_birth")
	private String dateOfBith;

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

	@Override
	public DiffResult diff(UserMetadata obj) {
		return new DiffBuilder(this, obj, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("race", this.race, obj.race)
				.append("age", this.age, obj.age)
				.append("zipCode", this.zipCode, obj.zipCode)
				.append("dateOfBith", this.dateOfBith, obj.dateOfBith)
				.append("isConsentAccepted", this.isConsentAccepted, obj.isConsentAccepted)
				.append("consentAcceptanceDate", this.consentAcceptanceDate, obj.consentAcceptanceDate)
				.append("isConsentAccepted", this.isConsentAccepted, obj.isConsentAccepted)
				.build();

	}
}
