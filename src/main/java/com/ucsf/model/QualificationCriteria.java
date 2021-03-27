package com.ucsf.model;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "qualification_criteria")
public class QualificationCriteria {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "criteria_id")
	private Long id;

	@Column(columnDefinition = "TEXT")
	private String title;
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	@OneToMany(fetch= FetchType.LAZY)
	@JoinColumn(name = "criteria_id")
    private List<Question> criteriaQuestions;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name = "qualified_studies", joinColumns = @JoinColumn(name = "criteria_id"), inverseJoinColumns = @JoinColumn(name = "study_id"))
	Set<UcsfStudy> studies;
	
	@Column(name = "study_id")
	private Long studyId;


}
