package com.ucsf.auditModel;

import com.ucsf.model.UserSurveyStatus;
import com.ucsf.model.UserTasks;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_tasks_history")
@EntityListeners(AuditingEntityListener.class)
@Data
public class UserTasksHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id", foreignKey = @ForeignKey(name = "FK_user_tasks_history_file"))
	private UserTasks userTasks;

	@Lob
	@Column(name = "user_tasks_content")
	private String userTasksContent;

	@Lob
	@Column(name = "previous_content")
	private String previousContent;

	@Lob
	@Column(name = "changed_content")
	private String changedContent;

	@CreatedBy
	private String modifiedBy;

	@CreatedDate
	private Date modifiedDate;

	private Action action;

	public UserTasksHistory() {
	}

	public UserTasksHistory(UserTasks userTasks, Action action, String userTasksContent) {
		this.userTasks = userTasks;
		this.userTasksContent = userTasksContent;
		this.action = action;
	}

	public UserTasksHistory(UserTasks userTasks, Action action, String userTasksContent, String previousContent, String changedContent) {
		this.userTasks = userTasks;
		this.userTasksContent = userTasksContent;
		this.action = action;
		this.previousContent = previousContent;
		this.changedContent = changedContent;
	}

}
