package ru.javafiddle.jpa.entity;

/**
 * Created by Fedor on 18.11.2015.
 */
import javax.persistence.*;
import java.util.List;

/**
 * A union of users.
 */
@Entity
@Table(name = "\"Group\"")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "\"groupId\"")
    private int groupId;
    @Column(name = "\"groupName\"")
    private String groupName;
    @OneToMany
    @JoinColumn(name = "\"projectId\"")
    List<Project> projects;
    @ManyToMany
    @JoinTable(name = "\"UserGroup\"" ,
        joinColumns = @JoinColumn(name = "\"groupId\""),
        inverseJoinColumns = @JoinColumn(name = "\"userId\""))
    private List<User> members;

    public Group(String groupName) {
        this.groupName = groupName;
    }

    public Group() {
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Project> getProject() { return projects; }

    public void setProject(List<Project> projects) { this.projects = projects; }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                '}';
    }
}