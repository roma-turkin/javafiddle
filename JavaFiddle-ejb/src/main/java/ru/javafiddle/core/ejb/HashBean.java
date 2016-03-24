package ru.javafiddle.core.ejb;

import ru.javafiddle.jpa.entity.Access;
import ru.javafiddle.jpa.entity.Hash;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mac on 24.03.16.
 */

@Stateless
public class HashBean {

    private static final Logger logger =
            Logger.getLogger(ProjectBean.class.getName());

    @PersistenceContext(name = "JFPersistenceUnit")
    EntityManager em;

    public HashBean(){

    }

    public Hash getHash(int hashId) {

        Hash hash;

        try {
            hash = (Hash) em.createQuery("SELECT h FROM Hash h WHERE h.id =:hashid")
                    .setParameter("hashid", hashId)
                    .getSingleResult();
        } catch (NoResultException noResult) {
            logger.log(Level.WARNING, "No result in getHash()", noResult);
            return null;
        }
        return hash;

    }

}
