import org.junit.*;
import ru.javafiddle.core.ejb.GroupBean;
import ru.javafiddle.core.ejb.HashBean;
import ru.javafiddle.core.ejb.ProjectBean;
import ru.javafiddle.core.ejb.UserBean;
import ru.javafiddle.jpa.entity.*;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
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

    @org.junit.Test
    public void testProjectOperations() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        UserBean userBean = null;
        GroupBean groupBean = null;
        ProjectBean projectBean = null;
        HashBean hashBean = null;

        try {
            projectBean = (ProjectBean) context.lookup("java:global/JavaFiddle-ejb/ProjectBean");
            userBean = (UserBean) context.lookup("java:global/JavaFiddle-ejb/UserBean");
            groupBean = (GroupBean) context.lookup("java:global/JavaFiddle-ejb/GroupBean");
            hashBean = (HashBean) context.lookup("java:global/JavaFiddle-ejb/HashBean");

        } catch (NamingException ex) {
            System.out.println("Unable to initialize UserBean instance: " + ex);
        }
        Assert.assertNotNull(userBean);


        initialize(userBean, groupBean);
        Project project = projectBean.createProject(1,"","first_proj");

        Group g = groupBean.getGroup("default");

        Assert.assertNotNull(project);
        for(Project p: g.getProjects()){
            Assert.assertEquals("Not only one entity was added", "first_proj", p.getProjectName());
        }

        Project project2 = projectBean.createProject(1,project.getHash().getHash(),"first_proj");
        Assert.assertNotNull(project2);
        Assert.assertFalse("Hashe are identical", project.getHash().getHash().equals(project2.getHash().getHash()));

        //projectBean.changeProjectName(project2.getHash().getHash(),"second_proj");
        Assert.assertTrue("The name of the project was not changed", projectBean.updateProject(project2.getHash().getHash(),"second_proj").getProjectName().equals("second_proj"));
        projectBean.deleteProject(project2.getHash().getHash());
        Assert.assertNull(hashBean.getHash(2));
        Assert.assertNull("The project was not deleted", projectBean.getProjectByProjectHash(project2.getHash().getHash()));



    }

    private void initialize(UserBean userBean, GroupBean groupBean) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        User uu = userBean.register("Nastia", "Ruzh", "skotti", "aa", "12345");
        userBean.register("Vania", "Truf", "vivi", "fff", "hvoehvfknd");
        Access a = groupBean.createAccess("full");
        Group g = groupBean.createGroup("default");

        groupBean.createUserGroup(uu,g,a);
        User uu1 = userBean.register("Bar", "Stins", "barny", "aa", "123gg5");
        groupBean.createUserGroup(uu1,g,a);


    }




}
