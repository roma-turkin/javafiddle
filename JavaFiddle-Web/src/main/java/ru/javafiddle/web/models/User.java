package ru.javafiddle.web.models;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by artyom on 22.11.15.
 */
@XmlRootElement
public class User {

    private String firstName;
    private String lastName;
    private String nickName;
    private String email;
    private List<String> projectsHashs;

    public User(String firstName, String lastName, String nickName, String email, List<String> projectsHashs) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickName = nickName;
        this.email = email;
        this.projectsHashs = projectsHashs;
    }

    public User() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getProjectsHashs() {
        return projectsHashs;
    }

    public void setProjectsHashs(List<String> projectsHashs) {
        this.projectsHashs = projectsHashs;
    }
}
