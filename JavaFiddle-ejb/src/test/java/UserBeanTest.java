import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.javafiddle.core.ejb.*;
import ru.javafiddle.jpa.entity.*;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class UserBeanTest {

    EJBContainer ejbContainer;
    Context context;
    @Before
    public void setUp() {
        Map<String, Object> properties = new HashMap<String, Object>();
        //properties.put(EJBContainer.MODULES, new File("target/classes");
        properties.put("org.glassfish.ejb.embedded.glassfish.installation.root",
                "/Users/mac/glassfish4/glassfish");
        ejbContainer = EJBContainer.createEJBContainer();
        System.out.println("Test EJBContainer is created");
        context = ejbContainer.getContext();
    }

    @After
    public void tearDown() {
        ejbContainer.close();
        System.out.println("Test EJBContainer is closed" );
    }

    @Test
    public void testUserOperations() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        UserBean userBean = null;
        GroupBean groupBean = null;
        ProjectBean projectBean = null;
        AccessBean accessBean = null;
        UserGroupBean userGroupBean = null;

        try {
            userBean = (UserBean) context.lookup("java:global/JavaFiddle-ejb/UserBean");
            groupBean = (GroupBean) context.lookup("java:global/JavaFiddle-ejb/GroupBean");
            projectBean = (ProjectBean) context.lookup("java:global/JavaFiddle-ejb/ProjectBean");
            accessBean = (AccessBean) context.lookup("java:global/JavaFiddle-ejb/AccessBean");
            userGroupBean = (UserGroupBean) context.lookup("java:global/JavaFiddle-ejb/UserGroupBean");
        } catch (NamingException ex) {
            System.out.println("Unable to initialize UserBean instance: " + ex);
        }
        Assert.assertNotNull(userBean);



        initialize(userBean, groupBean, projectBean, accessBean, userGroupBean);
        //1) Check user adding-------------------------------------------------------------------
        User u = userBean.getUser("skotti");
        //projectBean.createCopy();

       // Assert.assertNotNull("NO USER", userBean.updateUser(u, "afa"));
        Assert.assertNotNull("NO USER", u);
        Assert.assertEquals("NOT EXPECTED USER", "skotti",u.getNickName());
        //2) Check usergroup  adding-------------------------------------------------------------
        UserGroup uug = userGroupBean.getUserGroup(1,1);
        Assert.assertNotNull(uug);

        //3) Info printing
        System.out.println(uug.getGroupId());
        System.out.println(u.getFirstName());

        //4)Updating user information
        User newUser = new User( "Anastasia", "Ruzh", "skotti", "aa", "12345", null, null);
        userBean.updateUser(newUser);
        Assert.assertEquals("SET USER DIDN'T CHANGE INFO CORRECTLY","Anastasia",userBean.getUser("skotti").getFirstName());
        newUser = new User( "Nastia", "Ruzh", "skotti", "aa", "12345", null, null);
        userBean.updateUser(newUser);
        Assert.assertEquals("SET USER DIDN'T CHANGE INFO CORRECTLY","Nastia", userBean.getUser("skotti").getFirstName());


        User oldUser = userBean.getUser("skotti");
        userBean.deleteUser(oldUser);
        Assert.assertNull("THE OBJECT WASN'T DELETED",userBean.getUser("skotti"));

    }

    private void initialize(UserBean userBean, GroupBean groupBean, ProjectBean projectBean, AccessBean accessBean, UserGroupBean userGroupBean) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        User user1 = new User("Nastia", "Ruzh", "skotti", "aa", "12345", null, null);
        User uu = userBean.register(user1);
        User user2 = new User("Vania", "Truf", "vivi", "fff", "hvoehvfknd", null, null);
        User uu1 = userBean.register(user2);
        Group group = new Group("default");
        Group g = groupBean.createGroup(group);
        Access access = new Access("full");
        Access a = accessBean.createAccess(access);
        UserGroup userGroup = new UserGroup(g,uu,a);
        userGroupBean.createUserGroup(uu,g,a);
        User user3 = new User("Bar", "Stins", "barny", "aa", "123gg5", null, null);
        User uu2 = userBean.register(user3);
      //  User uu1 = userBean.register("Bar", "Stins", "barny", "aa", "123gg5");
        userGroupBean.createUserGroup(uu2,g,a);

       // Project p = projectBean.createProject(1, "", "firstproj");

    }



}