package ru.javafiddle.ejb.beans;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author mac
 */
@Stateless
@Named(value = "userBean")
public class UserBean {

    @PersistenceContext(unitName = "User")
    EntityManager em;

    public UserJF register(UserRegistrationData userRegistrationData) {

        User user = new User();

        user.setFirstName(userRegistrationData.getFirstname());
        user.setLastName(userRegistrationData.getLastName());
        user.setNickName(userRegistrationData.getNickName());
        user.setEmail(userRegistrationData.getNickName());
        user.setPasswordHash(userRegistrationData.getPasswordHash());

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        user.setRegistered(dateFormat.format(date));
        user.setStatus(1);//add status information in userRegistration

        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
    }

    public UserJF getUserInfo(String nickName) {

        User user;

        try {
            user = (User)em.createQuery("SELECT p FROM User p WHERE p.nickName LIKE :nickName")
                    .setParameter("nickName", nickName)
                    .getSingleResult();
        } catch (NoResultException noResult) {

            return null;
        }

        return this.changeType(user);

    }

    public UserJF setUserInfo(String nickName, UserRegistrationData userRegistrationData) {

        User user;

        em.getTransaction().begin();
        try {
            user = (User)em.createQuery("SELECT p FROM User p WHERE p.nickName LIKE :nickName")
                    .setParameter("nickName", nickName)
                    .getSingleResult();
        } catch (NoResultException noresult) {

            return null;
        }

        this.setFields(user, userRegistrationData);

        em.getTransaction().commit();


    }

    public UserJF deleteUser(String nickName) {

        User user;

        em.getTransaction().begin();
        try {
            user = (User)em.createQuery("SELECT p FROM User p WHERE p.nickName LIKE :nickName")
                    .setParameter("nickName", nickName)
                    .getSingleResult();
        } catch (NoResultException noresult) {

            return null;
        }

        em.remove(user);
        em.getTransaction().commit();
        return user;

    }

    public  UserJF changeType(User user) {

        UserJF userjf = new UserJF();

        userjf.setFirstName(user.getFirstname());
        userjf.setLastName(user.getLastName());
        userjf.setNickName(user.getNickName());
        userjf.setEmail(user.getNickName());



        //I'm not sure now that it is the right query, joins in this sql are really strange
        List<String> result = (List<String>)em.createQuery(("SELECT  new List(x.hash) FROM USER p JOIN p.UserGroups a JOIN a.Groups b JOIN b.Projects c JOIN c.Hashes x" +
                "WHERE p.nickName LIKE :nickName")
                .setParameter("nickName", user.getNickName())
                .getResultList();

        userjf.setProjectHashes(result);

        return userjf;
    }

    public void setFields(User user, UserRegistrationData userRegistrationData) {

        user.setFirstName(userRegistrationData.getFirstname());
        user.setLastName(userRegistrationData.getLastName());
        user.setNickName(userRegistrationData.getNickName());
        user.setEmail(userRegistrationData.getNickName());
        user.setPasswordHash(userRegistrationData.getPasswordHash());


    }


}