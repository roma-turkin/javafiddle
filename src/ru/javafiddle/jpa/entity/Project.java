package ru.javafiddle.jpa.entity;

/**
 * Created by Fedor on 18.11.2015.
 */
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;

@Entity
@Table

public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int projectId;
    private String projectName;
    @ManyToOne
    private Group group;

    public Project(String projectName, Group group) {
        this.projectName = projectName;
        this.group = group;
    }

    public Project() {
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "Project{" +
                "projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", group=" + group.getGroupId() +
                '}';
    }
}
