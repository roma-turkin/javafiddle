package ru.javafiddle.core.ejb;

import ru.javafiddle.jpa.entity.Status;
import ru.javafiddle.jpa.entity.User;
import ru.javafiddle.jpa.entity.UserGroup;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author mac
 */
@Stateless
public class UserBean {

    @Resource
    private SessionContext ctx;

    private static final Logger logger =
            Logger.getLogger(UserBean.class.getName());

    private static final int DEFAULT_USER_STATUS = 1;

    @PersistenceContext(name = "JFPersistenceUnit")
    EntityManager em;

    public UserBean() {

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public User register(User user) {
        //search for registered class
        Status st = em.find(Status.class, DEFAULT_USER_STATUS);
        if (checkCorrectnessOfNick(user.getNickName())) {
            user.setStatus(st);

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            user.setRegistrationDate(dateFormat.format(date));

            em.persist(user);

            User uBase = getUser(user.getNickName());
            return uBase;
        } else {
            return null;
        }

    }


    public User updateUser(User newUser) {

        User oldUser = getUser(newUser.getNickName());
        this.setFields(oldUser, newUser.getFirstName(), newUser.getLastName(), newUser.getEmail(), newUser.getPasswordHash());

        return getUser(newUser.getNickName());

    }

    public User deleteUser(User user) {

        TypedQuery<UserGroup> query =
                em.createQuery("SELECT u FROM UserGroup u WHERE u.userId =:userid", UserGroup.class);
        List<UserGroup> u = query.setParameter("userid", user.getUserId()).getResultList();

        for (Iterator<UserGroup> i = u.iterator(); i.hasNext(); ) {
            UserGroup u1 = i.next();

            em.remove(u1);
        }

        em.remove(em.contains(user) ? user : em.merge(user));
        return user;

    }

    public User getUser(String nickName) {

        User user;

        try {
            user = (User) em.createQuery("SELECT u FROM User u WHERE u.nickName =:nickname")
                    .setParameter("nickname", nickName)
                    .getSingleResult();
        } catch (NoResultException noResult) {

            logger.log(Level.WARNING, "No result in getUser()", noResult);
            return null;
        }

        return user;

    }

    public void setFields(User user, String firstName, String lastName, String email, String passwordHash) {

        if (!firstName.equals(""))
            user.setFirstName(firstName);
        if (!lastName.equals(""))
            user.setLastName(lastName);
        if (!email.equals(""))
            user.setEmail(email);
        if (!passwordHash.equals(""))
            user.setPasswordHash(passwordHash);

        em.persist(user);

    }

    public String getCurUserNick() {
        String nick = ctx.getCallerPrincipal().getName();
        return nick;
    }


    public boolean checkCorrectnessOfNick(String nickName) {

        User user;

        try {
            user = (User) em.createQuery("SELECT u FROM User u WHERE u.nickName =:nickname")
                    .setParameter("nickname", nickName)
                    .getSingleResult();
        } catch (NoResultException noResult) {

            logger.log(Level.WARNING, "No result in getUser()", noResult);
            return true;
        }

        return false;
    }

}
