package com.ucsf.AuditRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ucsf.auditModel.UserHistory;

@Repository
public interface UserHistoryRepositroy extends JpaRepository<UserHistory, Integer> {
}