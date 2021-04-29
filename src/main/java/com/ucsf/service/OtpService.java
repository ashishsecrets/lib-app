package com.ucsf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ucsf.auth.model.UserOtp;
import com.ucsf.repository.OtpRepository;

@Service
public class OtpService {
	
	@Autowired
	OtpRepository otpRepository;
	
	public void saveOtp(UserOtp otp) {
		otpRepository.save(otp);
	}
	
	public UserOtp findByUserId(Long id) {
		return otpRepository.findByUserId(id);
	}
}
