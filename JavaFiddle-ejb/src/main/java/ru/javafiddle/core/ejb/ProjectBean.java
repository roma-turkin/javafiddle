package ru.javafiddle.ejb.beans;

import javax.persistence.PersistenceContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.inject.Named;

@Stateless
@Named(value = "projectBean")
public class ProjectBean {

    private static final String DEFAULT_GROUP_NAME = "default";

    @PersistenceContext(unitName = "")
    EntityManager em;

    public ProjectBean() {}

    public String createProject(String projectName, String projectHash, String groupId) {//groupName??

        Group group;
        Project project = new Project();
        Hashes hashes = new Hashes();

        //set information related to project
        project.setName(projectName);
        project.setGroupId(groupId);

        em.getTransaction().begin();
        em.persist(project);
        em.getTransaction().commit();

        project = getProject(projectInfo);

        //set information related to hashes
        hashes.setProjectId(project.getProjectId());
        hashes.setprojectHash(projectHash);

        em.getTransaction().begin();
        em.persist(hashes);
        em.getTransaction().commit();

        //set information about groups
        group = (Group)em.createQuery("SELECT p FROM Group p WHERE groupId =:groupId")
                         .setParameter("groupid", groupId)
                         .getSingleResult();

        if (group == NULL) {
            group = new Group();
            group.setId(groupId);
            group.setName(DEFAULT_GROUP_NAME);
            em.getTransaction().begin();
            em.persist(group);
            em.getTransaction().commit();
            //setting name??
        }




    }

    public void renameProject(String projectName, String groupId) {

        Project project;
        project = getProject(projectName, groupId);

        project.setName(projectName);

        em.getTransaction().begin();
        em.persist(project);
        em.getTransaction().commit();
    }

    public void deleteProject(String projectName, String groupId) {


        Project project = getProject(projectInfo);

        em.getTransaction().begin();
        em.remove(project);
        em.getTransaction().commit();
    }



    public Project getProject(String projectName, String groupId) {

        Project project;

        project = (Project)em.createQuery("SELECT p FROM Project p WHERE p.projectName =:projectname and p.groupId =:groupid")
                .setParameter("projectname", projectName)
                .setParameter("groupid", groupId)
                .getSingleResult();

        return project;

    }
}
