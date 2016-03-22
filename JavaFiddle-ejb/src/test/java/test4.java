import org.junit.*;
import ru.javafiddle.core.ejb.FileBean;
import ru.javafiddle.core.ejb.GroupBean;
import ru.javafiddle.core.ejb.ProjectBean;
import ru.javafiddle.core.ejb.UserBean;
import ru.javafiddle.jpa.entity.*;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.sound.midi.SysexMessage;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mac on 21.03.16.
 */
public class test4 {

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
    public void testUserOperations() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        UserBean userBean = null;
        GroupBean groupBean = null;
        ProjectBean projectBean = null;
        FileBean fileBean = null;

        try {
            userBean = (UserBean) context.lookup("java:global/JavaFiddle-ejb/UserBean");
            groupBean = (GroupBean) context.lookup("java:global/JavaFiddle-ejb/GroupBean");
            projectBean = (ProjectBean) context.lookup("java:global/JavaFiddle-ejb/ProjectBean");
            fileBean = (FileBean) context.lookup("java:global/JavaFiddle-ejb/FileBean");
        } catch (NamingException ex) {
            System.out.println("Unable to initialize UserBean instance: " + ex);
        }



        Access a = groupBean.createAccess("full");
        Group g = groupBean.createGroup("default");
        Type t = fileBean.createType("java");
       // initialize(userBean, groupBean, projectBean);
        Project project = projectBean.createProject(1,"","first_proj");
        Assert.assertTrue("WRONG LIST SIZE",projectBean.getProject("48").getFiles().size() == 0);
        byte[] myvar = "Any String you want".getBytes();
        System.out.println(project.getHash().getHash());
        fileBean.addFile(project.getHash().getHash(),"ff",myvar,"java","/g/g");
        File f = fileBean.getFile(1);
        File file = fileBean.getFile(f.getFileId());
        Assert.assertEquals("GOT WRONG NAME","ff", file.getFileName());
        Assert.assertEquals("WRONG LIST SIZE",1,projectBean.getProject("48").getFiles().size());

        Assert.assertEquals("NAME WAS NOT CHANGED", fileBean.updateFile("48",1,"ff2",myvar,"java","/g/g").getFileName(), "ff2");
        List<File> ff = fileBean.getType("java").getFiles();

        for (File curFile:ff) {
            System.out.println(curFile.getFileName());
        }

        fileBean.deleteFile("48",1);
        List<File> gotfiles = fileBean.getType("java").getFiles();
        for (File curFile1:gotfiles) {
            System.out.println("----"+curFile1.getFileName());
        }
//        Assert.assertNull("THE OBJECT WAS NOT NULL", projectBean.getProject("48"));
        Assert.assertNull("THE OBJECT WAS NOT NULL", fileBean.getFile(1));

        Assert.assertEquals("FILE WAS NOT DELETED", fileBean.getType("java").getFiles().size(),0);
        System.out.println(fileBean.getType("java").getFiles().size());
       // Assert.assertEquals("FILE WAS NOT DELETED", fileBean.getProjectFiles("48").size() == 0);




    }

    private void initialize(UserBean userBean, GroupBean groupBean, ProjectBean projectBean) throws UnsupportedEncodingException, NoSuchAlgorithmException {




    }


}
