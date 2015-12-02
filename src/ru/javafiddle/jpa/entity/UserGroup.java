package ru.javafiddle.jpa.entity;

/**
 * Created by Fedor on 18.11.2015.
 */
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 * A relationship between users and groups.
 */
@Entity
@Table

public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne
    private Group group;
    @ManyToMany
    private List<User> client;
    @ManyToOne
    private Access access;

    public UserGroup(Group group, List<User> client, Access access) {
        this.group = group;
        this.client = client;
        this.access = access;
    }

    public UserGroup() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public List<User> getClient() {
        return client;
    }

    public void setClient(List<User> client) {
        this.client = client;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    @Override
    public String toString() {
        return "UserGroup{" +
                "id=" + id +
                ", group=" + group.getGroupId() +
                ", client=" + client.toString() +
                ", access=" + access.getAccessId() +
                '}';
    }
}