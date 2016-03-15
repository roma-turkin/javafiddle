package ru.javafiddle.core.ejb;

import javax.persistence.NoResultException;
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
import java.util.logging.Level;
import java.util.logging.Logger;


@Stateless
public class ProjectBean {

    private static final Logger logger =
            Logger.getLogger(ProjectBean.class.getName());

    private static final String DEFAULT_GROUP_NAME = "default";

    @PersistenceContext(name = "JFPersistenceUnit")
    EntityManager em;

    public ProjectBean() {}

    public Project createProject(String projectName, String groupName) throws UnsupportedEncodingException, NoSuchAlgorithmException {//groupName??

        Group group;
        Project project = new Project();
        Hash hashes = new Hash();

        //get group, corresponding to this id
        group = getGroup(groupName);

        //-----------------------------------------set information related to project
        project.setProjectName(projectName);
        project.setGroup(group);
        //-----------------------------------------set hash
        String projectHash = getHash(projectName, groupName);
        hashes.setHash(projectHash);

        //-----------------------------------------persist hashes
//        em.getTransaction().begin();
        em.persist(hashes);
        //em.flush();
 //       em.getTransaction().commit();


        //------------------------------------------get the example of the upper hash from the database
        Hash h1 = getHashFromBase(projectName, groupName);
        //------------------------------------------set the appropriate field in project( we need here exactly the hash instance from database
        project.setHash(h1);
        //------------------------------------------persiste project
//        em.getTransaction().begin();
        em.persist(project);
//        em.getTransaction().commit();

        //-------------------------------------------get the insance of the upper project from the database
        Project p = getProject(projectHash);

        updateHashFromBase(projectName, groupName, p);


        p = getProject(projectHash);
 //     update list of projects in groups
        addProject(group, project);

        return p;

    }

    private Hash getHashFromBase(String projectName, String groupName) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String Hash = getHash(projectName, groupName);
        Hash h;

        try {
             h = (Hash)em.createQuery("SELECT h FROM Hash h WHERE h.hash=:hash")
                    .setParameter("hash", Hash)
                    .getSingleResult();
        } catch (NoResultException noResult) {

            logger.log(Level.WARNING, "NO RESULT IN QUERY", noResult);
            return null;
        }

        return h;

    }

    private void updateHashFromBase(String projectName,  String groupName, Project p) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        String Hash = getHash(projectName, groupName);

        Hash h = (Hash)em.createQuery("SELECT h FROM Hash h WHERE h.hash=:hash")
                .setParameter("hash", Hash)
                .getSingleResult();

        h.setProject(p);
        em.persist(h);

    }


    private Group getGroup(String groupName) {

        Group group;
     try {
        group = (Group)em.createQuery("SELECT g FROM Group g WHERE g.groupName=:groupname")
                .setParameter("groupname", groupName)
                .getSingleResult();
     } catch (NoResultException noResult) {

         logger.log(Level.WARNING, "NO RESULT IN QUERY", noResult);
         return null;
     }

        return group;
    }


   /* private Group createGroup(int groupId) {
        Group group = new Group();

        group.setGroupName("default");
        group.setGroupId(groupId);

        return group;
    }*/



    public String getHash(String projectName,  String groupName) throws NoSuchAlgorithmException, UnsupportedEncodingException {
/*
        int res = projectName.hashCode();
        int res2 = groupName.hashCode();
        String strRes = String.valueOf(res);
        String strRes2 = String.valueOf(res2);
        String a = new StringBuilder(strRes).append(strRes2).toString();*/

        return "";
    }


    public Project getProject(String projectHash) {
      /*  Hash h;

        try {
            h = (Hash) em.createQuery("SELECT h FROM Hash h WHERE h.hash =:projecthash")
                    .setParameter("projecthash", projectHash)
                    .getSingleResult();
        } catch (NoResultException nores) {

            return null;
        }

        int hashId = h.getId();*/

        Project project;

        project = (Project)em.createQuery("SELECT p FROM Project p WHERE p.hash.hash =:projecthash")
                .setParameter("projecthash", projectHash)
                .getSingleResult();

        return project;

    }

    public Project getProject(String projectName, String groupName) {

        Project p;
        try {
            p = (Project) em.createQuery("SELECT p FROM Project p WHERE p.projectName =:projectname AND p.group.groupName =:groupname")
                    .setParameter("projectname", projectName)
                    .setParameter("groupname", groupName)
                    .getSingleResult();
        }catch (NoResultException noResult) {
            logger.log(Level.WARNING, "NO RESULT IN QUERY", noResult);
            return null;
        }
        return p;
    }


    public int getGroupId(String projectHash) {

        Project p = getProject(projectHash);

        int groupId = p.getGroup().getGroupId();

        return groupId;

    }




    public void deleteProject(String projectHash){

        Project project = getProject(projectHash);

        em.remove(project);


    }

    public void changeProjectName(String projectHash, String newProjectName) {

        Project project;
        project = getProject(projectHash);

        if (project == null) {

            logger.log(Level.SEVERE, "NO RESULT IN QUERY");
            return;
        }
        project.setProjectName(newProjectName);

        em.persist(project);
    }

   /* public void addProject(Group group, Project project) {

        GroupBean gb = new GroupBean();
        gb.addProject(group,project);
    }*/

    public Group addProject(Group group, Project project) {

        if (group.getProjects() == null) {

            List<Project> p = new LinkedList<>();
            p.add(project);
            group.setProjects(p);
        }
        List<Project> p = group.getProjects();
        p.add(project);
        group.setProjects(p);

        return group;

    }

}
