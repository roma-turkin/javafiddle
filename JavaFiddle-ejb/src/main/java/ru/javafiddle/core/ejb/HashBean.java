package ru.javafiddle.core.ejb;

import ru.javafiddle.jpa.entity.Access;
import ru.javafiddle.jpa.entity.Hash;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public String getHashForNewProject(int projectId) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        Integer projId = new Integer(projectId);
        String prId = projId.toString();

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] array = md.digest(prId.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
        }
        return sb.toString();

    }

}
