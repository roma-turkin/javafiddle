package ru.javafiddle.core.ejb;


import ru.javafiddle.jpa.entity.Status;

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
public class StatusBean {

    private static final Logger logger =
            Logger.getLogger(ProjectBean.class.getName());

    @PersistenceContext(name = "JFPersistenceUnit")
    EntityManager em;

    public StatusBean() {

    }

    public Status getStatus(int statusId) {
        Status status;

        try {
            status = (Status) em.createQuery("SELECT s FROM Status s WHERE s.statusId =:statusid")
                    .setParameter("statusid", statusId)
                    .getSingleResult();
        } catch (NoResultException noResult) {
            logger.log(Level.WARNING, "No result in getHash()", noResult);
            return null;
        }
        return status;

    }

}
