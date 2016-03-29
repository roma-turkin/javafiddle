package ru.javafiddle.core.ejb;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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

    @EJB
    private UserGroupBean userGroup;

    private static final Logger logger =
            Logger.getLogger(ProjectBean.class.getName());

    @PersistenceContext(name = "JFPersistenceUnit")
    EntityManager em;

    public GroupBean(){

    }

//we always know that the group were we are trying to add a user exits now
//CALL THIS METHOD WHEN THE PERSON IS CREATING A GROUP BY HIMSELF!!

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
//    public void addMember(int groupId, String groupName,String userNickName, String accessRights) {
    public void addMember(Group group, User user, Access access) throws IllegalAccessException, InstantiationException {

        if (group.getGroupId() == -1) {
            createGroup(group);
        }

        UserGroup ug = new UserGroup(group, user, access);


        ug.setUserId(user.getUserId());
        ug.setGroupId(group.getGroupId());

        em.persist(ug);
        em.flush();
        ug = userGroup.getUserGroup(user.getUserId(), group.getGroupId());
        addUserGroupToGroupList(group, ug);


    }

    public  Map<String, String> getMemberAccessMap(int groupId){

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

        TypedQuery<UserGroup> q= em.createQuery("SELECT p FROM UserGroup p WHERE p.groupId=:groupid AND p.userId =:userid",UserGroup.class);
        List<UserGroup> userGroupsToDelete = q.setParameter("groupid", groupId)
                .setParameter("userid", userId)
                .getResultList();


        for (UserGroup ug:userGroupsToDelete) {
            em.remove(ug);

        }


    }

    private int getUserId(String userNickName) {

        User user = (User)em.createQuery("SELECT p FROM User p WHERE p.nickName=:nickname")
                .setParameter("nickname", userNickName)
                .getSingleResult();
        return user.getUserId();
    }

    public Group getGroupByGroupId(int groupId) {

        Group group = null;
        try {
            group = (Group) em.createQuery("SELECT g FROM Group g WHERE g.groupId=:groupid")
                    .setParameter("groupid", groupId)
                    .getSingleResult();
        }catch(NoResultException noResult) {
            logger.log(Level.WARNING, "No result in getGroup()", noResult);
            return null;
        }

        return group;
    }

    public Group createGroup(Group group) {

        em.persist(group);
        em.flush();

        return group;


    }


    public void addUserGroupToGroupList(Group group, UserGroup userGroup) {
        if (group.getMembers() == null) {

            List<UserGroup> p = new LinkedList<>();
            p.add(userGroup);
            group.setMembers(p);
        }

        //List<UserGroup> p = group.getMembers();
        TypedQuery<UserGroup> query =
                em.createQuery("SELECT ug FROM UserGroup ug WHERE ug.groupId =:groupid", UserGroup.class);
        List<UserGroup> p = query.setParameter("groupid", group.getGroupId()).getResultList();

        p.add(userGroup);
        group.setMembers(p);
        em.persist(em.contains(group) ? group : em.merge(group));


    }

    public List<Group> getAllGroups() {

        TypedQuery<Group> q= em.createQuery("SELECT g FROM Group g ",Group.class);
        List<Group> listOfGroups= q.getResultList();

        return listOfGroups;

    }

    public List<UserGroup> getGroupMembers(int groupId) {

        TypedQuery<UserGroup> q= em.createQuery("SELECT p FROM UserGroup p WHERE p.groupId=:groupId",UserGroup.class);
        List<UserGroup> userGroup = q.setParameter("groupId", groupId)
                .getResultList();

        return userGroup;
    }
}
