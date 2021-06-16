package com.ucsf.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.ucsf.auth.model.User;
import com.ucsf.auth.model.UserOtp;
import com.ucsf.model.Notifications;
import com.ucsf.repository.NotificationsRepository;
import com.ucsf.repository.OtpRepository;
import com.ucsf.repository.UserRepository;

@Service
public class OtpService {
	
	@Autowired
	OtpRepository otpRepository;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	NotificationsRepository notificationsRepository;

	@Value("${spring.mail.from}")
	String fromEmail;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	UserRepository userRepository;
	
	public void saveOtp(UserOtp otp) {
		otpRepository.save(otp);
	}
	
	public UserOtp findByUserId(Long id) {
		return otpRepository.findByUserId(id);
	}
	
	public void  informStudyTeam(User user) {
		List<String> list = new ArrayList<String>();
		List<Map<String, Object>> patientList = jdbcTemplate.queryForList(
				"SELECT * FROM users u JOIN user_roles ur ON u.user_id = ur.user_id and ur.role_id IN(3,4)");
		Long userId ;
		for (Map<String, Object> map : patientList) {
			if (map.get("user_id") != null) {
				userId = Long.parseLong(map.get("user_id").toString());
				list.add(userRepository.findById(userId).get().getEmail());
				}
			}
		String[] emailIds = list.toArray(new String[list.size()]);
		try {
			emailService.informStudyTeam(fromEmail, emailIds, "New Patient Registered",
					user.getFirstName() + " " + user.getLastName(),user.getEmail());
			
			Notifications notification = new Notifications();
			notification.setDate(new Date());
			notification.setDescription("Patient Registered");
			notification.setKind(Notifications.NotificationKind.AUTHENTICATE);
			notification.setKindDescription("New Patient has been registered named: "+user.getFirstName());
			notification.setType(Notifications.NotificationType.EMAIL);
			notification.setUserId(user.getId());
			notification.setSentTO("studyTeam");
			notification.setIsRead(false);
			notificationsRepository.save(notification);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
}
