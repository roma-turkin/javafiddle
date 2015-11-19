package Entities;

/**
 * Created by Fedor on 18.11.2015.
 */
import java.text.DateFormat;
import java.util.Collection;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int user_id;
    private String name;
    private String email;
    private String password_hash;
    private DateFormat registered;
    @ManyToOne
    private Status status;

    public User(String name, String email, String password_hash, DateFormat registered, Status status) {
        this.name = name;
        this.email = email;
        this.password_hash = password_hash;
        this.registered = registered;
        this.status = status;
    }

    public User(){
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public DateFormat getRegistered() {
        return registered;
    }

    public void setRegistered(DateFormat registered) {
        this.registered = registered;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id=" + user_id +
                ", name='" + name +
                ", email='" + email +
                ", password_hash='" + password_hash +
                ", registered=" + registered +
                ", status=" + status.getStatus_id() +
                '}';
    }
}