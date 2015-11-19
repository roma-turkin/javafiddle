package Entities;

/**
 * Created by Fedor on 18.11.2015.
 */
import javax.persistence.*;

@Entity
@Table

public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int group_id;
    private String group_name;

    public Group(int group_id, String group_name) {
        this.group_id = group_id;
        this.group_name = group_name;
    }

    public Group() {
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    @Override
    public String toString() {
        return "Group{" +
                "group_id=" + group_id +
                ", group_name='" + group_name +
                '}';
    }
}
