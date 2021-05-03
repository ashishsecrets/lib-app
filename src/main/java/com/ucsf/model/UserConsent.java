package com.ucsf.model;

import com.ucsf.auth.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "user_consent")
@Getter
@Setter
public class UserConsent extends Auditable<String> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "consent_id")
	private Long id;
	
	
	@Column(name = "parent_name")
	private String parentName;
	
	@Column(name = "adolescent_name")
	private String adolescentName;
	
	@Column(name = "user_id")
	private Long userId;

	@OneToOne(targetEntity = User.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;

}
