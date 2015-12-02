package main.java.ru.javafiddle.core.ejb;

import javax.persistence.PersistenceContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.inject.Named;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import ru.javafiddle.jpa.entity.UserGroup;
import ru.javafiddle.jpa.entity.Group;
import ru.javafiddle.jpa.entity.User;
import ru.javafiddle.jpa.entity.Access;

@Stateless
@Named(value = "groupBean")
public class GroupBean {

    @PersistenceContext
    EntityManager em;

    GroupBean(){};

    //we should get at first userId, then add this element to UserGroups
    public void addMember(int groupId, String userNickName, String accessRights) {

        int userId = getUserId(userNickName);
        int accessId = getAccessId(accessRights);


        UserGroup userGroup = new UserGroup();
        Group group;

        userGroup.setUserId(userId);
        userGroup.setGroupId(groupId);
        userGroup.setAccessId(accessId);

        em.getTransaction().begin();
        em.persist(userGroup);
        em.getTransaction().commit();

        //check if this group exists in list of groups
        group = (Group)em.createQuery("SELECT g from Group g WHERE g.groupId =:groupid")
                .setParameter("groupid", groupId)
                .getSingleResult();
        //How can we get groupId from our services??
        if (group == null) {
            group = new Group();
            group.setGroupId(groupId);
            group.setGroupName("default");//still it is not clear about group names
        }


    }

    public  Map<String, String> getAllMembers(String groupId, String userNickName, String accessRights){
        List<UserGroup> usergroup;

        List<UserGroup> usergroup = (LinkedList<UserGroup>)em.createQuery("SELECT p FROM UserGroup p WHERE p.userId =:groupid")
                .setParameter("groupid", groupId)
                .getResultList();

        Map<String, String> mappedUserGroup = new HashMap<String, String>(usergroup.size());
        UserGroup[] arrayOfUserGroup = usergroup.toArray();

        for (int i = 0;i < usergroup.size(); i++) {

            UserGroup userGroup = arrayOfUserGroup[i];

            String nickName = (String)em.createQuery("SELECT distinct u.nickName FROM UserGroup p JOIN p.User u WHERE p.userId =:userid ")
                    .setParameter("userid", userGroup.getUserId())
                    .getSingleResult();
            String access =   (String)em.createQuery("SELECT distinct a.accessName FROM UserGroup u JOIN u.Access a WHERE u.userid")
                    .setParameter("userid", userGroup.getUserId())
                    .getSingleResult();

            mappedUserGroup.put(nickName, access);

        }
        return mappedUserGroup;

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

        List<UserGroup> listOfSpecificGroupUsers = (LinkedList<UserGroup>)em.createQuery("SELECT p FROM UserGroup p WHERE p.groupId =:groupid")
                .setParameter("groupid", groupId)
                .getResultList();

        if (listOfSpecificGroupUsers.isEmpty()) {

            Group group = (Group)em.createQuery("SELECT g FROM Group g WHERE g.groupId =:groupid")
                    .setParameter("groupid", groupId)
                    .getSingleResult();
            em.getTransaction().begin();
            em.remove(group);
            em.getTransaction().commit();
        }

        em.getTransaction().begin();
        em.remove(usergroup);
        em.getTransaction().commit();
    }

    private int getUserId(String userNickName) {

        User user = (User)em.createQuery("SELECT p FROM User p WHERE p.nickName =:nickname")
                .setParameter("nickName", userNickName)
                .getSingleResult();
        return user.getUserId();
    }

    private User getUser(String userNickName) {

        User user = (User)em.createQuery("SELECT p FROM User p WHERE p.nickName =:nickname")
                .setParameter("nickName", userNickName)
                .getSingleResult();

        return user;
    }

    private int getAccessId(String accessRights) {

        Integer accessId = (Integer)em.createQuery("SELECT p.accessId FROM Access p WHERE p.accessName =:accessrights")
                .setParameter("accessrights", accessRights)
                .getSingleResult();
        return accessId;
    }
}
