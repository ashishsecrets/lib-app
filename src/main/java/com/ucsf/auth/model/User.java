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
	private String firstName;
	
	@Column
	private String lastName;

	@Column
	private String email;

	@Column
	@JsonIgnore
	private String password;

	@Column
	private String phoneNumber;

	@Column
	private Boolean isVerified;

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

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", username=" + username + ", email=" + email + ", password="
				+ password + ", phoneNumber=" + phoneNumber + ", isVerified=" + isVerified + ", phoneCode=" + phoneCode
				+ ", userStatus=" + userStatus + ", authToken=" + authToken + ", createdDate=" + createdDate
				+ ", lastModifiedDate=" + lastModifiedDate + ", metadata=" + metadata + ", userDiseaseInfo="
				+ userDiseaseInfo + ", criterias=" + criterias + ", roles=" + roles + "]";
	}
	
	
}
