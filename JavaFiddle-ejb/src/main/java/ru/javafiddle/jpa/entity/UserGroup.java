package ru.javafiddle.jpa.entity;

/**
 * Created by Fedor on 18.11.2015.
 */
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
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
@Table(name = "\"UserGroup\"")
@IdClass(GroupAssociationId.class)
public class UserGroup {

    @Id
    private int userId;
    @Id
    private int groupId;

    @ManyToOne
    @PrimaryKeyJoinColumn(name = "userid", referencedColumnName = "\"userId\"")
    private User client;

    @ManyToOne
    @PrimaryKeyJoinColumn(name = "groupid", referencedColumnName = "\"groupId\"")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "\"accessId\"")
    private Access access;

    public UserGroup(Group group, User client, Access access) {
        this.group = group;
        this.client = client;
        this.access = access;
    }

    public UserGroup() {
    }

//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

//    public List<User> getClient() {
//        return client;
//    }
//
//    public void setClient(List<User> client) {
//        this.client = client;
//    }


    public User getClient() {
        return client;
    }

    public void setClient(User client) {
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
//                "id=" + id +
                ", group=" + group.getGroupId() +
                ", client=" + client.toString() +
                ", access=" + access.getAccessId() +
                '}';
    }
}