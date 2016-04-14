import org.junit.*;
import ru.javafiddle.core.ejb.*;
import ru.javafiddle.jpa.entity.*;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.io.UnsupportedEncodingException;
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
//!TODO
//    @org.junit.Test
//    public void testProjectOperations() throws UnsupportedEncodingException, NoSuchAlgorithmException, InstantiationException, IllegalAccessException {
//        UserBean userBean = null;
//        GroupBean groupBean = null;
//        ProjectBean projectBean = null;
//        HashBean hashBean = null;
//        AccessBean accessBean = null;
//        UserGroupBean userGroupBean = null;
//
//        try {
//            projectBean = (ProjectBean) context.lookup("java:global/JavaFiddle-ejb/ProjectBean");
//            userBean = (UserBean) context.lookup("java:global/JavaFiddle-ejb/UserBean");
//            groupBean = (GroupBean) context.lookup("java:global/JavaFiddle-ejb/GroupBean");
//            hashBean = (HashBean) context.lookup("java:global/JavaFiddle-ejb/HashBean");
//            accessBean = (AccessBean) context.lookup("java:global/JavaFiddle-ejb/AccessBean");
//            userGroupBean = (UserGroupBean) context.lookup("java:global/JavaFiddle-ejb/UserGroupBean");
//
//        } catch (NamingException ex) {
//            System.out.println("Unable to initialize UserBean instance: " + ex);
//        }
//        Assert.assertNotNull(userBean);
//
//
//        initialize(userBean, groupBean, accessBean, userGroupBean);
//        Project project = new Project("first_proj", null);
//        project = projectBean.createProject(1,project);
//
//        Group g = groupBean.getGroupByGroupId(1);
//
//        Assert.assertNotNull(project);
//        for(Project p: projectBean.getProjects(g)){
//            Assert.assertEquals("Not only one entity was added", "first_proj", p.getProjectName());
//        }
//
//        Project project2 = new Project("first_proj", null);
//        //project2.setHash(new Hash());
//        project2 = projectBean.createProject(1,project2);
//        Assert.assertNotNull(project2);
//        Assert.assertFalse("Hashes are identical", project.getHash().getHash().equals(project2.getHash().getHash()));
//
//        //getUserProjects()
//        User user = userBean.getUser("skotti");
//        List<String> projects = projectBean.getUserProjects(user);
//        Assert.assertNotNull("No projects", projects);
//
//        //projectBean.changeProjectName(project2.getHash().getHash(),"second_proj");
//        Project newProject = new Project("second_proj", null);
//        newProject.setHash(project2.getHash());
//        Assert.assertTrue("The name of the project was not changed", projectBean.updateProject(newProject).getProjectName().equals("second_proj"));
//        projectBean.deleteProject(project2.getHash().getHash());
//        Assert.assertNull(hashBean.getHash(2));
//        Assert.assertNull("The project was not deleted", projectBean.getProjectByProjectHash(project2.getHash().getHash()));
//
//
//
//    }

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
