package com.ucsf.service.impl;

import com.ucsf.auth.model.User;
import com.ucsf.model.Notifications;
import com.ucsf.repository.NotificationsRepository;
import com.ucsf.service.NotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service("NotificationsService")
public class NotificationServiceImpl implements NotificationsService {

	@Autowired
	NotificationsRepository notificationsRepository;
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Notifications> getListByUser(User user) {

		List<Notifications> list = null;

		if (notificationsRepository.findByUserId(user.getId()) != null) {
			list = notificationsRepository.findByUserId(user.getId());
		}

		return list;
	}

	@Override
	public List<Notifications> getListBySentTo(String sentTo) {

		List<Notifications> notifications = notificationsRepository.findBySentTOOrderByIdDesc("studyTeam");
		return notifications;
	}
	
	@Override
	public List<Notifications> getListBySentToAndIsRead(String sentTo,Boolean isRead) {
		List<Notifications> notifications = notificationsRepository.getListBySentTOAndIsRead("studyTeam",isRead);
		return notifications;
	}
	
	@Override
	public Notifications updateStatus(Long id,Boolean isRead) {
		Optional<Notifications> notification = notificationsRepository.findById(id);
		Notifications notify = notification.get();
		notify.setIsRead(isRead);
		notificationsRepository.save(notify);
		return notify;
	}
	
	@Override
	public void updateAll(Boolean isRead) {
		List<Notifications> notifications = new ArrayList<Notifications>();
		notifications = notificationsRepository.findAllByIsRead(!isRead);
		for(Notifications notify:notifications) {
			notify.setIsRead(isRead);
			notificationsRepository.save(notify);
		}
	}
}
