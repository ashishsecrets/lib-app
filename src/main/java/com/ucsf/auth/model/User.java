package com.ucsf.auth.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ucsf.model.QualificationCriteria;
import com.ucsf.model.UserDiseaseInfo;
import com.ucsf.model.UserMetadata;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

	public enum UserStatus {
		PENDING, EMAIL_NOT_VERIFIED, ACTIVE, DEACTIVE, DELETED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column
	private String name;

	@Column
	private String username;

	@Column
	private String email;

	@Column
	@JsonIgnore
	private String password;
	
	@Column
	private String phoneNumber;
	
	@Column
	private String phoneCode;

	@Column(name = "user_status")
	private UserStatus userStatus;

	@Column(name = "auth_token", columnDefinition = "TEXT")
	private String authToken;

	private Date createdDate;

	private Date lastModifiedDate;

	@ManyToOne(targetEntity = UserMetadata.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "metadata_id")
	private UserMetadata metadata;

	@ManyToOne(targetEntity = UserDiseaseInfo.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "disease_info_id")
	private UserDiseaseInfo userDiseaseInfo;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "qualified_users", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "criteria_id"))
	Set<QualificationCriteria> criterias;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();
}
