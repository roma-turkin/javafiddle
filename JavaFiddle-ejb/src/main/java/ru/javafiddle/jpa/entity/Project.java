package ru.javafiddle.jpa.entity;

/**
 * Created by Fedor on 18.11.2015.
 */
import javax.persistence.*;
import java.util.List;

@Entity
@Cacheable(false)
@Table(name = "\"Project\"")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"projectId\"")
    private int projectId;

    @Column(name = "\"projectName\"")
    private String projectName;

    @ManyToOne
    @JoinColumn(name = "\"groupId\"")
    private Group group;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true,mappedBy = "project")
    private List<File> files;

    @ManyToMany
    @JoinTable(name = "\"LibraryToProject\"" ,
            joinColumns = @JoinColumn(name = "\"libraryId\""),
            inverseJoinColumns = @JoinColumn(name = "\"projectId\""))
    private List<Library> libs;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "\"id\"")
    private Hash hash;

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

    public List<File> getFiles() { return files; }

    public void setFileList(List<File> files) { this.files = files; }

    public Hash getHash() { return hash; }

    public void setHash( Hash hash) { this.hash = hash; }


    public List<Library> getLibraries() { return libs; }

    public void setLibraries( List<Library> libs) { this.libs = libs; }

    @Override
    public String toString() {
        return "Project{" +
                "projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", group=" + group.getGroupId() +
                '}';
    }
}
