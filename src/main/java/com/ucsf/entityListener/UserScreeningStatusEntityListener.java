package com.ucsf.entityListener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ucsf.auditModel.Action;
import com.ucsf.auditModel.ScreeningAnswersHistory;
import com.ucsf.auditModel.UserScreeningStatusHistory;
import com.ucsf.model.ScreeningAnswers;
import com.ucsf.model.UserScreeningStatus;
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

public class UserScreeningStatusEntityListener {
	
	@PrePersist
    public void prePersist(UserScreeningStatus target) {
        perform(target, INSERTED);
    }

    @PreUpdate
    public void preUpdate(UserScreeningStatus target) {
        perform(target, UPDATED);
    }

    @PreRemove
    public void preRemove(UserScreeningStatus target) {
        perform(target, DELETED);
    }
    
    @Transactional
    private void perform(UserScreeningStatus target, Action action) {
        try {
        	EntityManager entityManager = BeanUtil.getBean(EntityManager.class);
        	JdbcTemplate jdbcTemplate = BeanUtil.getBean(JdbcTemplate.class);
        	
        	String sql = "Select user_screening_status_content from user_screening_status_history where status_id = "+target.getId()+" order by id desc limit 1";
        	List<String> list = jdbcTemplate.queryForList(sql, String.class);
        	String previousContent = list.size() > 0 ? list.get(0) : null;
        	
        	if(previousContent != null) {
				UserScreeningStatus previousUser = new Gson().fromJson(previousContent, UserScreeningStatus.class);
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

                	entityManager.persist(new UserScreeningStatusHistory(target, action, gson.toJson(target), previousContent, changedContent.toString()));
            	}
               	        	
	        } else {

				GsonBuilder x = new GsonBuilder();

				x.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);

				Gson gson = x.create();

	        	entityManager.persist(new UserScreeningStatusHistory(target, action, gson.toJson(target), "", ""));
	        }
            
        }catch (Exception e) {
			e.printStackTrace();
		}
               
    }
}
