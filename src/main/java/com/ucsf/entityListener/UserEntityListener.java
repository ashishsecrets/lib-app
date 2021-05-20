package com.ucsf.entityListener;

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
import com.ucsf.auditModel.UserHistory;
import com.ucsf.auth.model.User;
import com.ucsf.util.BeanUtil;

import static com.ucsf.auditModel.Action.*;

import java.util.List;

public class UserEntityListener {
	
	@PrePersist
    public void prePersist(User target) {
        perform(target, INSERTED);
    }

    @PreUpdate
    public void preUpdate(User target) {
        perform(target, UPDATED);
    }

    @PreRemove
    public void preRemove(User target) {
        perform(target, DELETED);
    }
    
    @Transactional
    private void perform(User target, Action action) {
        try {
        	EntityManager entityManager = BeanUtil.getBean(EntityManager.class);
        	JdbcTemplate jdbcTemplate = BeanUtil.getBean(JdbcTemplate.class);
        	
        	String sql = "Select user_content from user_history where user_id = "+target.getId()+" order by id desc limit 1";
        	List<String> list = jdbcTemplate.queryForList(sql, String.class);
        	String previousContent = list.size() > 0 ? list.get(0) : null;
        	
        	if(previousContent != null) {        		
        		User previousUser = new Gson().fromJson(previousContent, User.class);
            	DiffResult<?> diff = previousUser.diff(target);
            	JSONObject changedContent = new JSONObject();
                for(Diff<?> d: diff.getDiffs()) {
                	if(!d.getFieldName().equals("authToken")) {
                    	changedContent.put(d.getFieldName(), "FROM [" + d.getLeft() + "] TO [" + d.getRight() + "]");
                	}
                }
                if(changedContent.keySet().size() > 1) {
                	entityManager.persist(new UserHistory(target, action, new Gson().toJson(target), previousContent, changedContent.toString()));
            	}
               	        	
	        } else {
	        	entityManager.persist(new UserHistory(target, action, new Gson().toJson(target), "", ""));
	        }
            
        }catch (Exception e) {
			e.printStackTrace();
		}
               
    }
}
