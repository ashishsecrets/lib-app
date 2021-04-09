package com.ucsf.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
public class ScreeningQuestions extends Auditable<String> {

	public enum QuestionTypes {
		MULTIPLE_CHOICE, // given list of options
		RATING_SCALE, // given list of options
		DROPDOWN, // given list of options
		OPEN_ENDED, // User allows to answer into a input box.
		DICHOTOMOUS, // Question with two options "Yes/No"
		IMAGE_TYPE,//user allows to click on images as their answer option to a question
		CHECKBOX
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

}
