package com.ucsf.auditModel;

import com.ucsf.entityListener.UserScreeningStatusEntityListener;
import com.ucsf.model.ScreeningAnswers;
import com.ucsf.model.UserScreeningStatus;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_screening_status_history")
@EntityListeners(AuditingEntityListener.class)
@Data
public class UserScreeningStatusHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "status_id", foreignKey = @ForeignKey(name = "FK_user_screening_status_history_file"))
	private UserScreeningStatus userScreeningStatus;

	@Lob
	@Column(name = "user_screening_status_content")
	private String userScreeningStatusContent;

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

	public UserScreeningStatusHistory() {
	}

	public UserScreeningStatusHistory(UserScreeningStatus userScreeningStatus, Action action, String userScreeningStatusContent) {
		this.userScreeningStatus = userScreeningStatus;
		this.userScreeningStatusContent = userScreeningStatusContent;
		this.action = action;
	}

	public UserScreeningStatusHistory(UserScreeningStatus userScreeningStatus, Action action, String userScreeningStatusContent, String previousContent, String changedContent) {
		this.userScreeningStatus = userScreeningStatus;
		this.userScreeningStatusContent = userScreeningStatusContent;
		this.action = action;
		this.previousContent = previousContent;
		this.changedContent = changedContent;
	}

}
