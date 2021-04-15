package com.ucsf.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	private String questionType;

	@Column(name = "index_value")
	private int indexValue;

	@Column(name = "survey_id")
	private Long surveyId;
	
	@ManyToOne(targetEntity = UcsfSurvey.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "survey_id", insertable = false, updatable = false)
	@JsonIgnore
	private UcsfSurvey survey;

}
