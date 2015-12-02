package main.java.ru.javafiddle.core.ejb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import javax.persistence.PersistenceContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import ru.javafiddle.jpa.entity.File;
import ru.javafiddle.jpa.entity.Type;
import ru.javafiddle.jpa.entity.Project;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.inject.Named;

@Stateless
@Named(value = "fileBean")
public class FileBean {

    @PersistenceContext
    EntityManager em;

    public FileBean() {}


    public List<File> getProjectFiles(String projectHash) {

        List<File> files = (LinkedList<File>)em.createQuery("SELECT f FROM Hash h JOIN h.Project p JOIN p.File f WHERE h.hash=:projecthash")
                .setParameter("projecthash", projectHash)
                .getResultList();

        files = removeDuplicatesList(files);//should we use remove duplicates here???

        return files;

    }

    public void addFile(String projectHash, String fileName, byte[] data, String fileType, String pathToFile) {


        File file = new File();
        int projectId = getProjectId(projectHash);
        int typeId = getFileTypeId(fileType);

        file.setFileName(fileName);
        file.setData(data);
        file.setType(typeId);
        file.setPath(pathToFile);
        file.setProjectId(projectId);

        em.getTransaction().begin();
        em.persist(file);
        em.getTransaction().commit();



    }


    public void updateFile(String projectHash, int fileId, String fileName, byte[] data, String fileType, String pathToFile) {

        File file = getFile(fileId);

        int projectId = getProjectId(projectHash);
        int typeId = getFileTypeId(fileType);
        file.setFileName(fileName);
        file.setData(data);
        file.setType(typeId);
        file.setPath(pathToFile);
        file.setProjectId(projectId);

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

        Type type = (Type)em.createQuery("SELECT t FROM Type t WHERE t.typeName =:filetype")
                .setParameter("filetype", fileType)
                .getSingleResult();
        return type.getTypeId();


    }

    private List<File> removeDuplicatesList(List<File> files) {

        Set<Object> s = new TreeSet<Object>(new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {
                File f1 = (File)o1;
                File f2 = (File)o2;
                int f1Id = f1.getFileId();
                int f2Id = f2.getFileId();
                if ((f1Id > f2Id) || (f2Id > f1Id))
                    return 1;
                return 0;
            }
        });
        s.addAll(files);
        List<File> res = Arrays.asList(s.toArray());

        return res;
    }


}
