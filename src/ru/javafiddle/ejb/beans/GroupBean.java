package ru.javafiddle.ejb.beans;

import javax.persistence.PersistenceContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;

@Stateless
@Named(value = "groupBean")
public class GroupBean {

    @PersistenceContext(unitName = "")
    EntityManager em;

    GroupBean(){};

    //we should get at first userId, then add this element to UserGroups
    public void addMember(int groupId, String userNickName, String accessRights) {

        int userId = getUserId(userNickName);
        int accessId = getAcccessId(accessRights);


        UserGroup userGroup = new UserGroup();
        Group group = new Group();

        userGroup.setUserId(userId);
        userGroup.setGroupId(groupId);
        userGroup.setAccessId(accessId);

        em.getTransaction().begin();
        em.persist(userGroup);
        em.getTransaction().commit();

    }

    public  getAllMembers(String groupId, String userNickName, String accessRights){

//we should think how to compile data from two structures        

    }


    public void updateMember(int groupId, String userNickName, String accessRights) {


        int userId = getUserId(userNickName);
        User user = getUser(userNickName);

        UserGroup usergroup = (UserGroup)em.createQuery("SELECT p FROM UserGroup p WHERE p.userId =:userid and p.groupId =:groupid")
                .setParameter("userid", userId)
                .setParameter("groupid", groupId)
                .getSingleResult();

        usergroup.setAccessRights(accessRights);
        user.setNickName(userNickName);


    }

    public void deleteMember(int groupId, String userNickName) {

        int userId = getUserId(userNickName);

        UserGroup usergroup = (UserGroup)em.createQuery("SELECT p FROM UserGroup p WHERE p.userId =:userid and p.groupId =:groupid")
                                            .setParameter("userid", userId)
                                            .setParameter("groupid", groupId)
                                            .getSingleResult();

        em.getTransaction().begin();
        em.remove(userGroup);
        em.getTransaction().commit();
    }

    private int getUserId(String userNickName) {

        User user = (User)em.createQuery("SELECT p FROM User p WHERE p.nickName =:nickname")
                            .setParameter("nickName", nickName)
                            .getSingleResult();
        return user.getUserId();
    }

    private User getUser(String userNickName) {

        User user = (User)em.createQuery("SELECT p FROM User p WHERE p.nickName =:nickname")
                .setParameter("nickName", nickName)
                .getSingleResult();

        return user;
    }

    private int getAccessId(String accessRights) {

        Access access = (Access)em.createQuery("SELECT p FROM Access p WHERE p.accessName =:accessrights")
                                  .setParameter("accessrights", accessRights)
                                  .getSingleResult();
        return access.getAccessId();
    }
}
