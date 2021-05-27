package com.ucsf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ucsf.auth.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Primary;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "screening_answers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
public class ScreeningAnswers extends Auditable<String> implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "screening_answer_id")
	private Long id;

	@Column(name = "answer_description", columnDefinition = "TEXT")
	private String answerDescription;
	
	@Column(name = "answer_choice")
	private String answerChoice;

	@Column(name = "answered_by_id")
	private Long answeredById;

	@Column(name = "question_id")
	private Long questionId;
	
	@Column(name = "study_id")
	private Long studyId;


	@Column(name = "index_value")
	private int indexValue;

	@JsonIgnore
	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "answered_by_id", insertable = false, updatable = false)
	private User answeredBy;

	@JsonIgnore
	@OneToOne(targetEntity = ScreeningQuestions.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "question_id", insertable = false, updatable = false)
	private ScreeningQuestions question;

	@JsonIgnore
	@ManyToOne(targetEntity = UcsfStudy.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "study_id", insertable = false, updatable = false)
	private UcsfStudy study;

	@Override
	public String toString() {
		return "ScreeningAnswers [id=" + id + ", answerDescription=" + answerDescription + ", answerChoice="
				+ answerChoice + ", answeredById=" + answeredById + ", questionId=" + questionId + ", studyId="
				+ studyId + ", indexValue=" + indexValue + ", answeredBy=" + answeredBy + ", question=" + question
				+ ", study=" + study + ", getId()=" + getId() + ", getAnswerDescription()=" + getAnswerDescription()
				+ ", getAnswerChoice()=" + getAnswerChoice() + ", getAnsweredById()=" + getAnsweredById()
				+ ", getQuestionId()=" + getQuestionId() + ", getStudyId()=" + getStudyId() + ", getIndexValue()="
				+ getIndexValue() + ", getAnsweredBy()=" + getAnsweredBy() + ", getQuestion()=" + getQuestion()
				+ ", getStudy()=" + getStudy() + ", getCreatedBy()=" + getCreatedBy() + ", getCreatedDate()="
				+ getCreatedDate() + ", getLastModifiedBy()=" + getLastModifiedBy() + ", getLastModifiedDate()="
				+ getLastModifiedDate() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ ", getClass()=" + getClass() + "]";
	}
    
	
	
}
