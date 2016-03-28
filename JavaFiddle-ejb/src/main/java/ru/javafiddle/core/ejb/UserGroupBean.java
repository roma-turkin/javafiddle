package ru.javafiddle.core.ejb;

import ru.javafiddle.jpa.entity.*;

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

    public UserGroup createUserGroup(User u, Group g, Access a) {


/*
        Access a = (Access)em.createQuery("SELECT a FROM Access a WHERE a.accessId =:accessid")
                            .setParameter("accessid", accessId)
                            .getSingleResult();

        Group g = (Group)em.createQuery("SELECT g FROM Group g WHERE g.groupId =:groupid")
                .setParameter("groupid", groupId)
                .getSingleResult();

        User u = (User)em.createQuery("SELECT u FROM User u WHERE u.userId =:userid")
                .setParameter("userid", userId)
                .getSingleResult();*/

        UserGroup ug = new UserGroup(g, u, a);

        ug.setUserId(u.getUserId());
        ug.setGroupId(g.getGroupId());

        //  em.getTransaction().begin();
        em.persist(ug);
        //       em.flush();
        //  em.getTransaction().commit();
        return ug;

    }

}
