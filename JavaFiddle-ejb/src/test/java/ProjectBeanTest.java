import org.junit.*;
import ru.javafiddle.core.ejb.*;
import ru.javafiddle.jpa.entity.*;
import ru.javafiddle.jpa.entity.File;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mac on 16.03.16.
 */
public class ProjectBeanTest {


    EJBContainer ejbContainer;
    Context context;
    @Before
    public void setUp() throws IOException {
        Path sourcePath      = Paths.get(System.getProperty("user.dir")+"/src/test/resources/META-INF/persistence.xml");
        Path destinationPath = Paths.get(System.getProperty("user.dir")+"/src/main/resources/META-INF/persistence.xml");
        Path destinationPath2 = Paths.get(System.getProperty("user.dir")+"/target/classes//META-INF/persistence.xml");

        java.io.File temp = new java.io.File(System.getProperty("user.dir")+"/src/main/resources/META-INF/persistence.xml");
        java.io.File temp2 = new java.io.File(System.getProperty("user.dir")+"/target/classes/META-INF/persistence.xml");

        if(temp.exists() || temp2.exists()) {
            temp.delete();
            temp2.delete();
        }

        Files.copy(sourcePath, destinationPath);
        Files.copy(sourcePath, destinationPath2);
        Map<String, Object> properties = new HashMap<String, Object>();
        //properties.put(EJBContainer.MODULES, new File("target/classes");
        properties.put("org.glassfish.ejb.embedded.glassfish.installation.root",
                "/Users/mac/glassfish4/glassfish");
        ejbContainer = EJBContainer.createEJBContainer();
        System.out.println("Test EJBContainer is created");

        context = ejbContainer.getContext();
    }

    @After
    public void tearDown() throws IOException {
        ejbContainer.close();
        Path sourcePath      = Paths.get(System.getProperty("user.dir")+"/src/main/resources/META-INF/test.persistence.xml");
        Path destinationPath = Paths.get(System.getProperty("user.dir")+"/src/main/resources/META-INF/persistence.xml");
        Path destinationPath2 = Paths.get(System.getProperty("user.dir")+"/target/classes//META-INF/persistence.xml");
        java.io.File temp = new java.io.File(System.getProperty("user.dir")+"/src/main/resources/META-INF/persistence.xml");
        java.io.File temp2 = new java.io.File(System.getProperty("user.dir")+"/target/classes/META-INF/persistence.xml");
        if(temp.exists() || temp2.exists()) {
            temp.delete();
            temp2.delete();
        }
        Files.copy(sourcePath, destinationPath);
        Files.copy(sourcePath, destinationPath2);
        System.out.println("Test EJBContainer is closed" );
    }


    @org.junit.Test
    public void testProjectOperations() throws UnsupportedEncodingException, NoSuchAlgorithmException, InstantiationException, IllegalAccessException {
        UserBean userBean = null;
        GroupBean groupBean = null;
        ProjectBean projectBean = null;
        HashBean hashBean = null;
        AccessBean accessBean = null;
        UserGroupBean userGroupBean = null;

       try {
            projectBean = (ProjectBean) context.lookup("java:global/classes/ProjectBean");
            userBean = (UserBean) context.lookup("java:global/classes/UserBean");
             groupBean = (GroupBean) context.lookup("java:global/classes/GroupBean");
            hashBean = (HashBean) context.lookup("java:global/classes/HashBean");
            accessBean = (AccessBean) context.lookup("java:global/classes/AccessBean");
            userGroupBean = (UserGroupBean) context.lookup("java:global/classes/UserGroupBean");

        } catch (NamingException ex) {
            System.out.println("Unable to initialize UserBean instance: " + ex);
        }
        Assert.assertNotNull(userBean);


        initialize(userBean, groupBean, accessBean, userGroupBean);
        Group g = groupBean.getGroupByGroupId(1);
        Project project = new Project("first_proj", null);
        project.setGroup(g);
        project = projectBean.createProject(project);



        Assert.assertNotNull(project);
        for(Project p: projectBean.getProjects(g)){
            Assert.assertEquals("Not only one entity was added", "first_proj", p.getProjectName());
        }

        Project project2 = new Project("first_proj", null);
        project2.setGroup(g);
        //project2.setHash(new Hash());
         project2 = projectBean.createProject(project2);
        Assert.assertNotNull(project2);
        Assert.assertFalse("Hashes are identical", project.getHash().getHash().equals(project2.getHash().getHash()));

        //getUserProjects()
        User user = userBean.getUser("skotti");
        List<String> projects = projectBean.getUserProjects(user);
        Assert.assertNotNull("No projects", projects);

        //projectBean.changeProjectName(project2.getHash().getHash(),"second_proj");
        Project newProject = new Project("second_proj", null);
        newProject.setHash(project2.getHash());
        Assert.assertTrue("The name of the project was not changed", projectBean.updateProject(newProject).getProjectName().equals("second_proj"));
        projectBean.deleteProject(project2.getHash().getHash());
        Assert.assertNull(hashBean.getHash(2));
        Assert.assertNull("The project was not deleted", projectBean.getProjectByProjectHash(project2.getHash().getHash()));



    }

    private void initialize(UserBean userBean, GroupBean groupBean, AccessBean accessBean, UserGroupBean userGroupBean) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        User user1 = new User("Nastia", "Ruzh", "skotti", "aa", "12345", null, null);
        User uu = userBean.register(user1);
        User user2 = new User("Vania", "Truf", "vivi", "fff", "hvoehvfknd", null, null);
        User uu2 = userBean.register(user2);

        Access access = new Access("full");
        Access a = accessBean.createAccess(access);

        Group group = new Group("default");
        Group g = groupBean.createGroup(group);


        userGroupBean.createUserGroup(uu,g,a);

        User user3 = new User("Bar", "Stins", "barny", "aa", "123gg5", null, null);
        User uu3 = userBean.register(user3);
        userGroupBean.createUserGroup(uu3,g,a);


    }




}
