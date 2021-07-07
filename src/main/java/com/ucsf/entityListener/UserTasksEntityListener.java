package com.ucsf.entityListener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ucsf.auditModel.Action;
import com.ucsf.auditModel.UserSurveyStatusHistory;
import com.ucsf.auditModel.UserTasksHistory;
import com.ucsf.model.UserSurveyStatus;
import com.ucsf.model.UserTasks;
import com.ucsf.util.BeanUtil;
import com.ucsf.util.HibernateProxyTypeAdapter;
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

//Screening answers entity listner

public class UserTasksEntityListener {
	
	@PrePersist
    public void prePersist(UserTasks target) {
        perform(target, INSERTED);
    }

    @PreUpdate
    public void preUpdate(UserTasks target) {
        perform(target, UPDATED);
    }

    @PreRemove
    public void preRemove(UserTasks target) {
        perform(target, DELETED);
    }
    
    @Transactional
    private void perform(UserTasks target, Action action) {
        try {
        	EntityManager entityManager = BeanUtil.getBean(EntityManager.class);
        	JdbcTemplate jdbcTemplate = BeanUtil.getBean(JdbcTemplate.class);
        	
        	String sql = "Select user_tasks_content from user_tasks_history where taskId = "+target.getTaskTrueId()+" order by id desc limit 1";
        	List<String> list = jdbcTemplate.queryForList(sql, String.class);
        	String previousContent = list.size() > 0 ? list.get(0) : null;
        	
        	if(previousContent != null) {
				UserTasks previousUser = new Gson().fromJson(previousContent, UserTasks.class);
            	DiffResult<?> diff = previousUser.diff(target);
            	JSONObject changedContent = new JSONObject();
                for(Diff<?> d: diff.getDiffs()) {
                	if(!d.getFieldName().equals("authToken")) {
                    	changedContent.put(d.getFieldName(), "FROM " + d.getLeft() + " TO " + d.getRight() + "");
                	}
                }

                if(changedContent.keySet().size() > 0) {

					GsonBuilder x = new GsonBuilder();

					x.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);

					Gson gson = x.create();

                	entityManager.persist(new UserTasksHistory(target, action, gson.toJson(target), previousContent, changedContent.toString()));
            	}
               	        	
	        } else {

				GsonBuilder x = new GsonBuilder();

				x.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);

				Gson gson = x.create();

	        	entityManager.persist(new UserTasksHistory(target, action, gson.toJson(target), "", ""));
	        }
            
        }catch (Exception e) {
			e.printStackTrace();
		}
               
    }
}
