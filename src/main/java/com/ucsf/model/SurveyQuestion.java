package com.ucsf.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "survey_questions")
@NoArgsConstructor
@Getter
@Setter
public class SurveyQuestion extends Auditable<String> {

	public enum QuestionType {
		MULTIPLE_CHOICE, // given list of options
		RATING_SCALE, // given list of options
		DROPDOWN, // given list of options
		OPEN_ENDED, // User allows to answer into a input box.
		DICHOTOMOUS, // Question with two options "Yes/No"
		IMAGE_TYPE // user allows to click on images as their answer option to a question
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "question_id")
	private Long id;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column
	private boolean enabled;

	@Column(name = "question_type")
	private QuestionType questionType;

	@Column(columnDefinition = "TEXT")
	private String choice;

	@Column(name = "survey_id")
	private Long surveyId;

	@Column(name = "study_id")
	private Long studyId;

	@Column(name = "index_value")
	private int indexValue;

	@ManyToOne(targetEntity = UcsfStudy.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "study_id", insertable = false, updatable = false)
	@JsonIgnore
	private UcsfStudy study;

}
