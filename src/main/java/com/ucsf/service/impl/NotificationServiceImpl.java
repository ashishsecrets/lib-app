package com.ucsf.service.impl;

import com.ucsf.auth.model.User;
import com.ucsf.model.Notifications;
import com.ucsf.repository.NotificationsRepository;
import com.ucsf.service.NotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("NotificationsService")
public class NotificationServiceImpl implements NotificationsService {

    @Autowired
    NotificationsRepository notificationsRepository;

    @Override
    public List<Notifications> getListByUser(User user) {

        return notificationsRepository.findByUserId(user.getId());
    }

}
