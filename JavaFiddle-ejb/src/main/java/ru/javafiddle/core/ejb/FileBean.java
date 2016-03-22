package ru.javafiddle.core.ejb;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ru.javafiddle.jpa.entity.File;
import ru.javafiddle.jpa.entity.Hash;
import ru.javafiddle.jpa.entity.Type;
import ru.javafiddle.jpa.entity.Project;

@Stateless

public class FileBean {

    private static final Logger logger =
            Logger.getLogger(ProjectBean.class.getName());

    @PersistenceContext(name = "JFPersistenceUnit")
    EntityManager em;

    public FileBean() {}


    public List<File> getProjectFiles(String projectHash) {

        TypedQuery<Hash> query = em.createQuery("SELECT h FROM Hash h WHERE h.hash=:projectHash", Hash.class);
        Hash h = query.setParameter("projectHash",projectHash).getSingleResult();

        return h.getProject().getFiles();

    }
//we don't need to create file FileJF while passing it to functions, as this is a new file and we don't know id
//I assume that we already have a table of possible types
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public File addFile(String projectHash, String fileName, byte[] data, String fileType, String pathToFile) {


        File file = new File();
        Project project = getProject(projectHash);
        Type type = getType(fileType);

        file.setFileName(fileName);
        file.setData(data);
        file.setType(type);
        file.setPath(pathToFile);
        file.setProject(project);

        List<File> files = project.getFiles();
        files.add(file);
        type.getFiles().add(file);
        //project.setFileList(files);
        em.persist(file);
        //em.merge(project);
        //em.merge(type);

       // System.out.println(file.getFileId());
       // System.out.println("project after persistence "+ project.getProjectId()+" and file in it "+project.getFiles().size());
        return file;


    }


    public File updateFile(String projectHash, int fileId, String fileName, byte[] data, String fileType, String pathToFile) {

        File file = getFile(fileId);

        Project project = getProject(projectHash);
        Type type = getType(fileType);
        file.setFileName(fileName);
        file.setData(data);
        file.setType(type);
        file.setPath(pathToFile);
        file.setProject(project);

        em.persist(file);

        return file;


    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteFile(String projectHash, int fileId) {

        File file = getFile(fileId);
        Project p = getProject(projectHash);
        Type t = file.getType();
        List<File> pList = p.getFiles();
        List<File> tList = t.getFiles();
        System.out.println("size before rem "+tList.size());
        System.out.println("size before rem "+pList.size());

        for (Iterator<File> iter = pList.listIterator(); iter.hasNext(); ) {
            File a = iter.next();
            if (a.getFileId() == fileId)
                iter.remove();
        }
        for (Iterator<File> iter = tList.listIterator(); iter.hasNext(); ) {
            File a = iter.next();
            if (a.getFileId() == fileId)
                iter.remove();
        }

        System.out.println("size after rem "+tList.size());
        System.out.println("size after rem "+pList.size());
       /* em.merge(p);

        //f = p.getFiles();
        //f.remove(file);
        //p.setFileList(f);*/
        //em.remove(file);


    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public File getFile(int fileId) {

        File file;
        try {
            file = (File) em.createQuery("SELECT f FROM File f where f.fileId =:fileid")
                    .setParameter("fileid", fileId)
                    .getSingleResult();
        } catch (NoResultException noResult) {
        logger.log(Level.WARNING, "NO RESULT IN QUERY", noResult);
        return null;
        }
        return file;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Project getProject(String projectHash) {

        Project project;
        try {
            project = (Project) em.createQuery("SELECT p FROM Project p WHERE p.hash.hash =:projecthash")
                    .setParameter("projecthash", projectHash)
                    .getSingleResult();
        } catch (NoResultException noResult) {
            logger.log(Level.WARNING, "NO RESULT IN QUERY", noResult);
            return null;
        }
        return project;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Type getType(String fileType) {

        Type type;
        try {
            type = (Type) em.createQuery("SELECT t FROM Type t WHERE t.typeName =:filetype")
                    .setParameter("filetype", fileType)
                    .getSingleResult();
        } catch (NoResultException noResult) {
            logger.log(Level.WARNING, "NO RESULT IN QUERY", noResult);
            return null;
        }
        return type;


    }

//Do we need this method?
    public Type createType(String typeName) {

        Type t = new Type();
        t.setTypeName(typeName);
        em.persist(t);

        return t;
    }

    public boolean checkNumberOfFiles (String fileName,int number) {
        TypedQuery<File> query =
                em.createQuery("SELECT f FROM File f WHERE f.fileName=:filename", File.class);
        List<File> u = query.setParameter("filename", fileName).getResultList();

        return (u.size() == number);


    }



}

