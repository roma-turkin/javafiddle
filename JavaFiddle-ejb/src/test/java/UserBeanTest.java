import static org.junit.Assert.*;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.platform.database.SybasePlatform;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.javafiddle.core.ejb.*;
import ru.javafiddle.jpa.entity.*;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserBeanTest {

    private static final Logger logger =
            Logger.getLogger(ProjectBean.class.getName());

    EJBContainer ejbContainer;
    Context context;
    @Before
    public void setUp() throws NamingException, IOException {

        Path sourcePath      = Paths.get(System.getProperty("user.dir")+"/src/test/resources/META-INF/persistence.xml");
        Path destinationPath = Paths.get(System.getProperty("user.dir")+"/src/main/resources/META-INF/persistence.xml");
        Path destinationPath2 = Paths.get(System.getProperty("user.dir")+"/target/classes//META-INF/persistence.xml");
      //  Files.copy(new File("/Users/mac/javaff/javafiddle/JavaFiddle-ejb/target/test-classes/META-INF/persistence.xml").toPath(), new File("/Users/mac/javaff/javafiddle/JavaFiddle-ejb/target/classes/META-INF/persistence.xml").toPath());
     //   Map<String, Object> properties = new HashMap<String, Object>();
        //properties.put(EJBContainer.MODULES, new File("target/classes");
       // properties.put("org.glassfish.ejb.embedded.glassfish.installation.root",
       //         "/Users/mac/glassfish4/glassfish");
        File temp = new File(System.getProperty("user.dir")+"/src/main/resources/META-INF/persistence.xml");
        File temp2 = new File(System.getProperty("user.dir")+"/target/classes/META-INF/persistence.xml");

        if(temp.exists() || temp2.exists()) {
            temp.delete();
            temp2.delete();
        }

        Files.copy(sourcePath, destinationPath);
        Files.copy(sourcePath, destinationPath2);
        Properties properties = new Properties();
        properties.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.openejb.client.LocalInitialContextFactory");
        properties.setProperty("openejb.altdd.prefix", "test");


        //InitialContext initialContext = new InitialContext(properties);
        ejbContainer = EJBContainer.createEJBContainer();
      //  ejbContainer = EJBContainer.createEJBContainer();
        System.out.println("Test EJBContainer is created");
        context = ejbContainer.getContext();
    }

    @After
    public void tearDown() throws IOException {
        ejbContainer.close();
        Path sourcePath      = Paths.get(System.getProperty("user.dir")+"/src/main/resources/META-INF/persistence_test.xml");
        Path destinationPath = Paths.get(System.getProperty("user.dir")+"/src/main/resources/META-INF/persistence.xml");
        Path destinationPath2 = Paths.get(System.getProperty("user.dir")+"/target/classes//META-INF/persistence.xml");


        File temp = new File(System.getProperty("user.dir")+"/src/main/resources/META-INF/persistence.xml");
        File temp2 = new File(System.getProperty("user.dir")+"/target/classes/META-INF/persistence.xml");

        if(temp.exists() || temp2.exists()) {
            temp.delete();
            temp2.delete();
        }

        Files.copy(sourcePath, destinationPath);
        Files.copy(sourcePath, destinationPath2);
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
            userBean = (UserBean) context.lookup("java:global/classes/UserBean");
            groupBean = (GroupBean) context.lookup("java:global/classes/GroupBean");
            projectBean = (ProjectBean) context.lookup("java:global/classes/ProjectBean");
            accessBean = (AccessBean) context.lookup("java:global/classes/AccessBean");
            userGroupBean = (UserGroupBean) context.lookup("java:global/classes/UserGroupBean");
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
        logger.log(Level.INFO,"group id is"+ uug.getGroupId());
        logger.log(Level.INFO,"user id is"+ uug.getUserId());
        logger.log(Level.INFO,"nickname of user is"+ uug.getMember().getNickName());

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

        userGroupBean.createUserGroup(uu2,g,a);

    }



}
