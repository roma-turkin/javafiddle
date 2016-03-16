package ru.javafiddle.core.ejb;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

        //em.getTransaction().begin();
        em.persist(user);
 //       em.flush();
       // em.getTransaction().commit();

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

            logger.log(Level.WARNING, "NO RESULT IN QUERY", noResult);
            return null;
        }

        return user;

    }
//In services there is a problem with this function
//Look and correct two types of nicks.
    public User setUser(String nickName, String firstName, String lastName, String newNickName, String email, String passwordHash) {

        User user;

        try {
            user = (User)em.createQuery("SELECT p FROM User p WHERE p.nickName = :nickName")
                    .setParameter("nickName", nickName)
                    .getSingleResult();
        } catch (NoResultException noResult) {

            logger.log(Level.WARNING, "NO RESULT IN QUERY", noResult);
            return null;
        }

        user = this.setFields(user, firstName, lastName, newNickName, email, passwordHash);

        return user;

    }


    //we need to delete all entries from usergroup table too for this user
    public User deleteUser(String nickName) {

        User user;

        user = (User)em.createQuery("SELECT p FROM User p WHERE p.nickName =:nickName")
                .setParameter("nickName", nickName)
                .getSingleResult();


        TypedQuery<UserGroup> query =
                em.createQuery("SELECT u FROM UserGroup u WHERE u.userId =:userid", UserGroup.class);
        List<UserGroup> u = query.setParameter("userid", user.getUserId()).getResultList();

        for(Iterator<UserGroup> i = u.iterator(); i.hasNext(); ) {
            UserGroup u1 = i.next();

            em.remove(u1);
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

     /*   for (UserGroup g:) {

            List<Project> projects = UserGroup.getProjects();
            for (Project p:projects) {
                hashes.add(p.getHash().getHash());
            }
        }*/

        for (UserGroup userGroup:groups) {

            Group group = userGroup.getGroup();
            List<Project> projects = group.getProjects();
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

    public String getCurUserNick() {
        String nick = ctx.getCallerPrincipal().getName();
        return nick;
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
