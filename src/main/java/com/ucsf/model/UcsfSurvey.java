package com.ucsf.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.ucsf.entityListener.TasksEntityListener;
import com.ucsf.entityListener.UcsfSurveyEntityListener;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "ucsf_survey")
@EntityListeners(UcsfSurveyEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
public class UcsfSurvey extends Auditable<String> implements Diffable<UcsfSurvey> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "survey_id")
	private Long id;

	@Column
	private String title;

	@Column
	private String description;

	@Column
	private Integer duration; //duration in weeks

	@Column
	private Boolean enabled;

	@Column(name = "study_id")
	private Long studyId;

	@ManyToOne(targetEntity = UcsfStudy.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "study_id", insertable = false, updatable = false)
	@JsonIgnore
	private UcsfStudy ucsfStudy;

	@Override
	public DiffResult diff(UcsfSurvey obj) {
		return new DiffBuilder(this, obj, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("title", this.title, obj.title)
				.append("description", this.description, obj.description)
				.append("duration", this.duration, obj.duration)
				.append("enabled", this.enabled, obj.enabled)
				.append("studyId", this.studyId, obj.studyId)
				.build();
	}
}
