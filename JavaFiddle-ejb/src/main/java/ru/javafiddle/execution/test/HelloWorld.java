package ru.javafiddle.execution.test;

import ru.javafiddle.core.ejb.CompileAndRunBean;
import ru.javafiddle.core.ejb.ProjectBean;
import ru.javafiddle.jpa.entity.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nk on 13.03.2016.
 */


public class HelloWorld {


    @PersistenceContext
    static
    EntityManager em;

    public static void main(String[] args) {
        String fileData = "package ru.ncedu.javafiddle.execution.test;\n" +
                "\n" +
                "\n" +
                "/**\n" +
                " * Created by nk on 25.11.2015.\n" +
                " */\n" +
                "public class Simple {\n" +
                "public static void main(String[] args) {\n" +
                "System.out.println(\"Hello\");" + "\n" +
                "}\n }";
        String path = "ru.ncedu.javafiddle.execution.test";
        byte[] data = fileData.getBytes();
        Group group = new Group("DefaultGroup");
        group.setGroupId(1);
        Project project = new Project("HelloWorld", group);
        Type type = new Type("java");
        File file = new File("Simple", data, project, type, path);
        List<File> list = new ArrayList<>();
        list.add(file);
        project.setFileList(list);
        Hash hash = new Hash(project, "123");
        project.setHash(hash);
        System.out.println(project.toString());
        System.out.println(file.toString());
        CompileAndRunBean compileAndRunBean = new CompileAndRunBean();
        //String message = compileAndRunBean.compile(project.getHash().getHash());
        ProjectBean projectBean = new ProjectBean();
        System.out.println("\"");
        DBWorker dbWorker = new DBWorker();
        String query = "select * from \"Group\"";
        try {
            Statement statement = dbWorker.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            Group group1 = null;
            while (resultSet.next()) {
                group1 = new Group();
                group1.setGroupId(resultSet.getInt("groupId"));
                group1.setGroupName(resultSet.getString("groupName"));
                System.out.println(group1);
            }

            String queryProj = "select * from \"Project\"";
            resultSet = statement.executeQuery(queryProj);
            while (resultSet.next()) {
                Project p = new Project();
                p.setProjectId(resultSet.getInt("projectId"));
                p.setProjectName(resultSet.getString("projectName"));
                p.setGroup(group1);
                System.out.println(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }



        String message1 = compileAndRunBean.compile("123");
        System.out.println(compileAndRunBean.run("123"));

        try {
            dbWorker.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
