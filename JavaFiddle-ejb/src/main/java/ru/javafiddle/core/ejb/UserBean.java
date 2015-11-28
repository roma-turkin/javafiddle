package ru.javafiddle.ejb.beans;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import ru.javafiddle.jpa.entity.User;
/**
 *
 * @author mac
 */
@Stateless
@Named(value = "userBean")
public class UserBean {



    @PersistenceContext(unitName = "")
    EntityManager em;

    public UserJF register(String firstName, String lastName, String nickname, String email, String passwordHash) {

        User user = new User();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setNickName(nickname);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        user.setRegistered(dateFormat.format(date));
        user.setStatus(DEFAULT_USER_STATUS);//still add status information in userRegistration

        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
    }

    public User getUser(String nickName) {

        User user;

        try {
            user = (User)em.createQuery("SELECT p FROM User p WHERE p.nickName =: nickName")
                    .setParameter("nickName", nickName)
                    .getSingleResult();
        } catch (NoResultException noResult) {

            return null;
        }

        return user;

    }

    public User setUser(String nickName, String firstName, String lastName, String newNickName, String email, String passwordHash) {

        User user;

        try {
            user = (User)em.createQuery("SELECT p FROM User p WHERE p.nickName =:nickName")
                    .setParameter("nickName", nickName)
                    .getSingleResult();
        } catch (NoResultException noresult) {

            return null;
        }

        this.setFields(user, firstName, lastName, newNickName, email, passwordHash);



    }

    public UserJF deleteUser(String nickName) {

        User user;


        try {
            user = (User)em.createQuery("SELECT p FROM User p WHERE p.nickName =:nickName")
                    .setParameter("nickName", nickName)
                    .getSingleResult();
        } catch (NoResultException noresult) {

            return null;
        }

        em.getTransaction().begin();
        em.remove(user);
        em.getTransaction().commit();
        return user;

    }


    //Solve issue with JOINs (should we add links to tables in JPA)
    List<String> getUserProjects(String nickName) {

       // List<int> ids = (List<int>)em.createQuery("SELECT c.groupId FROM User p JOIN p.UserGroups a JOIN Groups c where p.nickName =:nickName");



        //I'm not sure now that it is the right query, joins in this sql are really strange
        List<String> result = (List<String>)em.createQuery(("SELECT x.hash FROM USER p JOIN p.UserGroups a JOIN a.Groups b JOIN b.Projects c JOIN c.Hashes x" +
                "WHERE p.nickName =:nickName")
                .setParameter("nickName", user.getNickName())
                .getResultList();

        return result;
    }


    public void setFields(User user, String firstName, String lastName, String newNickName, String email, String passwordHash) {

        if(!firstName.equal(""))
            user.setFirstName(firstName);
        if(!lastName.equal(""))
            user.setLastName(lastName);
        if(!newNickName.equal(""))
            user.setNickName(newNickName);
        if(!email.equal(""))
            user.setEmail(email);
        if(!passwordHash.equal(""))
            user.setHash(passwordHash);

        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
    }


}