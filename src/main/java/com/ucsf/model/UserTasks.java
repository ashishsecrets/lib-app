package com.ucsf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ucsf.auth.model.User;
import com.ucsf.entityListener.UserSurveyStatusEntityListener;
import com.ucsf.entityListener.UserTasksEntityListener;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user_tasks")
@NoArgsConstructor
@EntityListeners(UserTasksEntityListener.class)
@Getter
@Setter
public class UserTasks implements Diffable<UserTasks> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long taskTrueId;

	@Column
	private String title;

	@Column
	private String description;

	@Column(name = "task_id")
	private Long taskId;

	@Column(name = "task_type")
	private String taskType;

	@Column
	private Integer progress;

	@Column(name = "start_date")
	private Date startDate;

	@Column(name = "end_date")
	private Date endDate;

	@Column
	private Integer duration; //duration in weeks

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "study_id")
	private Long studyId;

	@Column(name = "week_count")
	private Integer weekCount;

	@ManyToOne(targetEntity = UcsfStudy.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "study_id", insertable = false, updatable = false)
	@JsonIgnore
	private UcsfStudy ucsfStudy;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	@JsonIgnore
	private User users;

	@Override
	public DiffResult<UserTasks> diff(UserTasks obj) {
		return new DiffBuilder(this, obj, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("title", this.title, obj.title)
				.append("description", this.description, obj.description)
				.append("taskId", this.taskId, obj.taskId)
				.append("taskType", this.taskType, obj.taskType)
				.append("progress", this.progress, obj.progress)
				.append("startDate", this.startDate, obj.startDate)
				.append("endDate", this.endDate, obj.endDate)
				.append("duration", this.duration, obj.duration)
				.append("userId", this.userId, obj.userId)
				.append("studyId", this.studyId, obj.studyId)
				.append("weekCount", this.weekCount, obj.weekCount)
				.build();
	}
}
