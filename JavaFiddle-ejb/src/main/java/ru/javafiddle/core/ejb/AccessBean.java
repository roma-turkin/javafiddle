package ru.javafiddle.core.ejb;

import ru.javafiddle.jpa.entity.Access;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mac on 23.03.16.
 */

@Stateless
public class AccessBean {

    private static final Logger logger =
            Logger.getLogger(ProjectBean.class.getName());

    @PersistenceContext(name = "JFPersistenceUnit")
    EntityManager em;

    public AccessBean(){

    }

    public Access getAccess(String accessRights) {

        Access access;

        try {
            access = (Access) em.createQuery("SELECT p FROM Access p WHERE p.accessName =:accessname")
                    .setParameter("accessname", accessRights)
                    .getSingleResult();
        }catch(NoResultException noResult) {
            logger.log(Level.WARNING, "No result in getAccess()", noResult);
            return null;
        }
        return access;

    }




}
