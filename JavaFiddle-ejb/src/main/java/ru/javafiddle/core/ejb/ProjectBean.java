package ru.javafiddle.core.ejb;

import javax.persistence.PersistenceContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.ws.rs.client.Entity;

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

    public void createProject(String projectName, String groupName, EntityManager em) throws UnsupportedEncodingException, NoSuchAlgorithmException {//groupName??

        Group group;
        Project project = new Project();
        Hash hashes = new Hash();

        //get group, corresponding to this id
        group = getGroup(groupName, em);

        //-----------------------------------------set information related to project
        project.setProjectName(projectName);
        project.setGroup(group);
        //-----------------------------------------set hash
        String projectHash = getHash(projectName);
        hashes.setHash(projectHash);

        //-----------------------------------------persist hashes
        em.getTransaction().begin();
        em.persist(hashes);
        //em.flush();
        em.getTransaction().commit();


        //------------------------------------------get the example of the upper hash from the database
        Hash h1 = getHashFromBase(projectName, em);
        //------------------------------------------set the appropriate field in project( we need here exactly the hash instance from database
        project.setHash(h1);
        //------------------------------------------persiste project
        em.getTransaction().begin();
        em.persist(project);
        em.getTransaction().commit();

        //-------------------------------------------get the insance of the upper project from the database
        Project p = getProject(projectHash,em);

        updateHashFromBase(projectName, p, em);




    }

    private Hash getHashFromBase(String projectName, EntityManager em) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String Hash = getHash(projectName);

        Hash h = (Hash)em.createQuery("SELECT h FROM Hash h WHERE h.hash=:hash")
                .setParameter("hash", Hash)
                .getSingleResult();

        return h;

    }

    private void updateHashFromBase(String projectName, Project p, EntityManager em) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        String Hash = getHash(projectName);

        Hash h = (Hash)em.createQuery("SELECT h FROM Hash h WHERE h.hash=:hash")
                .setParameter("hash", Hash)
                .getSingleResult();

        h.setProject(p);

        em.getTransaction().begin();
        em.persist(h);
        em.getTransaction().commit();


    }


    private Group getGroup(String groupName, EntityManager em) {

        Group group = (Group)em.createQuery("SELECT g FROM Group g WHERE g.groupName=:groupname")
                .setParameter("groupname", groupName)
                .getSingleResult();


        return group;
    }

    private void createGroup(int groupId) {
        Group g = new Group();

        g.setGroupName("default");
        g.setGroupId(groupId);


    }



    public String getHash(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        /*MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        byte[] input = digest.digest(password.getBytes("UTF-8"));*/

        int res = password.hashCode();
        String strRes = String.valueOf(res);
        return strRes;
    }

    public Project getProject(String projectHash, EntityManager em) {
        Hash h;
        h = (Hash)em.createQuery("SELECT h FROM Hash h WHERE h.hash =:projecthash")
                .setParameter("projecthash", projectHash)
                .getSingleResult();
        int hashId = h.getId();

        Project project;

        project = (Project)em.createQuery("SELECT p FROM Project p WHERE p.hash.id =:projecthash")
                .setParameter("projecthash", hashId)
                .getSingleResult();

        return project;

    }


    public int getGroupId(String projectHash) {

        Project p = getProject(projectHash, em);

        int groupId = p.getGroup().getGroupId();

        return groupId;

    }




    public void deleteProject(String projectHash, EntityManager em){

        Project project = getProject(projectHash, em);

        em.getTransaction().begin();
        em.remove(project);
        em.getTransaction().commit();


    }

    public void changeProjectName(String projectHash, String newProjectName, EntityManager em) {

        Project project;
        project = getProject(projectHash, em);
        project.setProjectName(newProjectName);


        em.getTransaction().begin();
        em.persist(project);
        em.getTransaction().commit();


    }

}
