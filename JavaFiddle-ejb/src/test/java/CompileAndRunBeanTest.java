import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import ru.javafiddle.core.ejb.*;
import ru.javafiddle.jpa.entity.*;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nk on 16.04.2016.
 */

public class CompileAndRunBeanTest {

    private static final String DEFAULT_USER_STATUS = "registered";
    EJBContainer ejbContainer;
    Context context;

    private static final Logger logger =
            Logger.getLogger(ProjectBean.class.getName());

    @Before
    public void setUp() throws IOException {
        Path sourcePath = Paths.get(System.getProperty("user.dir") + "/src/test/resources/META-INF/persistence.xml");
        Path destinationPath = Paths.get(System.getProperty("user.dir") + "/src/main/resources/META-INF/persistence.xml");
        Path destinationPath2 = Paths.get(System.getProperty("user.dir") + "/target/classes//META-INF/persistence.xml");

        java.io.File temp = new java.io.File(System.getProperty("user.dir") + "/src/main/resources/META-INF/persistence.xml");
        java.io.File temp2 = new java.io.File(System.getProperty("user.dir") + "/target/classes/META-INF/persistence.xml");

        if (temp.exists() || temp2.exists()) {
            temp.delete();
            temp2.delete();
        }

        Files.copy(sourcePath, destinationPath);
        Files.copy(sourcePath, destinationPath2);
        Map<String, Object> properties = new HashMap<String, Object>();
//        properties.put(EJBContainer.MODULES, new File("target/classes"));
        properties.put("org.glassfish.ejb.embedded.glassfish.installation.root",
                "C:\\GlassFish4.0\\glassfish4\\glassfish");
        ejbContainer = EJBContainer.createEJBContainer();
        System.out.println("Test EJBContainer is created");
        context = ejbContainer.getContext();
    }

    @After
    public void tearDown() throws IOException {
        ejbContainer.close();
        Path sourcePath = Paths.get(System.getProperty("user.dir") + "/src/main/resources/META-INF/persistence_test.xml");
        Path destinationPath = Paths.get(System.getProperty("user.dir") + "/src/main/resources/META-INF/persistence.xml");
        Path destinationPath2 = Paths.get(System.getProperty("user.dir") + "/target/classes//META-INF/persistence.xml");
        java.io.File temp = new java.io.File(System.getProperty("user.dir") + "/src/main/resources/META-INF/persistence.xml");
        java.io.File temp2 = new java.io.File(System.getProperty("user.dir") + "/target/classes/META-INF/persistence.xml");
        if (temp.exists() || temp2.exists()) {
            temp.delete();
            temp2.delete();
        }
        Files.copy(sourcePath, destinationPath);
        Files.copy(sourcePath, destinationPath2);
        System.out.println("Test EJBContainer is closed");
    }

    @org.junit.Test
    public void testCompileAndRunOperations() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        CompileAndRunBean compileAndRunBean = null;
        FileBean fileBean = null;
        ProjectBean projectBean = null;
        GroupBean groupBean = null;
        UserBean userBean = null;
        AccessBean accessBean = null;
        TypeBean typeBean = null;
        UserGroupBean userGroupBean = null;

        try {
            compileAndRunBean = (CompileAndRunBean) context.lookup("java:global/classes/CompileAndRunBean");
            fileBean = (FileBean) context.lookup("java:global/classes/FileBean");
            projectBean = (ProjectBean) context.lookup("java:global/classes/ProjectBean");
            groupBean = (GroupBean) context.lookup("java:global/classes/GroupBean");
            userBean = (UserBean) context.lookup("java:global/classes/UserBean");
            accessBean = (AccessBean) context.lookup("java:global/classes/AccessBean");
            typeBean = (TypeBean) context.lookup("java:global/classes/TypeBean");
            userGroupBean = (UserGroupBean) context.lookup("java:global/classes/UserGroupBean");
        } catch (NamingException ex) {
            System.out.println("Unable to initialize UserBean instance: " + ex);
        }

        Assert.assertNotNull(userBean);
        Assert.assertNotNull(compileAndRunBean);
        Assert.assertNotNull(fileBean);
        Assert.assertNotNull(projectBean);
        Assert.assertNotNull(groupBean);
        Assert.assertNotNull(accessBean);
        Assert.assertNotNull(typeBean);
        Assert.assertNotNull(userGroupBean);

        initialization(userGroupBean, groupBean, userBean, accessBean);
        initTypes(typeBean);
        Group g = groupBean.getGroupByGroupId(1);
        Assert.assertNotNull("NO GROUP", g);

        Project p = initFilesHelloWorld(projectBean, typeBean, fileBean, g);
        String resultHello = "";
        try {
            logger.log(Level.INFO, "Result of compilation: " + compileAndRunBean.compile(p.getHash().getHash()));
            resultHello = compileAndRunBean.run(p.getHash().getHash());
            logger.log(Level.INFO, "Result of running: " + resultHello);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String res = "hello\n" +
                "HELLO WORLD\n";
//        Assert.assertEquals(resultHello, res);

        Project proj = initEnumCars(projectBean, typeBean, fileBean, g);
        String resEn = "All car prices:\n" +
                "lamborghini costs 900 thousand dollars.\n" +
                "tata costs 2 thousand dollars.\n" +
                "audi costs 50 thousand dollars.\n" +
                "fiat costs 15 thousand dollars.\n" +
                "honda costs 12 thousand dollars.\n";
        String resultEn = "";
        try {
            logger.log(Level.INFO, "Result of compilation: " + compileAndRunBean.compile(proj.getHash().getHash()));
            resultEn = compileAndRunBean.run(proj.getHash().getHash());
            logger.log(Level.INFO, "Result of running: " + resultEn);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Assert.assertSame(resultEn, resEn);
    }

    public void initialization(UserGroupBean userGroupBean, GroupBean groupBean, UserBean userBean, AccessBean accessBean) {
        User user1 = new User("ark", "ark", "ark", "ark", "12345", null, null);
        User uu = userBean.register(user1);
        logger.log(Level.INFO, "nickname of user is " + uu.getNickName());
        Access access = new Access("full");
        Access a = accessBean.createAccess(access);
        logger.log(Level.INFO, "Access is " + a.getAccessName());
        Group group = new Group("default");
        Group g = groupBean.createGroup(group);
        logger.log(Level.INFO, "Group is " + g.getGroupName());
        UserGroup userGroup = userGroupBean.createUserGroup(uu, g, a);
        logger.log(Level.INFO, "UserGroup user's id is " + userGroup.getUserId());
    }

    public void initTypes(TypeBean typeBean) {
        Type type1 = new Type("folder");
        typeBean.createType(type1);
        Type type2 = new Type("root");
        typeBean.createType(type2);
        Type type3 = new Type("sources");
        typeBean.createType(type3);
        Type type4 = new Type("package");
        typeBean.createType(type4);
        Type type5 = new Type("runnable");
        typeBean.createType(type5);
        Type type6 = new Type("iml");
        typeBean.createType(type6);
        Type type7 = new Type("lib");
        typeBean.createType(type7);
        Type type8 = new Type("class");
        typeBean.createType(type8);
        Type type9 = new Type("interface");
        typeBean.createType(type9);
        Type type10 = new Type("exception");
        typeBean.createType(type10);
        Type type11 = new Type("enum");
        typeBean.createType(type11);
        Type type12 = new Type("annotation");
        typeBean.createType(type12);
    }

    public Project initFilesHelloWorld(ProjectBean projectBean, TypeBean typeBean, FileBean fileBean, Group g) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Project project = new Project("Hello", null);
        project.setGroup(g);
        Project p = projectBean.createProject(project);
        String source = "package test;\n public class Hello {\n static {\n System.out.println(\"hello\");\n }\n public Hello() {\n System.out.println(\"world\");\n }  \n" +
                "public static void main(String[] args) {\n System.out.println(\"HELLO WORLD\");\n  } \n}";
        logger.log(Level.INFO, "Type from initType() is " + typeBean.getType(1));
        Type type = typeBean.getType(CompileAndRunBean.FileType.CLASS.getType());
        File helloFile = new File("Hello", source.getBytes(), project, type, "Hello/src/test/Hello");
        fileBean.createFile(helloFile);
        File root = new File("Hello", null, project, typeBean.getType(2), "Hello/");
        fileBean.createFile(root);
        File sources = new File("src", null, project, typeBean.getType(3), "Hello/src");
        fileBean.createFile(sources);
        File pack = new File("test", null, project, typeBean.getType(4), "Hello/src/test");
        fileBean.createFile(pack);
        return p;
    }

    public Project initEnumCars(ProjectBean projectBean, TypeBean typeBean, FileBean fileBean, Group g) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Project project = new Project("CarsEnumProject", null);
        project.setGroup(g);
        Project proj = projectBean.createProject(project);
        String carsEnum = "package test;\n" +
                "\n" +
                "public enum CarsEnum {\n" +
                "\tlamborghini(900),tata(2),audi(50),fiat(15),honda(12);\n" +
                "\t   private int price;\n" +
                "\t   CarsEnum(int p) {\n" +
                "\t      price = p;\n" +
                "\t   }\n" +
                "\t   public int getPrice() {\n" +
                "\t      return price;\n" +
                "\t   } \n" +
                "}";
        String mainCars = "package hello;\n" +
                "\n" +
                "import test.CarsEnum;\n" +
                "\n" +
                "public class Main {\n" +
                "\tpublic static void main(String args[]){\n" +
                "\t      System.out.println(\"All car prices:\");\n" +
                "\t      for (CarsEnum c : CarsEnum.values())\n" +
                "\t      System.out.println(c + \" costs \" \n" +
                "\t      + c.getPrice() + \" thousand dollars.\");\n" +
                "\t   }\n" +
                "}";
        Type typeClass = typeBean.getType(CompileAndRunBean.FileType.CLASS.getType());
        Type typeEnum = typeBean.getType(CompileAndRunBean.FileType.ENUM.getType());
        File carsEnumFile = new File("CarsEnum", carsEnum.getBytes(), project, typeEnum, "CarsEnumProject/src/test/CarsEnum");
        File mainCarsFile = new File("Main", mainCars.getBytes(), project, typeClass, "CarsEnumProject/src/hello/Main");
        fileBean.createFile(carsEnumFile);
        fileBean.createFile(mainCarsFile);
        File root = new File("CarsEnumProject", null, project, typeBean.getType(2), "CarsEnumProject/");
        fileBean.createFile(root);
        File sources = new File("src", null, project, typeBean.getType(3), "CarsEnumProject/src");
        fileBean.createFile(sources);
        File pack = new File("test", null, project, typeBean.getType(4), "CarsEnumProject/src/test");
        fileBean.createFile(pack);
        File packMain = new File("hello", null, project, typeBean.getType(4), "CarsEnumProject/src/hello");
        fileBean.createFile(packMain);
        return proj;
    }

}