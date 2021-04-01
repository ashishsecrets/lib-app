package com.ucsf.repository;

import org.springframework.data.repository.CrudRepository;

import com.ucsf.auth.model.TwoFactorAuthentication;

public interface VerificationRepository extends CrudRepository<TwoFactorAuthentication, Long> {
	TwoFactorAuthentication findByUserId(Long id);
}