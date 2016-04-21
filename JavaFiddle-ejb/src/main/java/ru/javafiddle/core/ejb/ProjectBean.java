package ru.javafiddle.core.ejb;

import ru.javafiddle.jpa.entity.File;
import ru.javafiddle.jpa.entity.Group;
import ru.javafiddle.jpa.entity.Hash;
import ru.javafiddle.jpa.entity.Library;
import ru.javafiddle.jpa.entity.Project;
import ru.javafiddle.jpa.entity.User;
import ru.javafiddle.jpa.entity.UserGroup;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;


import java.io.UnsupportedEncodingException;

import java.security.NoSuchAlgorithmException;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@Stateless
public class ProjectBean {

    @EJB
    private HashBean hashBean;

    @EJB
    private GroupBean groupBean;

    @EJB
    private FileBean fileBean;

    private static final Logger logger =
            Logger.getLogger(ProjectBean.class.getName());

    private static final String DEFAULT_GROUP_NAME = "default";

    @PersistenceContext(name = "JFPersistenceUnit")
    EntityManager em;

    public ProjectBean() {

    }

    /**
     * Creates a project
     * @param project Group must be specified!
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    public Project createProject(Project project) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        if (project.getGroup() == null) {
            throw new IllegalArgumentException("Group must be specified");
        }

        em.persist(project);
        em.flush();
        //-----------------------------------------set hash
        Hash hash = new Hash();
        String projectHash = hashBean.getHashForNewProject(project.getProjectId());
        hash.setHash(projectHash);
        project.setHash(hash);
        hash.setProject(project);

        em.persist(project);

        return project;
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

    public Project updateProject(Project newProject) {

        Project project;
        project = getProjectByProjectHash(newProject.getHash().getHash());

        if (project == null) {

            logger.log(Level.WARNING, "No result in getProject()");
            return null;
        }
        project.setProjectName(newProject.getProjectName());

        em.persist(project);
        return getProjectByProjectHash(project.getHash().getHash());
    }


    public void deleteProject(String projectHash) {

        Project project = getProjectByProjectHash(projectHash);

        em.remove(project);
    }

    public Project getProjectByProjectHash(String projectHash) {

        Project project;

        try {
            project = (Project) em.createQuery("SELECT p FROM Project p WHERE p.hash.hash =:projecthash")
                    .setParameter("projecthash", projectHash)
                    .getSingleResult();
        } catch (NoResultException noResult) {
            logger.log(Level.WARNING, "No result in getProject()", noResult);
            return null;
        }

        return project;
    }


    public List<String> getUserProjects(User user) {
        List<String> hashes = new LinkedList<String>();

        TypedQuery<UserGroup> query =
                em.createQuery("SELECT u FROM UserGroup u WHERE u.userId =:userid", UserGroup.class);
        List<UserGroup> groups = query.setParameter("userid", user.getUserId()).getResultList();

        for (UserGroup userGroup:groups) {

            Group group = userGroup.getGroup();
            List<Project> projects = group.getProjects();
            for (Project p:projects) {
                hashes.add(p.getHash().getHash());
            }
        }

        return hashes;
    }


    //for testing reasons
    public List<Project> getProjects(Group group) {

        TypedQuery<Project> q = em.createQuery("SELECT p FROM Project p WHERE p.group.groupId =:groupid", Project.class);
        List<Project> projects = q.setParameter("groupid", group.getGroupId())
                .getResultList();

        return projects;
    }




}
