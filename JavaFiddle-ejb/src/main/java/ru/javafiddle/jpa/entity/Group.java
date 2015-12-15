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
@Table

public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int groupId;
    private String groupName;
    @OneToMany
    List<Project> projects;

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

    @Override
    public String toString() {
        return "Group{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                '}';
    }
}