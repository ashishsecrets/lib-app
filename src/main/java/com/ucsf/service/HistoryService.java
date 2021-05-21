package com.ucsf.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.ucsf.auditModel.UserHistory;
import com.ucsf.payload.response.HistoryResponse;

@Service
public class HistoryService {
	
	@Autowired JdbcTemplate jdbcTemplate;

	public List<UserHistory> getUserActivityLogs() {
		
		String sql = "select * from user_history where modified_by is not null or user_id in (select u.user_id from users u join user_roles ur on "
				+ " u.user_id = ur.user_id where ur.role_id in (3,4))";
		List<UserHistory> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<UserHistory>(UserHistory.class));
		
		return list;
	}
	
	public List<HistoryResponse> getUserActivityLogsByUserId(Long userId) {
		
		String sql = "select * from user_history where user_id = "+userId;
		List<UserHistory> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<UserHistory>(UserHistory.class));
		List<HistoryResponse> updatedList = new ArrayList<HistoryResponse>();
		for(UserHistory history: list) {
			if(history.getChangedContent()!=null && !history.getChangedContent().equals("")) {
				
				HistoryResponse historyResponse = new HistoryResponse();
				historyResponse.setChangedContent(new JSONObject(history.getChangedContent()));
				historyResponse.setAction(history.getAction().toString());
				historyResponse.setModifiedBy(history.getModifiedBy());
				historyResponse.setModifiedDate(history.getModifiedDate());
				updatedList.add(historyResponse);
			}
		}
		return updatedList;
	}
}
