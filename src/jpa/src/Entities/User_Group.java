package Entities;

/**
 * Created by Fedor on 18.11.2015.
 */
import javax.persistence.*;

@Entity
@Table
@Embeddable

public class User_Group {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int ID;
    @ManyToOne
    private Group group;
    @ManyToMany
    private User user;
    @ManyToOne
    private Access access;

    public User_Group(Group group, Access access, User user) {
        this.group = group;
        this.access = access;
        this.user = user;
    }

    public User_Group() {
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

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    @Override
    public String toString() {
        return "User_Group{" +
                "ID=" + ID +
                ", group=" + group.getGroup_id() +
                ", user=" + user.getUser_id() +
                ", access=" + access.getAccess_id() +
                '}';
    }
}
