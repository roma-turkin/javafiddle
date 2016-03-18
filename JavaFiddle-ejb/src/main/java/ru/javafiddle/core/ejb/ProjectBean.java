package ru.javafiddle.core.ejb;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.ws.rs.client.Entity;

import ru.javafiddle.jpa.entity.*;

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

    //services : WITHOUT GROUPNAME WE CANNOT CREATE SERVICES AS WE NEED TO UPDATE PROJECT LIST IN GROUP
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Project createProject(int groupId, String hash, String projectName) throws UnsupportedEncodingException, NoSuchAlgorithmException {//groupName??

        if (!hash.equals("")) {
            return createCopy(hash);

        }
        Group group;
        Project project = new Project();
        Hash hashes = new Hash();

        //get group, corresponding to this id
        group = getGroup(groupId);

        //-----------------------------------------set information related to project
        project.setProjectName(projectName);
        project.setGroup(group);
        //-----------------------------------------set hash
        String projectHash = getHash(project.getProjectId());
        hashes.setHash(projectHash);
        project.setHash(hashes);
        hashes.setProject(project);
        em.persist(project);
        //em.flush();
        System.out.println("New hash id is"+hashes.getId());
        addProject(group,project);
        System.out.println("The number of projects in this group is  " + group.getProjects().size());

        return project;

    }

    private Hash getHashFromBase(int projectId) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String Hash = getHash(projectId);
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

    private void updateHashFromBase(int projectId, Project p) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        String Hash = getHash(projectId);

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

    private Group getGroup(int groupId) {

        Group group;
        try {
            group = (Group)em.createQuery("SELECT g FROM Group g WHERE g.groupId=:groupid")
                    .setParameter("groupid", groupId)
                    .getSingleResult();
        } catch (NoResultException noResult) {

            logger.log(Level.WARNING, "NO RESULT IN QUERY", noResult);
            return null;
        }

        return group;
    }




    public String getHash(int projectId) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        Integer projId = new Integer(projectId);
        String prId = projId.toString();
        int res = prId.hashCode();
        return String.valueOf(res);

    }


    public Project getProject(String projectHash) {

        Project project;

        try {
            project = (Project) em.createQuery("SELECT p FROM Project p WHERE p.hash.hash =:projecthash")
                    .setParameter("projecthash", projectHash)
                    .getSingleResult();
        } catch(NoResultException noResult) {
            logger.log(Level.WARNING, "NO RESULT IN QUERY", noResult);
            return null;
        }

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

    public Project getProjectByName(String projectName) {

        Project p;
        try {
            p = (Project) em.createQuery("SELECT p FROM Project p WHERE p.projectName =:projectname")
                    .setParameter("projectname", projectName)
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

    public Project changeProjectName(String projectHash, String newProjectName) {

        Project project;
        project = getProject(projectHash);

        if (project == null) {

            logger.log(Level.SEVERE, "NO RESULT IN QUERY");
            return null;
        }
        project.setProjectName(newProjectName);

        em.persist(project);
        return getProject(projectHash);
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Group addProject(Group group, Project project) {

        if (group.getProjects() == null) {

            List<Project> p = new LinkedList<>();
            p.add(project);
            group.setProjects(p);
        }
        List<Project> p = group.getProjects();
        p.add(project);
        group.setProjects(p);
        em.persist(group);

        return group;

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Project createCopy(String hash) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        Project oldProject = getProject(hash);
//we create a new project and set fields -----------------------------------------
        Project newProject = new Project();
        newProject.setProjectName(oldProject.getProjectName());
        List<File> files = oldProject.getFiles();
        List<File> newFileList = new LinkedList<>(files);
        newProject.setFileList(newFileList);
        newProject.setGroup(oldProject.getGroup());
        List<Library> libs = oldProject.getLibraries();
        List<Library> newLibList = new LinkedList<>(libs);
        newProject.setLibraries(newLibList);

        em.persist(newProject);
        //em.flush();

        Hash newHash = new Hash();
        newHash.setHash(getHash(newProject.getProjectId()));
        newProject.setHash(newHash);
        // em.merge(newProject);
        // em.flush();
        newHash.setProject(newProject);
        // System.out.println("After refreshing we have a concrete hash "+newProject.getHash().getHash());
        //em.flush();
        System.out.println(newHash.getProject().getProjectId());
        System.out.println(newHash.getId());
        System.out.println(newHash.getProject().getHash().getHash());
        em.flush();
        return getProject(newHash.getHash());

    }

}