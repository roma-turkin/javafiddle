import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.javafiddle.core.ejb.GroupBean;
import ru.javafiddle.core.ejb.ProjectBean;
import ru.javafiddle.core.ejb.UserBean;
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

        try {
            userBean = (UserBean) context.lookup("java:global/JavaFiddle-ejb/UserBean");
            groupBean = (GroupBean) context.lookup("java:global/JavaFiddle-ejb/GroupBean");
            projectBean = (ProjectBean) context.lookup("java:global/JavaFiddle-ejb/ProjectBean");
        } catch (NamingException ex) {
            System.out.println("Unable to initialize UserBean instance: " + ex);
        }
        Assert.assertNotNull(userBean);



        initialize(userBean, groupBean, projectBean);
        //1) Check user adding-------------------------------------------------------------------
        User u = userBean.getUser("skotti");
        //projectBean.createCopy();

       // Assert.assertNotNull("NO USER", userBean.updateUser(u, "afa"));
        Assert.assertNotNull("NO USER", u);
        Assert.assertEquals("NOT EXPECTED USER", "skotti",u.getNickName());
        //2) Check usergroup  adding-------------------------------------------------------------
        UserGroup uug = groupBean.getUserGroup(1,1);
        Assert.assertNotNull(uug);

        //3) Info printing
        System.out.println(uug.getGroupId());
        System.out.println(u.getFirstName());

        /* Separate scenario
        userBean.deleteUser("skotti");
        u = userBean.getUser("skotti");
        uug = groupBean.getUserGroup(1,1);
        if (u == null && uug == null ) {
            System.out.println("SUCCESS");
        }*/
        //4)Updating user information
        userBean.setUser("skotti", "Anastasia", "Ruzh", "skot", "aa", "12345");
        Assert.assertNotNull("SET USER DIDN'T CHANGE INFO CORRECTLY",userBean.getUser("skot"));
        userBean.setUser("skot", "Nastia", "Ruzh", "skotti", "aa", "12345");
        Assert.assertNotNull("SET USER DIDN'T CHANGE INFO CORRECTLY",userBean.getUser("skotti"));


       // Project p = projectBean.getProject("firstproj","default");
       // Assert.assertNotNull("NO SUCH PROJECT",p);
       // System.out.println(p.getProjectName());

        userBean.deleteUser("skotti");
        Assert.assertNull("THE OBJECT WASN'T DELETED",userBean.getUser("skotti"));

    }

    private void initialize(UserBean userBean, GroupBean groupBean, ProjectBean projectBean) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        User uu = userBean.register("Nastia", "Ruzh", "skotti", "aa", "12345");
        userBean.register("Vania", "Truf", "vivi", "fff", "hvoehvfknd");
        Group g = groupBean.createGroup("default");
        Access a = groupBean.createAccess("full");
        groupBean.createUserGroup(uu,g,a);
        User uu1 = userBean.register("Bar", "Stins", "barny", "aa", "123gg5");
        groupBean.createUserGroup(uu1,g,a);

       // Project p = projectBean.createProject("firstproj", "default");

    }



}