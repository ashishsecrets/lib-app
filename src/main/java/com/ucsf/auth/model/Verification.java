package com.ucsf.auth.model;

import java.util.Date;

import javax.persistence.*;

import org.springframework.data.annotation.PersistenceConstructor;

import lombok.Data;

@Entity
@Table(name = "otp")
@Data
public class Verification {
	
	@Id
	@Column(unique = true, nullable = false)
	private String phone;

	@Column(nullable = false)
	private String requestId;

	@Column(nullable = false)
	private Date expirationDate;

	@PersistenceConstructor
	public Verification() {
		// Empty constructor for JPA
	}

	public Verification(String phone, String requestId, Date expirationDate) {
		this.phone = phone;
		this.requestId = requestId;
		this.expirationDate = expirationDate;
	}

}