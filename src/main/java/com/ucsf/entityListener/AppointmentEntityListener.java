package com.ucsf.entityListener;

import static com.ucsf.auditModel.Action.DELETED;
import static com.ucsf.auditModel.Action.INSERTED;
import static com.ucsf.auditModel.Action.UPDATED;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.apache.commons.lang3.builder.Diff;
import org.apache.commons.lang3.builder.DiffResult;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.ucsf.auditModel.Action;
import com.ucsf.auditModel.AppointmentHistory;
import com.ucsf.model.Appointment;
import com.ucsf.util.BeanUtil;

public class AppointmentEntityListener {

	@PrePersist
	public void prePersist(Appointment target) {
		perform(target, INSERTED);
	}

	@PreUpdate
	public void preUpdate(Appointment target) {
		perform(target, UPDATED);
	}

	@PreRemove
	public void preRemove(Appointment target) {
		perform(target, DELETED);
	}

	@Transactional
	private void perform(Appointment target, Action action) {
		try {
			EntityManager entityManager = BeanUtil.getBean(EntityManager.class);
			JdbcTemplate jdbcTemplate = BeanUtil.getBean(JdbcTemplate.class);

			jdbcTemplate.update("SET FOREIGN_KEY_CHECKS = 0");

			String sql = "Select appointment_content from appointment_history where appointment_id = "
					+ target.getAppointmentId() + " order by id desc limit 1";
			List<String> list = jdbcTemplate.queryForList(sql, String.class);
			String previousContent = list.size() > 0 ? list.get(0) : null;

			if (previousContent != null) {
				Appointment previousAppointment = new Gson().fromJson(previousContent, Appointment.class);
				DiffResult<?> diff = previousAppointment.diff(target);
				JSONObject changedContent = new JSONObject();
				for (Diff<?> d : diff.getDiffs()) {
					changedContent.put(d.getFieldName(), "FROM " + d.getLeft() + " TO " + d.getRight() + "");
				}
				if (changedContent.keySet().size() > 0) {
					entityManager.persist(new AppointmentHistory(target, action, new Gson().toJson(target),
							previousContent, changedContent.toString()));
				}

			} else {
				entityManager.persist(new AppointmentHistory(target, action, new Gson().toJson(target), "", ""));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
