package com.ucsf.auth.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "two_factor_authentication")
@Data
@NoArgsConstructor
public class TwoFactorAuthentication {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "code")
	private String code;

	@Column(name = "expired_at")
	private Date expiredAt;

	@Column(name = "created_at")
	private Date createdAt;

}
