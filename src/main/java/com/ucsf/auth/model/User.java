package com.ucsf.auth.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

	public enum UserStatus {
		PENDING, EMAIL_NOT_VERIFIED, ACTIVE, DEACTIVE, DELETED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;

	private String email;

	@JsonIgnore
	private String password;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "phone_code")
	private String phoneCode;
	
	@Column(name = "user_status")
	private UserStatus userStatus;

	@Column(name = "auth_token", columnDefinition = "TEXT")
	private String authToken;

	private Date createdDate;

	private Date lastModifiedDate;
	
	@Column(name = "device_id", columnDefinition = "TEXT")
	private String devideId;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();
	
	public void addRole(Role userRole) {
		if(this.roles == null)		{
			this.roles = new HashSet<>();
		}
		for(Role role : this.roles) {
			if(role.getName().equals(userRole.getName())){
				return;
			}
		}
		if(userRole != null && userRole.getName() != null) {
			this.roles.add(userRole);	
		}
		
	}
}
