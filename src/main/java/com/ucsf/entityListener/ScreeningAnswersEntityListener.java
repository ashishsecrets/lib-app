package com.ucsf.entityListener;

import com.google.gson.Gson;
import com.ucsf.auditModel.Action;
import com.ucsf.auditModel.ScreeningAnswersHistory;
import com.ucsf.auditModel.UserHistory;
import com.ucsf.auth.model.User;
import com.ucsf.model.ScreeningAnswers;
import com.ucsf.util.BeanUtil;
import org.apache.commons.lang3.builder.Diff;
import org.apache.commons.lang3.builder.DiffResult;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import java.util.List;

import static com.ucsf.auditModel.Action.*;

public class ScreeningAnswersEntityListener {
	
	@PrePersist
    public void prePersist(ScreeningAnswers target) {
        perform(target, INSERTED);
    }

    @PreUpdate
    public void preUpdate(ScreeningAnswers target) {
        perform(target, UPDATED);
    }

    @PreRemove
    public void preRemove(ScreeningAnswers target) {
        perform(target, DELETED);
    }
    
    @Transactional
    private void perform(ScreeningAnswers target, Action action) {
        try {
        	EntityManager entityManager = BeanUtil.getBean(EntityManager.class);
        	JdbcTemplate jdbcTemplate = BeanUtil.getBean(JdbcTemplate.class);
        	
        	String sql = "Select screening_answers_content from screening_answers_history where screening_answer_id = "+target.getId()+" order by id desc limit 1";
        	List<String> list = jdbcTemplate.queryForList(sql, String.class);
        	String previousContent = list.size() > 0 ? list.get(0) : null;
        	
        	if(previousContent != null) {        		
        		ScreeningAnswers previousUser = new Gson().fromJson(previousContent, ScreeningAnswers.class);
            	DiffResult<?> diff = previousUser.diff(target);
            	JSONObject changedContent = new JSONObject();
                for(Diff<?> d: diff.getDiffs()) {
                	if(!d.getFieldName().equals("authToken")) {
                    	changedContent.put(d.getFieldName(), "FROM " + d.getLeft() + " TO " + d.getRight() + "");
                	}
                }
                if(changedContent.keySet().size() > 0) {
                	entityManager.persist(new ScreeningAnswersHistory(target, action, new Gson().toJson(target), previousContent, changedContent.toString()));
            	}
               	        	
	        } else {
	        	entityManager.persist(new ScreeningAnswersHistory(target, action, new Gson().toJson(target), "", ""));
	        }
            
        }catch (Exception e) {
			e.printStackTrace();
		}
               
    }
}
