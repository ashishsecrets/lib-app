package com.ucsf.model;


import com.ucsf.auth.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "user_rating")
@Getter
@Setter
public class UserRating extends Auditable<String> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rating_id")
	private Long id;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "study_id")
	private Long studyId;
	
	@Column(name = "survey_id")
	private Long surveyId;
	
	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;

	@ManyToOne(targetEntity = UcsfStudy.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "study_id", insertable = false, updatable = false)
	private UcsfStudy study;
	
	@ManyToOne(targetEntity = UcsfSurvey.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "survey_id", insertable = false, updatable = false)
	private UcsfSurvey survey;
    
}
