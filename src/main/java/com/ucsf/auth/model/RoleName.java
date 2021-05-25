package com.ucsf.auth.model;

import org.springframework.security.core.GrantedAuthority;

public enum RoleName implements GrantedAuthority{
	ADMIN, PATIENT, PHYSICIAN,STUDYTEAM, PRE_VERIFICATION_USER;

	@Override
	public String getAuthority() {
		// TODO Auto-generated method stub
		return null;
	}
}