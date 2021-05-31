package com.ucsf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ucsf.auth.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "user_survey_status")
@Data
@NoArgsConstructor
public class UserSurveyStatus {

	public enum SurveyStatus {
		NEWLY_ADDED,INPROGRESS,UNDER_REVIEW, ENROLLED, AVAILABLE, DISQUALIFIED
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "survey_status_id")
	private Long id;

	@Column(name = "user_survey_status")
	private SurveyStatus userSurveyStatus;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "index_value")
	private int indexValue;

	@Column(name = "survey_id")
	private Long surveyId;

	@Column(name = "task_true_id")
	private Long taskTrueId;

	@ManyToOne(targetEntity = UcsfSurvey.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "survey_id", insertable = false, updatable = false)
	@JsonIgnore
	private UcsfSurvey survey;

	/*@ManyToOne(targetEntity = UcsfStudy.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "study_id", insertable = false, updatable = false)
	@JsonIgnore
	private UcsfStudy study;

	@Column(name = "study_id")
	private Long studyId;*/

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	@JsonIgnore
	private User users;

}
