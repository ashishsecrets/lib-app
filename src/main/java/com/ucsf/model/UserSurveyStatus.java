package com.ucsf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ucsf.auth.model.User;
import com.ucsf.entityListener.UserScreeningStatusEntityListener;
import com.ucsf.entityListener.UserSurveyStatusEntityListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

@Entity
@Table(name = "user_survey_status", uniqueConstraints=
@UniqueConstraint(columnNames={"task_true_id"}))
@EntityListeners(UserSurveyStatusEntityListener.class)
@Data
@NoArgsConstructor
public class UserSurveyStatus implements Diffable<UserSurveyStatus> {


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

	@Column(name = "task_true_id", unique = true)
	private Long taskTrueId;

	@Column(name = "max_index_value")
	private int maxIndexValue;

	@Column(name = "skip_count")
	private int skipCount;

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


	@Override
	public DiffResult<UserSurveyStatus> diff(UserSurveyStatus obj) {
		return new DiffBuilder(this, obj, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("userSurveyStatus", this.userSurveyStatus, obj.userSurveyStatus)
				.append("studyId", this.surveyId, obj.surveyId)
				.append("userId", this.userId, obj.userId)
				.append("indexValue", this.indexValue, obj.indexValue)
				.append("taskTrueId", this.taskTrueId, obj.taskTrueId)
				.append("maxIndexValue", this.maxIndexValue, obj.maxIndexValue)
				.append("skipCount", this.skipCount, obj.skipCount)
				.build();
	}


}
