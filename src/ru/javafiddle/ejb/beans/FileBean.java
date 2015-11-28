package ru.javafiddle.ejb.beans;

import javax.persistence.PersistenceContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import ru.javafiddle.jpa.entity.File;
import ru.javafiddle.jpa.entity.Type;

@Stateless
@Named(value = "fileBean")
public class FileBean {

    @PersistenceContext(unitName = "")
    EntityManager em;

    public FileBean() {}


    public List<File> getProjectFiles(String projectHash) {

        List<File> files = (List<File>)em.createQuery("SELECT f FROM Hash h JOIN h.Project p JOIN p.File f WHERE h.hash=:projecthash")
                                         .setParameter("projecthash", projectHash)
                                         .getResultList();

        return files;

    }//test if this query returns enough row, otherwise transform it into two independent queries

    public void addFile(String projectHash, String fileName, byte[] data, String fileType, String pathToFile) {


        File file = new File();
        int projectId = getProjectId(projectHash);
        int typeId = getFileTypeId(fileType);

        file.setFileName(fileName);
        file.setData(data);
        file.setType(typeId);
        file.setPath(pathToFile);
        file.setProjectId()

        em.getTransaction().begin();
        em.persist(file);
        em.getTransaction().commit();



    }


    public void updateFile(String projectHash, int fileId, String fileName, byte[] data, String fileType, String pathToFile) {

        File file = getFile(int fileId);

        int projectId = getProjectId(projectHash);
        int typeId = getFileTypeId(fileType);
        file.setFileName(fileName);
        file.setData(data);
        file.setType(typeId);
        file.setPath(pathToFile);
        file.setProjectId();

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

    private int getProjectId(String projectHash) {

        Project project = (Project)em.createQuery("SELECT p FROM Hash h JOIN h.Project p WHERE h.hash =:projecthash")
                                     .setParameter("projecthash", projectHash)
                                     .getSingleResult();
        return project.getProjectId();
    }

    private int getFileTypeId(String fileType) {

        Type type = (Type)em.createQuery("SELECT FROM Type t WHERE t.typeName =:filetype")
                            .setParameter("filetype", fileType)
                            .getSingleResult();
        return type.getTypeId();//check name of func


    }


}