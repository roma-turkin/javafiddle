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

@Entity
@Table

public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int ID;
    @ManyToOne
    private Group group;
    @ManyToMany
    private User user;
    @ManyToOne
    private Access access;

    public UserGroup(Group group, Access access, User user) {
        this.group = group;
        this.access = access;
        this.user = user;
    }

    public UserGroup() {
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ru.javafiddle.jpa.entity.Access getAccess() {
        return access;
    }

    public void setAccess(ru.javafiddle.jpa.entity.Access access) {
        this.access = access;
    }

    @Override
    public String toString() {
        return "UserGroup{" +
                "ID=" + ID +
                ", group=" + group.getGroup_id() +
                ", user=" + user.getUser_id() +
                ", access=" + access.getAccess_id() +
                '}';
    }
}
