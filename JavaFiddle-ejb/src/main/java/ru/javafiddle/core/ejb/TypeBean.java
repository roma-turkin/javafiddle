package ru.javafiddle.core.ejb;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mac on 27.03.16.
 */
public class TypeBean {

    private static final Logger logger =
            Logger.getLogger(ProjectBean.class.getName());

    @PersistenceContext(name = "JFPersistenceUnit")
    EntityManager em;

    public TypeBean() {

    }

    public Type getType(int typeId) {

        Type type;

        try {
            type = (Type) em.createQuery("SELECT t FROM Type t WHERE t.typeId =:typeid")
                    .setParameter("typeid", typeId)
                    .getSingleResult();
        } catch (NoResultException noResult) {
            logger.log(Level.WARNING, "No result in getHash()", noResult);
            return null;
        }
        return type;

    }

}
