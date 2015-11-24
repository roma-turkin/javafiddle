package ru.javafiddle.ejb.beans;

import javax.persistence.PersistenceContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;


@Stateless
@Named(value = "projectBean")
public class ProjectBean {

    @PersistenceContext(unitName = "")
    EntityManager em;

    public ProjectBean() {}

    public String createProject(ProjectInfo projectInfo) {

        Project project = new Project();
        Hashes hashes = new Hashes();

        project.setName(projectInfo.getName());
        project.setGroupId(projectInfo.getGroupId());

        em.getTransaction().begin();
        em.persist(project);

        project = getProject(projectInfo);

        hashes.setProjectId(project.getProjectId());
        hashes.setprojectHash(projectInfo.getProjectHash());

        em.persist(hashes);
        em.getTransaction().commit();


    }

    public void renameProject(ProjectInfo projectInfo) {

        Project project;
        project = getProject(projectInfo);

        project.setName(projectInfo.getName());

        em.getTransaction().begin();
        em.persist(project);
        em.getTransaction().commit();
    }

    public void deleteProject(ProjectInfo projectInfo) {


        Project project = getProject(projectInfo);

        em.getTransaction().begin();
        em.remove(project);
        em.getTransaction().commit();
    }

    public Project getProject(ProjectInfo projectInfo) {

        Project project;

        project = (Project)em.createQuery("SELECT p FROM Project p WHERE p.projectName LIKE :projectname and p.groupId LIKE :groupid")
                .setParameter("projectname", projectInfo.getName())
                .setParameter("groupid", projectInfo.getGroupId())
                .getSingleResult();

        return project;

    }
}