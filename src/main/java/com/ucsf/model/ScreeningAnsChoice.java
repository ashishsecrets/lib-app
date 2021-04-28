package com.ucsf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "screening_ans_choice")
@Getter
@Setter
public class ScreeningAnsChoice extends Auditable<String> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Lob
	private String choice;

	@Column(name = "question_id")
	private Long questionId;

	@ManyToOne(targetEntity = ScreeningQuestions.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "question_id", insertable = false, updatable = false)
	@JsonIgnore
	private ScreeningQuestions questions;
}