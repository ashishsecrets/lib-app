package com.ucsf.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ucsf_surveys")
@NoArgsConstructor
@Data
public class UcsfSurvey {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "survey_id")
	private Long id;

	@Column
	private String title;

	@Column
	private String description;

	@Column
	private boolean enabled;
	
	@Column(name = "study_id")
	private Long studyId;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "survey_id")
	private List<SurveyQuestion> surveyQuestions;

}
