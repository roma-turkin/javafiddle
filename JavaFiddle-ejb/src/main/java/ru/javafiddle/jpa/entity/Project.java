package ru.javafiddle.jpa.entity;

/**
 * Created by Fedor on 18.11.2015.
 */
import javax.persistence.*;
import java.util.List;

@Entity
@Table

public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int projectId;
    private String projectName;
    @ManyToOne
    private Group group;
    @OneToMany
    private List<File> file;
    @OneToMany
    private List<LibraryToProject> lib;
    @OneToOne
    Hash hash;

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

    public List<File> getFiles() { return file; }

    public void setFileList(List<File> file) { this.file = file; }

    public Hash getHash() { return hash; }

    public void setHash( Hash hash) { this.hash = hash; }


    public List<LibraryToProject> getLibraries() { return lib; }

    public void setLibraries( List<LibraryToProject> lib) { this.lib = lib; }

    @Override
    public String toString() {
        return "Project{" +
                "projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", group=" + group.getGroupId() +
                '}';
    }
}
