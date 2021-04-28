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
import com.ucsf.auth.model.User;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_screening_status")
@Data
@NoArgsConstructor
public class UserScreeningStatus {

	public enum UserScreenStatus {
		NEWLY_ADDED,INPROGRESS,UNDER_REVIEW, ENROLLED, AVAILABLE
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "status_id")
	private Long id;

	@Column(name = "user_screening_status")
	private UserScreenStatus userScreeningStatus;

	@ManyToOne(targetEntity = UcsfStudy.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "study_id", insertable = false, updatable = false)
	@JsonIgnore
	private UcsfStudy study;

	@Column(name = "study_id")
	private Long studyId;

	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "index_value")
	private int indexValue;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	@JsonIgnore
	private User users;

}
