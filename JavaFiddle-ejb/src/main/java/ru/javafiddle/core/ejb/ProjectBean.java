package ru.javafiddle.core.ejb;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import ru.javafiddle.jpa.entity.Hash;
import ru.javafiddle.jpa.entity.Project;
import ru.javafiddle.jpa.entity.Group;
import ru.javafiddle.jpa.entity.File;
import ru.javafiddle.jpa.entity.Library;
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

    public ProjectBean() {

    }

    //services : WITHOUT GROUPNAME WE CANNOT CREATE SERVICES AS WE NEED TO UPDATE PROJECT LIST IN GROUP
    public Project createProject(int groupId, String hash, String projectName) throws UnsupportedEncodingException, NoSuchAlgorithmException {//groupName??

        if (!hash.equals("")) {
            return createCopy(hash);

        }
        Group group;
        Project project = new Project();
        Hash hashes = new Hash();

        //-----------------------------------------get group, corresponding to this id
        group = getGroupByGroupId(groupId);

        //-----------------------------------------set information related to project
        project.setProjectName(projectName);
        project.setGroup(group);
        //-----------------------------------------set hash
        String projectHash = getHashForNewProject(project.getProjectId());
        hashes.setHash(projectHash);
        project.setHash(hashes);
        hashes.setProject(project);

        em.persist(project);
        //-----------------------------------------logging
        logger.info("New hash id is" + hashes.getId());
        addProject(group, project);
        logger.info("The number of projects in this group is  " + group.getProjects().size());

        return project;

    }

    public Project createCopy(String hash) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        Project oldProject = getProjectByProjectHash(hash);
//------------------------------------------------------------we create a new project and set fields
        Project newProject = new Project();
        newProject.setProjectName(oldProject.getProjectName());
        List<File> files = oldProject.getFiles();
        List<File> newFileList = new LinkedList<>(files);
        newProject.setFileList(newFileList);
        newProject.setGroup(oldProject.getGroup());
        List<Library> libs = oldProject.getLibraries();
        List<Library> newLibList = new LinkedList<>(libs);
        newProject.setLibraries(newLibList);
//------------------------------------------------------------we need to persist at first to get the id of our project
//------------------------------------------------------------and then base the hash on this id
        em.persist(newProject);

        Hash newHash = new Hash();
        newHash.setHash(getHashForNewProject(newProject.getProjectId()));
        newProject.setHash(newHash);
//------------------------------------------------------------we don't need to persist hash entity separetely
//------------------------------------------------------------as cascade type is mentioned
        newHash.setProject(newProject);

        logger.info("Projecct-copy id got from hash " + newHash.getProject().getProjectId());
        logger.info("Hash id " + newHash.getId());
        logger.info("Checked hash for a project is " + newHash.getProject().getHash().getHash());
        em.flush();
        return getProjectByProjectHash(newHash.getHash());

    }



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

    public Project updateProject(String projectHash, String newProjectName) {

        Project project;
        project = getProjectByProjectHash(projectHash);

        if (project == null) {

            logger.log(Level.WARNING, "No result in getProject()");
            return null;
        }
        project.setProjectName(newProjectName);

        em.persist(project);
        return getProjectByProjectHash(projectHash);
    }


    public void deleteProject(String projectHash){

        Project project = getProjectByProjectHash(projectHash);

        em.remove(project);


    }

    public Group getGroupByGroupId(int groupId) {

        Group group;
        try {
            group = (Group) em.createQuery("SELECT g FROM Group g WHERE g.groupId=:groupid")
                    .setParameter("groupid", groupId)
                    .getSingleResult();
        } catch (NoResultException noResult) {

            logger.log(Level.WARNING, "No result in getGroupByGroupId()", noResult);
            return null;
        }

        return group;
    }




    public String getHashForNewProject(int projectId) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        Integer projId = new Integer(projectId);
        String prId = projId.toString();

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] array = md.digest(prId.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
        }
        return sb.toString();

    }


    public Project getProjectByProjectHash(String projectHash) {

        Project project;

        try {
            project = (Project) em.createQuery("SELECT p FROM Project p WHERE p.hash.hash =:projecthash")
                    .setParameter("projecthash", projectHash)
                    .getSingleResult();
        } catch( NoResultException noResult) {
            logger.log(Level.WARNING, "No result in getProject()", noResult);
            return null;
        }

        return project;

    }




}