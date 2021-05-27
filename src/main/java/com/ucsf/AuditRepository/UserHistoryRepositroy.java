package com.ucsf.AuditRepository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ucsf.auditModel.UserHistory;

@Repository
public interface UserHistoryRepositroy extends CrudRepository<UserHistory, Integer> {
	
}
