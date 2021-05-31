package com.ucsf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ucsf.auth.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tasks")
@NoArgsConstructor
@Getter
@Setter
public class Tasks {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;

	@Column
	private String title;

	@Column
	private String description;

	@Column
	private Integer duration; //duration in weeks

	@Column(name = "task_type")
	private String taskType;

	@Column(name = "study_id")
	private Long studyId;

	@ManyToOne(targetEntity = UcsfStudy.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "study_id", insertable = false, updatable = false)
	@JsonIgnore
	private UcsfStudy ucsfStudy;


}
