package com.ucsf.service;

import com.ucsf.auth.model.User;
import com.ucsf.model.Notifications;

import java.util.List;

public interface NotificationsService {

    List<Notifications> getListByUser(User user);
    List<Notifications> getListBySentTo(String sentTo);
    Notifications updateStatus(Long id,Boolean isRead);
}
