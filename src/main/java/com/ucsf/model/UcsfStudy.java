package com.ucsf.model;

import java.util.Date;
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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "ucsf_studies")
@NoArgsConstructor
@Getter
@Setter
public class UcsfStudy {
	public enum StudyFrequency {
		DAILY, WEEKLY, SEMI_MONTHLY, MONTHLY, QUARTERLY, YEARLY, DAY_OF_MONTH, CUSTOM_DATE
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "study_id")
	private Long id;

	@Column
	private String title;

	@Column
	private String description;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "study_id")
	private List<UcsfSurvey> surveys;

	@Column
	private boolean enabled;

	@Column(name = "start_date")
	private Date startDate;

	@Column(name = "end_date")
	private Date endDate;
	
	@Column(name = "frequency")
	private StudyFrequency frequency;
	
	@Column(name = "custom_date")
	private Date custom_date;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name = "qualified_studies", joinColumns = @JoinColumn(name = "study_id"), inverseJoinColumns = @JoinColumn(name = "criteria_id"))
	Set<QualificationCriteria> criterias;

}
