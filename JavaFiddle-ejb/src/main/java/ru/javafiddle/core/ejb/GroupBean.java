package ru.javafiddle.core.ejb;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.javafiddle.jpa.entity.*;


@Stateless

public class GroupBean {

    private static final Logger logger =
            Logger.getLogger(ProjectBean.class.getName());
    @PersistenceContext(name = "JFPersistenceUnit")
    EntityManager em;

    public GroupBean(){}

//we always know that the group were we are trying to add a user exits now

//CALL THIS METHOD WHEN THE PERSON IS CREATING A GROUP BY HIMSELF!!

    public void addMember(String groupName, String userNickName, String accessRights) {


        Group group = getGroup(groupName);
        if (group.getGroupId() == -1) {
            createGroup(groupName);
        }
        User user = getUser(userNickName);
        Access access = getAccess(accessRights);

        UserGroup ug = new UserGroup(group, user, access);


        ug.setUserId(user.getUserId());
        ug.setGroupId(group.getGroupId());

        //  em.getTransaction().begin();
        em.persist(ug);

        ug = getUserGroup(user.getUserId(),group.getGroupId());
        addUserGroupToGroupList(group, ug);


    }

    public  Map<String, String> getAllMembers(int groupId){

        //int groupId = getGroupId(groupName);

        TypedQuery<UserGroup> q= em.createQuery("SELECT p FROM UserGroup p WHERE p.groupId=:groupId",UserGroup.class);
        List<UserGroup> usergroup = q.setParameter("groupId", groupId)
                .getResultList();


        Map<String, String> mappedUserGroup = new HashMap<String, String>(usergroup.size());
        //UserGroup[] arrayOfUserGroup = (UserGroup[]) usergroup.toArray();

        for (UserGroup ug: usergroup) {

           // UserGroup userGroup = arrayOfUserGroup[i];
            User nickName = (User)em.createQuery("SELECT u.member FROM UserGroup u WHERE u.member.userId =:userid")
                    .setParameter("userid", ug.getUserId())
                    .getSingleResult();
            Access access =   (Access)em.createQuery("SELECT u.access FROM UserGroup u WHERE u.access.accessId =:accessid")
                    .setParameter("accessid", ug.getAccess().getAccessId())
                    .getSingleResult();
             mappedUserGroup.put(nickName.getNickName(), access.getAccessName());

        }
        return mappedUserGroup;

    }


/*    public void updateMember(int groupId, String userNickName, String accessRights) {
//do we update usernickname also?

        int userId = getUserId(userNickName);
        User user = getUser(userNickName);

        UserGroup u = (UserGroup)em.createQuery("SELECT g FROM UserGroup g WHERE g.userId=:userid and g.groupId=:groupid")
                        .setParameter("userid", userId)
                        .setParameter("groupid", groupId)
                        .getSingleResult();
        Access a = getAccess(accessRights);

        u.setAccess(a);

        em.getTransaction().begin();
        em.persist(u);
        em.getTransaction().commit();
    }
    */

    public void deleteMember(int groupId, String userNickName) {

        int userId = getUserId(userNickName);
        //int groupId = getGroupId(groupName);

        TypedQuery<UserGroup> q= em.createQuery("SELECT p FROM UserGroup p WHERE p.groupId=:groupid AND p.userId =:userid",UserGroup.class);
        List<UserGroup> listOfSpecificGroupUsers = q.setParameter("groupid", groupId)
                .setParameter("userid", userId)
                .getResultList();


        for(UserGroup ug:listOfSpecificGroupUsers) {
            em.remove(ug);

        }


    }

    private int getUserId(String userNickName) {

        User user = (User)em.createQuery("SELECT p FROM User p WHERE p.nickName=:nickname")
                .setParameter("nickname", userNickName)
                .getSingleResult();
        return user.getUserId();
    }

    private User getUser(String userNickName) {

        User user;
        try {
            user = (User) em.createQuery("SELECT p FROM User p WHERE p.nickName=:nickname")
                    .setParameter("nickname", userNickName)
                    .getSingleResult();
        }catch(NoResultException noResult) {
            logger.log(Level.WARNING, "NO RESULT IN QUERY IN GETACCESS()", noResult);
            return null;
        }

        return user;
    }

    public Access getAccess(String accessRights) {
        Access access;

        try {
            access = (Access) em.createQuery("SELECT p FROM Access p WHERE p.accessName =:accessname")
                    .setParameter("accessname", accessRights)
                    .getSingleResult();
        }catch(NoResultException noResult) {
            logger.log(Level.WARNING, "NO RESULT IN QUERY IN GETACCESS()", noResult);
            return null;
        }
        return access;
    }

    public Group getGroup(int groupId) {

        Group group = null;
        try {
            group = (Group) em.createQuery("SELECT g FROM Group g WHERE g.groupId=:groupid")
                    .setParameter("groupid", groupId)
                    .getSingleResult();
        }catch(NoResultException noResult) {
            logger.log(Level.WARNING, "NO RESULT IN QUERY IN GETGROUP()", noResult);
            return null;
        }

        return group;
    }

    public Group getGroup(String groupName) {

        Group group = null;
        try {
            group = (Group) em.createQuery("SELECT g FROM Group g WHERE g.groupName=:groupname")
                    .setParameter("groupname", groupName)
                    .getSingleResult();
        }catch(NoResultException noResult) {
            logger.log(Level.WARNING, "NO RESULT IN QUERY IN GETGROUP()", noResult);
            return null;
        }
        return group;
    }

    public int getGroupId(String groupName) {

        Group group = getGroup(groupName);
        if (group == null) {
            return -1;
        }
        return group.getGroupId();

    }

    public Group createGroup(String groupName) {

        Group g = new Group(groupName);

        em.persist(g);
        em.flush();

        return g;


    }

    public Access createAccess(String accessName) {
        Access a = new Access(accessName);

        em.persist(a);
        em.flush();

        return a;

    }

    public UserGroup createUserGroup(User u, Group g, Access a) {


/*
        Access a = (Access)em.createQuery("SELECT a FROM Access a WHERE a.accessId =:accessid")
                            .setParameter("accessid", accessId)
                            .getSingleResult();

        Group g = (Group)em.createQuery("SELECT g FROM Group g WHERE g.groupId =:groupid")
                .setParameter("groupid", groupId)
                .getSingleResult();

        User u = (User)em.createQuery("SELECT u FROM User u WHERE u.userId =:userid")
                .setParameter("userid", userId)
                .getSingleResult();*/

        UserGroup ug = new UserGroup(g, u, a);

        ug.setUserId(u.getUserId());
        ug.setGroupId(g.getGroupId());

      //  em.getTransaction().begin();
        em.persist(ug);
        //       em.flush();
      //  em.getTransaction().commit();
        return ug;

    }

    public UserGroup getUserGroup(int userId, int groupId) {

        UserGroup ug;
     try {
            ug = (UserGroup)em.createQuery("SELECT ug FROM UserGroup ug WHERE ug.userId =:userid AND ug.groupId =:groupid")
                .setParameter("userid", userId)
                .setParameter("groupid", groupId)
                .getSingleResult();
     } catch (NoResultException noResult) {
         logger.log(Level.WARNING, "NO RESULT IN QUERY IN getUserGroup()", noResult);
         return null;
     }

        return ug;
    }

    public void addUserGroupToGroupList(Group group, UserGroup userGroup) {
        if (group.getMembers() == null) {

            List<UserGroup> p = new LinkedList<>();
            p.add(userGroup);
            group.setMembers(p);
        }
        List<UserGroup> p = group.getMembers();
        p.add(userGroup);
        group.setMembers(p);
        em.persist(group);


    }

    public List<Group> getAllGroups() {

        TypedQuery<Group> q= em.createQuery("SELECT g FROM Group g ",Group.class);
        List<Group> listOfSpecificGroups= q.getResultList();

        return listOfSpecificGroups;

    }
}
