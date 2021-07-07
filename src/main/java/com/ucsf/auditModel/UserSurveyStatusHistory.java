package com.ucsf.auditModel;

import com.ucsf.model.UserScreeningStatus;
import com.ucsf.model.UserSurveyStatus;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_survey_status_history")
@EntityListeners(AuditingEntityListener.class)
@Data
public class UserSurveyStatusHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "survey_status_id", foreignKey = @ForeignKey(name = "FK_user_survey_status_history_file"))
	private UserSurveyStatus userSurveyStatus;

	@Lob
	@Column(name = "user_survey_status_content")
	private String userSurveyStatusContent;

	@Lob
	@Column(name = "previous_content")
	private String previousContent;

	@Lob
	@Column(name = "changed_content")
	private String changedContent;

	@CreatedBy
	private String modifiedBy;

	@CreatedDate
	private Date modifiedDate;

	private Action action;

	public UserSurveyStatusHistory() {
	}

	public UserSurveyStatusHistory(UserSurveyStatus userSurveyStatus, Action action, String userSurveyStatusContent) {
		this.userSurveyStatus = userSurveyStatus;
		this.userSurveyStatusContent = userSurveyStatusContent;
		this.action = action;
	}

	public UserSurveyStatusHistory(UserSurveyStatus userSurveyStatus, Action action, String userSurveyStatusContent, String previousContent, String changedContent) {
		this.userSurveyStatus = userSurveyStatus;
		this.userSurveyStatusContent = userSurveyStatusContent;
		this.action = action;
		this.previousContent = previousContent;
		this.changedContent = changedContent;
	}

}
