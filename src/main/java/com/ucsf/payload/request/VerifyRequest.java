package com.ucsf.payload.request;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class VerifyRequest implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;

	private String email;
	private String code;
	private Boolean isNew;

	public VerifyRequest(String email, String code,Boolean isNew) {
		this.email = email;
		this.code = code;
		this.isNew = isNew;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
