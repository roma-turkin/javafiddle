package ru.javafiddle.core.ejb;

import ru.javafiddle.jpa.entity.Hash;
import ru.javafiddle.jpa.entity.UserGroup;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mac on 27.03.16.
 */
@Stateless
public class UserGroupBean {

    private static final Logger logger =
            Logger.getLogger(ProjectBean.class.getName());

    @PersistenceContext(name = "JFPersistenceUnit")
    EntityManager em;

    public UserGroupBean() {

    }

    public UserGroup getUserGroup(int userId, int groupId) {

        UserGroup ug;
        try {
            ug = (UserGroup)em.createQuery("SELECT ug FROM UserGroup ug WHERE ug.userId =:userid AND ug.groupId =:groupid")
                    .setParameter("userid", userId)
                    .setParameter("groupid", groupId)
                    .getSingleResult();
        } catch (NoResultException noResult) {
            logger.log(Level.WARNING, "No result in getUserGroup()", noResult);
            return null;
        }

        return ug;
    }

}
