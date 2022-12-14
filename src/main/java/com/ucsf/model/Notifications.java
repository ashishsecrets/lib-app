package com.ucsf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ucsf.auth.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
public class Notifications {

	/*:userId,type,date on which notification triggered,Description*/
	public enum NotificationType {
		EMAIL, SMS, PUSH
	}
	public enum NotificationKind {
		SCREENING, TASK, PHOTOS, AUTHENTICATE, APPROVEDINSTUDY, DISAPPROVEDINSTUDY
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "type")
	private NotificationType type;

	@Column(name = "kind")
	private NotificationKind kind;

	@Column(name = "kind_description")
	private String kindDescription;
	
	@Column(name = "sent_to")
	private String sentTO;
	
	@Column(name = "is_read")
	private Boolean isRead;

	@Column(name = "date")
	private Date date;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "user_id")
	private Long userId;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	@JsonIgnore
	private User users;

}
