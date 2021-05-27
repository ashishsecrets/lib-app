package com.ucsf.auditModel;

import java.util.Date;
import javax.persistence.*;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ucsf.auth.model.User;

import lombok.Data;

@Entity
@Table(name = "user_history")
@EntityListeners(AuditingEntityListener.class)
@Data
public class UserHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_user_history_file"))
	private User user;
	
	@Lob
	@Column(name = "user_content")
	private String userContent;
	
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

	public UserHistory() {
	}

	public UserHistory(User user, Action action, String userContent) {
		this.user = user;
		this.userContent = userContent;
		this.action = action;
	}

	public UserHistory(User user, Action action, String userContent, String previousContent, String changedContent) {
		this.user = user;
		this.userContent = userContent;
		this.action = action;
		this.previousContent = previousContent;
		this.changedContent = changedContent;
	}

}
