package com.ucsf.payload.response;

import java.util.Date;

import com.ucsf.auth.model.User.UserStatus;
import com.ucsf.model.Auditable;
import com.ucsf.model.UcsfStudy.StudyFrequency;
import com.ucsf.model.UserMetadata.StudyAcceptanceNotification;
import com.ucsf.model.UserMetadata.StudyStatus;
import com.ucsf.model.UserScreeningStatus.UserScreenStatus;

import lombok.Data;

@Data
public class UserDataResponse extends Auditable<String> {

	private Long userId;

	private String firstName;
	
	private String lastName;

	private String email;

	private String phoneNumber;

	private String phoneCode;
	
	private UserStatus userStatus;

	private String authToken;

	private String devideId;

	private Long metadataId;

	private String race;

	private Long age;

	private String zipCode;
	
	private String dateOfBirth;

	private boolean isConsentAccepted;

	private Date consentAcceptanceDate;

	private StudyAcceptanceNotification notifiedBy;

	//private StudyStatus studyStatus;
	
	private UserScreenStatus userScreeningStatus;

	private Long studyId;

	private int indexValue;
	
	private String title;

	private String description;

	private Boolean enabled;
	
	private boolean isDefault;

	private Date startDate;

	private Date endDate;
	
	private StudyFrequency frequency;
	
	private Date custom_date;
}
