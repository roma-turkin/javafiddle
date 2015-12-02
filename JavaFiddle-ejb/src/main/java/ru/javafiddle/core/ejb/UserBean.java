package main.java.ru.javafiddle.core.ejb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import ru.javafiddle.jpa.entity.User;
import javax.inject.Named;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @author mac
 */
@Stateless
@Named(value = "userBean")
public class UserBean {

    private static final Integer DEFAULT_USER_STATUS = 1;

    @PersistenceContext
    EntityManager em;

    public User register(String firstName, String lastName, String nickname, String email, String passwordHash) {

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

        return user;
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

        em.getTransaction().begin();
        em.remove(user);
        em.getTransaction().commit();
        return user;

    }


    //Solve issue with JOINs (should we add links to tables in JPA)
    List<String> getUserProjects(String nickName) {

        //I'm not sure now that it is the right query, joins in this sql are really strange
        List<String> result = (List<String>)em.createQuery("SELECT distinct x.hash FROM USER p JOIN p.UserGroups a JOIN a.Groups b JOIN b.Projects c JOIN c.Hashes x" +
                "WHERE p.nickName =:nickname")
                .setParameter("nickname", nickName)
                .getResultList();

        return result;
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
            user.setHash(passwordHash);

        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();

        return user;
    }


}
