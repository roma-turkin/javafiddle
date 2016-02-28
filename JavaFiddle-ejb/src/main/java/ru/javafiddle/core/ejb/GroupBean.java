package ru.javafiddle.core.ejb;

import javax.persistence.PersistenceContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

import ru.javafiddle.jpa.entity.Access;
import ru.javafiddle.jpa.entity.UserGroup;
import ru.javafiddle.jpa.entity.Group;
import ru.javafiddle.jpa.entity.User;


@Stateless

public class GroupBean {

    @PersistenceContext
    EntityManager em;

    public GroupBean(){}

    //we should get at first userId, then add this element to UserGroups
    //when we are in this method we definitly have group with such id
    public void addMember(int groupId, String userNickName, String accessRights) {
//если указано имя группы то сначала вызвать метод метод создания а потом регать, если не указано то в дефолтную
        //check if this group exists
        Group g = getGroup(groupId, em);
        String groupName = "";
        if (g == null) {

            g = createGroup(groupName);
            em.getTransaction().begin();
            em.persist(g);
            em.getTransaction().commit();
        }
        User user = getUser(userNickName);
        Access access; //= getAccess(accessRights);


       // Group g;// = getGroup(groupId);
        UserGroup userGroup = (UserGroup)em.createQuery("SELECT us FROM UserGroup us WHERE us.group.groupId=:groupId");
        /*Group group = getGroup(groupId);
        if (group == null) {
            group = new Group();
            group.setGroupId(groupId);
            group.setGroupName("default");//still it is not clear about group names
        }
        userGroup.setAccess(access);

        em.getTransaction().begin();
        em.persist(userGroup);
        em.getTransaction().commit();*/

        //check if this group exists in list of groups
        //group = (Group)em.createQuery("SELECT g from Group g WHERE g.groupId =:groupid")
        //      .setParameter("groupid", groupId)
        //      .getSingleResult();
        //How can we get groupId from our services?


    }

    public  Map<String, String> getAllMembers(String groupId, String userNickName, String accessRights){

        TypedQuery<UserGroup> q= em.createQuery("SELECT p FROM UserGroup p JOIN Group g WHERE g.groupId=:groupid", UserGroup.class);
        List<UserGroup> usergroup = q.setParameter("groupid", groupId)
                .getResultList();


        Map<String, String> mappedUserGroup = new HashMap<String, String>(usergroup.size());
        UserGroup[] arrayOfUserGroup = (UserGroup[]) usergroup.toArray();

        for (int i = 0;i < usergroup.size(); i++) {

            UserGroup userGroup = arrayOfUserGroup[i];//how to operate with user list????
           /* String nickName = (String)em.createQuery("SELECT distinct u.nickName FROM UserGroup p JOIN User u WHERE p.userId =:userid ")
                    .setParameter("userid", userGroup.getUserId())
                    .getSingleResult();
            String access =   (String)em.createQuery("SELECT distinct a.accessName FROM UserGroup u JOIN u.access a WHERE u.userid")
                    .setParameter("userid", userGroup.getUserId())
                    .getSingleResult();
             mappedUserGroup.put(nickName, access);
*/
        }
        return mappedUserGroup;

    }


    public void updateMember(int groupId, String userNickName, String accessRights) {
//do we update usernickname also?

        int userId = getUserId(userNickName);
        User user = getUser(userNickName);

        UserGroup u = (UserGroup)em.createQuery("SELECT g FROM UserGroup g WHERE g.userId=:userid and g.groupId=:groupid")
                        .setParameter("userid", userId)
                        .setParameter("groupid", groupId)
                        .getSingleResult();
        Access a = getAccess(accessRights, em);

        u.setAccess(a);

        em.getTransaction().begin();
        em.persist(u);
        em.getTransaction().commit();
    }

    public void deleteMember(int groupId, String userNickName, EntityManager em) {

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

        User user = (User)em.createQuery("SELECT p FROM User p WHERE p.nickName=:nickname")
                .setParameter("nickname", userNickName)
                .getSingleResult();
        return user.getUserId();
    }

    private User getUser(String userNickName) {

        User user = (User)em.createQuery("SELECT p FROM User p WHERE p.nickName=:nickname")
                .setParameter("nickname", userNickName)
                .getSingleResult();

        return user;
    }

    public Access getAccess(String accessRights, EntityManager em) {

        Access access = (Access)em.createQuery("SELECT p FROM Access p WHERE p.accessName =:accessname")
                .setParameter("accessname", accessRights)
                .getSingleResult();
        return access;
    }

    public Group getGroup(int groupId, EntityManager em) {

        Group group = (Group)em.createQuery("SELECT g FROM Group g WHERE g.groupId=:groupid")
                .setParameter("groupid", groupId)
                .getSingleResult();
        return group;
    }

    public Group createGroup(String groupName) {

        Group g = new Group(groupName);

        em.getTransaction().begin();
        em.persist(g);
        em.getTransaction().commit();

        //Group gBase = getGroup(groupName, em);

        return g;

    }

    //!TODO
    public Map<String, String> getAllMembers(int groupId){
        return null;
    }
}
