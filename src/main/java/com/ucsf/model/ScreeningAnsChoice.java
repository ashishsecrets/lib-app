package com.ucsf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "screening_ans_choice")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ScreeningAnsChoice extends Auditable<String> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Lob
	private String choice;

	@Column(name = "question_id")
	private Long questionId;

	@Column(name = "study_id")
	private Long studyId;

	@JsonIgnore
	@ManyToOne(targetEntity = UcsfStudy.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "study_id", insertable = false, updatable = false)
	private UcsfStudy study;

	@JsonIgnore
	@ManyToOne(targetEntity = ScreeningQuestions.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "question_id", insertable = false, updatable = false)
	private ScreeningQuestions questions;
}