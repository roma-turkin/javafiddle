import org.junit.*;
import org.junit.Test;
import ru.javafiddle.core.ejb.GroupBean;
import ru.javafiddle.core.ejb.ProjectBean;
import ru.javafiddle.core.ejb.UserBean;
import ru.javafiddle.jpa.entity.*;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mac on 14.03.16.
 */
public class test2 {


    private static final Logger logger =
            Logger.getLogger(ProjectBean.class.getName());

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
//Some small functions are used in these three or four main ones.

    @Test
    public void testGroupOperations()  throws UnsupportedEncodingException, NoSuchAlgorithmException{

        GroupBean groupBean = null;
        ProjectBean projectBean = null;
        UserBean userBean = null;
        try {
            groupBean = (GroupBean) context.lookup("java:global/JavaFiddle-ejb/GroupBean");
            projectBean = (ProjectBean) context.lookup("java:global/JavaFiddle-ejb/ProjectBean");
            userBean = (UserBean) context.lookup("java:global/JavaFiddle-ejb/UserBean");
        } catch (NamingException ex) {
            System.out.println("Unable to initialize UserBean instance: " + ex);
        }



        initializeAndCreate(groupBean, projectBean,userBean);
        int groupId = groupBean.getGroup("extended").getGroupId();
//added member-----------------------------------------------------------
        groupBean.addMember(1,"extended","barny","partial");
        groupBean.addMember(1,"extended","uollis","full");
//check if member was added----------------------------------------------
        Group group = groupBean.getGroup("extended");
        Assert.assertNotNull(group);
        System.out.println(group.getGroupName());
        List<UserGroup> gr = group.getMembers();

        Assert.assertNotNull("GET MEMBERS RETURNED NULL",gr);
        Assert.assertFalse("THE LIST OF MEMBERS IS EMPTY", gr.isEmpty());
        if (gr == null || gr.isEmpty()) {

            logger.log(Level.SEVERE, "NO ENTITY WAS ADDED");
            System.exit(-1);

        }
        for (UserGroup ug:gr) {
            System.out.println(ug.getMember().getFirstName());
        }


//check getAllMembers()----------------------------------------------------------------
        Assert.assertEquals("NOT ENOUGH MEMBERS IN MAP", 2,groupBean.getAllMembers(groupId).size());
        for (Map.Entry<String, String> entry : groupBean.getAllMembers(groupId).entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }



//Delete operation--------------------------------------------------------
        groupBean.deleteMember(groupId,"barny");
        groupBean.deleteMember(groupId, "uollis");
        //check if was deleted
        group = groupBean.getGroup("extended");
        gr = group.getMembers();
        Assert.assertTrue("ERROR", gr.isEmpty());

    }

    public void initializeAndCreate(GroupBean groupBean, ProjectBean projectBean, UserBean userBean) {

        Group g = groupBean.createGroup("extended");
        User uu = userBean.register("Oleg", "Ruzh", "barny", "aa", "gfhkfo");
        uu = userBean.register("Vadim", "Vet", "uollis", "gg", "frewscfs");
        Access a = groupBean.createAccess("partial");
        a = groupBean.createAccess("full");
    }
}

