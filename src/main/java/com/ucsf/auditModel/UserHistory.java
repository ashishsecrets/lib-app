package com.ucsf.auditModel;

import java.util.Date;
import javax.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ucsf.auth.model.User;

import lombok.Data;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
public class UserHistory {
	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_user_history_file"))
	private User user;
	
	@Lob
	@Column(name = "user_consent")
	private String userContent;

	@CreatedBy
	private String modifiedBy;

	@CreatedDate
	private Date modifiedDate;

	private Action action;

	public UserHistory() {
	}

	public UserHistory(User user, Action action) {
		this.user = user;
		this.userContent = user.toString();
		this.action = action;
	}

}