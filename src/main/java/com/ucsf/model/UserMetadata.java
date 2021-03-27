package com.ucsf.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_metadata")
public class UserMetadata extends Auditable<String> {
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

	@Column
	private String phone;
	
	@Column(name = "is_consent_accepted")
	private boolean isConsentAccepted;
	
	@Column(name = "consent_acceptance_date")
	private Date consentAcceptanceDate;
	
	public Long getMetadataId() {
		return metadataId;
	}

	public void setMetadataId(Long metadataId) {
		this.metadataId = metadataId;
	}

	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public boolean isConsentAccepted() {
		return isConsentAccepted;
	}

	public void setConsentAccepted(boolean isConsentAccepted) {
		this.isConsentAccepted = isConsentAccepted;
	}

	public Date getConsentAcceptanceDate() {
		return consentAcceptanceDate;
	}

	public void setConsentAcceptanceDate(Date consentAcceptanceDate) {
		this.consentAcceptanceDate = consentAcceptanceDate;
	}

}
