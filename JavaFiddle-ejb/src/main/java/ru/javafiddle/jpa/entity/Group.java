package ru.javafiddle.jpa.entity;

/**
 * Created by Fedor on 18.11.2015.
 */
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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

    @OneToMany(fetch= FetchType.EAGER,mappedBy = "group")
    List<Project> projects;

    @OneToMany(mappedBy = "group")
    private List<UserGroup>  members;

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

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public List<UserGroup> getMembers() {
        return members;
    }

    public void setMembers(List<UserGroup> members) {
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
