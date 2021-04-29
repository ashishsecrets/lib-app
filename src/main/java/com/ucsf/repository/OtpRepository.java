package com.ucsf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ucsf.auth.model.UserOtp;

@Repository
public interface OtpRepository extends JpaRepository<UserOtp, Long> {
	UserOtp findByUserId(Long userId);
}