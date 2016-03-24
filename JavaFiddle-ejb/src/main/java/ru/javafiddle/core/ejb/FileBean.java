package ru.javafiddle.core.ejb;

import ru.javafiddle.jpa.entity.File;
import ru.javafiddle.jpa.entity.Hash;
import ru.javafiddle.jpa.entity.Project;
import ru.javafiddle.jpa.entity.Type;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;

@Stateless

public class FileBean {

    @PersistenceContext
    EntityManager em;

    @PersistenceUnit(unitName = "test")
    EntityManagerFactory entityManagerFactory;

    public FileBean() {}



//    /////////////////////////
//    public List<File> getProjectFiles(String projectHash) {
//        List<File> fileList = new ArrayList<>();
//        int projectId = getProjectId(projectHash);
//        DBWorker dbWorker = new DBWorker();
//        String queryFiles = "select * from \"File\" f, \"Project\" p where f.\"projectId\" = p.\"projectId\"" + "and p.\"projectId\" = " + projectId;
//        try {
//            Statement statement = dbWorker.getConnection().createStatement();
//            ResultSet resultSet = statement.executeQuery(queryFiles);
//            while (resultSet.next()) {
//                File file = new File();
//                file.setData(resultSet.getBytes("data"));
//                file.setFileId(resultSet.getInt("fileId"));
//                file.setFileName(resultSet.getString("fileName"));
//                file.setPath(resultSet.getString("path"));
//                Type type = new Type();
//                String queryType = "select t.\"typeName\", t.\"typeId\" from \"Type\" t, \"File\" f where f.\"typeId\" = t.\"typeId\" and f.\"typeId\" = "
//                        + resultSet.getInt("typeId") + " and f.\"fileName\" = \'" + resultSet.getString("fileName") + "\'";
//                Statement statement1 = dbWorker.getConnection().createStatement();
//                ResultSet res = statement1.executeQuery(queryType);
//                while(res.next()) {
//                    type.setTypeName(res.getString("typeName"));
//                    type.setTypeId(res.getInt("typeId"));
//                }
//                file.setType(type);
//                fileList.add(file);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        try {
//            dbWorker.getConnection().close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return fileList;
//
//    }
//
//    public int getProjectId(String projectHash) {
//        DBWorker dbWorker = new DBWorker();
//        int projectId = 0;
//        String query = "select p.\"projectId\", p.\"projectName\" from \"Hash\" h, \"Project\" p "
//                + "where h.\"id\" = p.\"id\" and h.\"hash\"=\'" + projectHash + "\'";
//        try {
//            Statement statement = dbWorker.getConnection().createStatement();
//            ResultSet res = statement.executeQuery(query);
//            while (res.next()) {
//                projectId = res.getInt("projectId");
//            }
//            dbWorker.getConnection().close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return projectId;
//    }
//    ////////////////////////

    public List<File> getProjectFiles(String projectHash) {

        TypedQuery<Hash> query = em.createQuery("SELECT h FROM Hash h WHERE h.hash=:projectHash", Hash.class);
        Hash h = query.setParameter("projectHash",projectHash).getSingleResult();

        return h.getProject().getFiles();

    }

    public void addFile(String projectHash, String fileName, byte[] data, String fileType, String pathToFile) {


        File file = new File();
        Project project = getProject(projectHash);
        Type type = getFile(fileType);

        file.setFileName(fileName);
        file.setData(data);
        file.setType(type);
        file.setPath(pathToFile);
        file.setProject(project);

        em.getTransaction().begin();
        em.persist(file);
        em.getTransaction().commit();



    }


    public void updateFile(String projectHash, int fileId, String fileName, byte[] data, String fileType, String pathToFile) {

        File file = getFile(fileId);

        Project project = getProject(projectHash);
        Type type = getFile(fileType);
        file.setFileName(fileName);
        file.setData(data);
        file.setType(type);
        file.setPath(pathToFile);
        file.setProject(project);

        em.getTransaction().begin();
        em.persist(file);
        em.getTransaction().commit();

    }

    public void deleteFile(String projectHash, int fileId) {

        File file = getFile(fileId);

        em.getTransaction().begin();
        em.remove(file);
        em.getTransaction().commit();


    }

    private File getFile(int fileId) {

        File file = (File)em.createQuery("SELECT f FROM File f where f.fileId =:fileid")
                .setParameter("fileid", fileId)
                .getSingleResult();
        return file;
    }

    private Project getProject(String projectHash) {

        Project project = (Project)em.createQuery("SELECT p FROM \"Hash\" h JOIN \"Project\" p WHERE h.hash =:projectHash")
                .setParameter("projectHash", projectHash)
                .getSingleResult();
        return project;
    }

    private Type getFile(String fileType) {

        Type type = (Type)em.createQuery("SELECT t FROM Type t WHERE t.typeName =:filetype")
                .setParameter("filetype", fileType)
                .getSingleResult();
        return type;


    }



}
