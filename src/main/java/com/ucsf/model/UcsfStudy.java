package com.ucsf.model;

import java.util.Date;

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

	@Column
	private Boolean enabled;
	
	@Column
	private boolean isDefault;

	@Column(name = "start_date")
	private Date startDate;

	@Column(name = "end_date")
	private Date endDate;
	
	@Column(name = "frequency")
	private StudyFrequency frequency;
	
	@Column(name = "custom_date")
	private Date custom_date;
	
}
