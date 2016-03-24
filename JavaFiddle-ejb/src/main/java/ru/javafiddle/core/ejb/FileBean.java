package ru.javafiddle.core.ejb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import javax.persistence.PersistenceContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ru.javafiddle.jpa.entity.File;
import ru.javafiddle.jpa.entity.Hash;
import ru.javafiddle.jpa.entity.Type;
import ru.javafiddle.jpa.entity.Project;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Stateless

public class FileBean {

    @PersistenceContext
    EntityManager em;

    public FileBean() {
    }


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

        em.persist(file);

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

    public File getFile(int fileId) {

        File file = (File)em.createQuery("SELECT f FROM File f where f.fileId =:fileid")
                .setParameter("fileid", fileId)
                .getSingleResult();
        return file;
    }

    private Project getProject(String projectHash) {

        Project project = (Project)em.createQuery("SELECT p FROM Hash h JOIN Project p WHERE h.hash =:projecthash")
                .setParameter("projecthash", projectHash)
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

