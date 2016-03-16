package ru.javafiddle.core.ejb;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.client.Entity;

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

    private static final int DEFAULT_USER_STATUS = 1;

    @PersistenceContext(name = "JFPersistenceUnit")
    EntityManager em;

    public UserBean(){}

    public User register(String firstName, String lastName, String nickname, String email, String passwordHash) {

        //search for registered class
        Status st = em.find(Status.class,DEFAULT_USER_STATUS);
        User user = new User();


        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setNickName(nickname);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        user.setRegistrationDate(dateFormat.format(date));

        em.getTransaction().begin();
        em.persist(user);
        em.flush();
        em.getTransaction().commit();

        User uBase = getUser(nickname);
        return uBase;

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

       // user = this.setFields(user, firstName, lastName, newNickName, email, passwordHash);

        return user;

    }


    //we need to delete all entries from usergroup table too for this user
    public User deleteUser(String nickName) {

        User user;

        user = (User)em.createQuery("SELECT p FROM User p WHERE p.nickName =:nickName")
                .setParameter("nickName", nickName)
                .getSingleResult();

        List<UserGroup> u = (List<UserGroup>)em.createQuery("SELECT u from UserGroup u WHERE u.userId=:userid")
                            .setParameter("userid", user.getUserId())
                            .getResultList();

        for(Iterator<UserGroup> i = u.iterator(); i.hasNext(); ) {
            UserGroup u1 = i.next();

            em.getTransaction().begin();
            em.remove(u1);
            em.getTransaction().commit();
        }


        em.getTransaction().begin();
        em.remove(user);
        em.getTransaction().commit();
        return user;

    }


    public List<String> getUserProjects(String nickName) {

//        List<String> hashes = new LinkedList<String>();
//        User user = (User)em.createQuery("SELECT u FROM User u WHERE u.nickName=:nickname")
//                .setParameter("nickname", nickName)
//                .getSingleResult();
//        List<Group> groups = user.getGroups();
//
//        for (Group g:groups) {
//
//            List<Project> projects = group.getProjects();
//            for (Project p:projects) {
//                hashes.add(p.getHash().getHash());
//            }
//        }
//
//        return hashes;
        return null;
    }


    public User setFields(User user, String firstName, String lastName, String newNickName, String email, String passwordHash, EntityManager em) {

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

        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();

        return user;
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User updateUser(User user, String nickname) {
        User u = em.find(User.class,1);
        System.out.println(u.getNickName());
        em.merge(u);
       // User user1 = getUser(user.getNickName());
        em.getTransaction().begin();
        u.setNickName(nickname);
        em.getTransaction().commit();
       // em.refresh(u);
        em.flush();

        User u1 = getUser(nickname);
        return u1;
    }

   /* public void wrongUpdateUser(String nickName, EntityManager em) {
        em.getTransaction().begin();
        User u = getUser(nickName, em);
        u.setEmail("colsvfldkn");
        em.getTransaction().commit();

    }*/


}
