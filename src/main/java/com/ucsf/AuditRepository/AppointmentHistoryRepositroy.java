package com.ucsf.AuditRepository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ucsf.auditModel.AppointmentHistory;

@Repository
public interface AppointmentHistoryRepositroy extends CrudRepository<AppointmentHistory, Integer> {

}
