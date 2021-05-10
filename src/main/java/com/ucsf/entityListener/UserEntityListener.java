package com.ucsf.entityListener;

import javax.persistence.EntityManager;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.transaction.Transactional;

import com.ucsf.auditModel.Action;
import com.ucsf.auditModel.UserHistory;
import com.ucsf.auth.model.User;
import com.ucsf.util.BeanUtil;

import static javax.transaction.Transactional.TxType.MANDATORY;
import static com.ucsf.auditModel.Action.*;



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

    @Transactional(MANDATORY)
    private void perform(User target, Action action) {
        EntityManager entityManager = BeanUtil.getBean(EntityManager.class);
        entityManager.persist(new UserHistory(target, action));
    }

}