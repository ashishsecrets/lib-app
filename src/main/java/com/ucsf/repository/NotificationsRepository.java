package com.ucsf.repository;

import com.ucsf.model.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationsRepository extends JpaRepository<Notifications, Long> {

    List<Notifications> findByUserId(Long userId);
    List<Notifications> getListBySentTOAndIsRead(String sentTo,Boolean isRead);
    List<Notifications> findBySentTOOrderByIdDesc(String sentTo);

}