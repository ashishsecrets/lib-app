package com.ucsf.entityListener;

import static com.ucsf.auditModel.Action.DELETED;
import static com.ucsf.auditModel.Action.INSERTED;
import static com.ucsf.auditModel.Action.UPDATED;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import com.google.gson.GsonBuilder;
import com.ucsf.auditModel.*;
import com.ucsf.model.Tasks;
import com.ucsf.model.UcsfStudy;
import com.ucsf.model.UcsfSurvey;
import com.ucsf.util.HibernateProxyTypeAdapter;
import org.apache.commons.lang3.builder.Diff;
import org.apache.commons.lang3.builder.DiffResult;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.ucsf.model.Appointment;
import com.ucsf.util.BeanUtil;

public class UcsfSurveyEntityListener {

    @PrePersist
    public void prePersist(UcsfSurvey target) {

        perform(target, INSERTED);
    }

    @PreUpdate
    public void preUpdate(UcsfSurvey target) {

        perform(target, UPDATED);
    }

    @PreRemove
    public void preRemove(UcsfSurvey target) {

        perform(target, DELETED);
    }

    @Transactional
    private void perform(UcsfSurvey target, Action action) {
        try {
            EntityManager entityManager = BeanUtil.getBean(EntityManager.class);
            JdbcTemplate jdbcTemplate = BeanUtil.getBean(JdbcTemplate.class);

            String sql = "Select ucsf_study_content from ucsf_study_history where study_id = "
                    + target.getId() + " order by id desc limit 1";
            List<String> list = jdbcTemplate.queryForList(sql, String.class);
            String previousContent = list.size() > 0 ? list.get(0) : null;

            if (previousContent != null) {
                UcsfSurvey previousUcsfSurvey = new Gson().fromJson(previousContent, UcsfSurvey.class);
                DiffResult<?> diff = previousUcsfSurvey.diff(target);
                JSONObject changedContent = new JSONObject();
                for (Diff<?> d : diff.getDiffs()) {
                    changedContent.put(d.getFieldName(), "FROM " + d.getLeft() + " TO " + d.getRight() + "");
                }
                if (changedContent.keySet().size() > 0) {

                    GsonBuilder x = new GsonBuilder();

                    x.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);

                    Gson gson = x.create();

                    entityManager.persist(new UcsfSurveyHistory(target, action, gson.toJson(target), previousContent, changedContent.toString()));
                }

            } else {

                GsonBuilder x = new GsonBuilder();

                x.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);

                Gson gson = x.create();

                entityManager.persist(new UcsfSurveyHistory(target, action, gson.toJson(target), "", ""));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
