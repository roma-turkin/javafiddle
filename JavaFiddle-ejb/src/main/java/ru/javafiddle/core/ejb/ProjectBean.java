package ru.javafiddle.core.ejb;

import javax.persistence.PersistenceContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import ru.javafiddle.jpa.entity.Group;
import ru.javafiddle.jpa.entity.Project;
import ru.javafiddle.jpa.entity.Hash;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


@Stateless
public class ProjectBean {

    private static final String DEFAULT_GROUP_NAME = "default";

    @PersistenceContext
    EntityManager em;

    public ProjectBean() {}

    public String createProject(String projectName, int groupId) {//groupName??

        Group group;


        Project project = new Project();
        Hash hashes = new Hash();

        //get group, corresponding to this id
        group = getGroup(groupId);

        //-----------------------------------------set information related to project
        project.setProjectName(projectName);
        project.setGroup(group);

        em.getTransaction().begin();
        em.persist(project);
        em.getTransaction().commit();

        project = getProject(projectName, groupId);

        //------------------------------------------set information related to hashes
        //  String projectHash = getHash(projectName);
        hashes.setProject(project);
        // hashes.setHash(projectHash);

        em.getTransaction().begin();
        em.persist(hashes);
        em.getTransaction().commit();




        return null;

    }

    public void renameProject(String projectName, int groupId) {

        Project project;
        project = getProject(projectName, groupId);

        project.setProjectName(projectName);

        em.getTransaction().begin();
        em.persist(project);
        em.getTransaction().commit();
    }

    public void deleteProject(String projectName, int groupId) {


        Project project = getProject(projectName, groupId);

        em.getTransaction().begin();
        em.remove(project);
        em.getTransaction().commit();
    }



    public Project getProject(String projectName, int groupId) {

        Project project;

        project = (Project)em.createQuery("SELECT p FROM Project p WHERE p.projectName =:projectname and p.group.groupId =:groupid")
                .setParameter("projectname", projectName)
                .setParameter("groupid", groupId)
                .getSingleResult();

        return project;

    }

    private Group getGroup(int groupId) {

        Group group = (Group)em.createQuery("SELECT g FROM Group g WHERE g.groupId=:groupid")
                .setParameter("groupid", groupId)
                .getSingleResult();

        NavigableMap<String, String> m = new TreeMap<>();
        return group;
    }

    private void createGroup(int groupId) {
        Group g = new Group();

        g.setGroupName("default");
        g.setGroupId(groupId);






    }



    public String getHash(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        byte[] input = digest.digest(password.getBytes("UTF-8"));

        String res = new String(input, "UTF-8");
        return res;
    }

    //!TODO
    public int getGroupId(String projectHash) {
        return -1;
    }

    //!TODO
    public void deleteProject(String projectHash){

    }

    //!TODO
    public void changeProjectName(String projectHash, String newProjectName) {

    }
}
