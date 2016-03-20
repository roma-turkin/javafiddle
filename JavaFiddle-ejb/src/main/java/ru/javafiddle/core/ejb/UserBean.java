package ru.javafiddle.core.ejb;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.faces.bean.ManagedBean;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import ru.javafiddle.jpa.entity.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @author mac
 */
@Stateless
public class UserBean {

    private static final String DEFAULT_USER_STATUS = "registered";

    @Resource
    private SessionContext ctx;

    @PersistenceContext(name = "JFPersistenceUnit")
    private EntityManager em;

    public UserBean() {
    }

    public User register(String firstName, String lastName, String nickname, String email, String passwordHash) {

        User user = new User();
        Status status = (Status)em.createQuery("SELECT s FROM Status s WHERE s.statusName =:status")
                .setParameter("status", DEFAULT_USER_STATUS)
                .getSingleResult();



        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setNickName(nickname);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        user.setRegistrationDate(dateFormat.format(date));
        user.setStatus(status);//still add status information in userRegistration

        em.persist(user);

        return user;
    }

    public User getUser(String nickName) {

        User user;

        try {
            user = (User)em.createQuery("SELECT u FROM User u WHERE u.nickName =:nickname")
                    .setParameter("nickname", nickName)
                    .getSingleResult();
        } catch (NoResultException noResult) {

            return null;
        }

        return user;

    }

    public User setUser(String nickName, String firstName, String lastName, String newNickName, String email, String passwordHash) {

        User user;

        try {
            user = (User)em.createQuery("SELECT p FROM User p WHERE p.nickName = :nickName")
                    .setParameter("nickName", nickName)
                    .getSingleResult();
        } catch (NoResultException noresult) {

            return null;
        }

        user = this.setFields(user, firstName, lastName, newNickName, email, passwordHash);

        return user;

    }

    public User deleteUser(String nickName) {

        User user;


        try {
            user = (User)em.createQuery("SELECT p FROM User p WHERE p.nickName =:nickName")
                    .setParameter("nickName", nickName)
                    .getSingleResult();
        } catch (NoResultException noresult) {

            return null;
        }

        em.remove(user);

        return user;

    }


    public List<String> getUserProjects(String nickName) {

        List<String> hashes = new LinkedList<String>();
        User user = (User)em.createQuery("SELECT u FROM User u WHERE u.nickName=:nickname")
                .setParameter("nickname", nickName)
                .getSingleResult();

        List<UserGroup> groups = user.getGroups();

        for (UserGroup g:groups) {

            List<Project> projects = g.getGroup().getProjects();
            for (Project p:projects) {
                hashes.add(p.getHash().getHash());
            }
        }

        return hashes;
    }


    public User setFields(User user, String firstName, String lastName, String newNickName, String email, String passwordHash) {

        if(!firstName.equals(""))
            user.setFirstName(firstName);
        if(!lastName.equals(""))
            user.setLastName(lastName);
        if(!newNickName.equals(""))
            user.setNickName(newNickName);
        if(!email.equals(""))
            user.setEmail(email);
        if(!passwordHash.equals(""))
            user.setPasswordHash(passwordHash);

        em.persist(user);

        return user;
    }

    public User findUser(String nickName) {
        User user;


        try {
            user = (User)em.createQuery("SELECT p FROM User p WHERE p.nickName =:nickName")
                    .setParameter("nickName", nickName)
                    .getSingleResult();
        } catch (NoResultException noresult) {

            return null;
        }

        return user;
    }

    public String getCurUserNick() {
        String nick = ctx.getCallerPrincipal().getName();
        return nick;
    }


}
