package com.ucsf.auth.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_otp")
@Data
@NoArgsConstructor
public class UserOtp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "otp_code")
	private String otpCode;
	
	@Column(name = "otp_expiry")
	private Long otpExpiry;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "user_id")
	private Long userId;
	
	@OneToOne(targetEntity = User.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "user_id",insertable = false,updatable = false)
	private User user;

}
