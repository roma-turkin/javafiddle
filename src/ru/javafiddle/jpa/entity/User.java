package ru.javafiddle.jpa.entity;

/**
 * Created by Fedor on 18.11.2015.
 */
import java.text.DateFormat;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;

@Entity
@Table

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userId;
    private String name;
    private String email;
    private String passwordHash;
    private DateFormat registered;
    @ManyToOne
    private Status status;

    public User(String name, String email, String passwordHash, DateFormat registered, Status status) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.registered = registered;
        this.status = status;
    }

    public User(){
    }

    public int getUser_id() {
        return userId;
    }

    public void setUser_id(int user_id) {
        this.userId = userId;
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
        return passwordHash;
    }

    public void setPassword_hash(String password_hash) {
        this.passwordHash = password_hash;
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
                "userId=" + userId +
                ", name='" + name +
                ", email='" + email +
                ", passwordHash='" + passwordHash +
                ", registered=" + registered +
                ", status=" + status.getStatus_id() +
                '}';
    }
}