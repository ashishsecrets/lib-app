package com.ucsf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ucsf.auth.model.User;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

@Entity
@Table(name = "survey_answers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
public class SurveyAnswer extends Auditable<String> implements Diffable<SurveyAnswer> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "survey_answer_id")
	private Long id;

	@Column(name = "answer_description", columnDefinition = "TEXT")
	private String answerDescription;
	
	@Column(name = "answer_choice")
	private String answerChoice;

	@Column(name = "answered_by_id")
	private Long answeredById;

	@Column(name = "question_id")
	private Long questionId;
	
	@Column(name = "survey_id")
	private Long surveyId;

	@Column(name = "task_true_id")
	private Long taskTrueId;
	
	@Column(name = "index_value")
	private int indexValue;

	@JsonIgnore
	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "answered_by_id", insertable = false, updatable = false)
	private User answeredBy;

	@JsonIgnore
	@ManyToOne(targetEntity = SurveyQuestion.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "question_id", insertable = false, updatable = false)
	private SurveyQuestion question;

	@JsonIgnore
	@ManyToOne(targetEntity = UcsfSurvey.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "survey_id", insertable = false, updatable = false)
	private UcsfSurvey survey;

	@Override
	public DiffResult<SurveyAnswer> diff(SurveyAnswer obj) {
		return new DiffBuilder(this, obj, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("answerDescription", this.answerDescription, obj.answerDescription)
				.append("answerChoice", this.answerChoice, obj.answerChoice)
				.append("answeredById", this.answeredById, obj.answeredById)
				.append("questionId", this.questionId, obj.questionId)
				.append("surveyId", this.surveyId, obj.surveyId)
				.append("indexValue", this.indexValue, obj.indexValue)
				.append("taskTrueId", this.taskTrueId, obj.taskTrueId)
				.build();
	}
}
