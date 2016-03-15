package ru.javafiddle.web.models;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by artyom on 22.11.15.
 */
@XmlRootElement
public class UserJF {

    private String firstName;
    private String lastName;
    private String nickName;
    private String email;
    private String registrationDate;
    private List<String> projectsHashs;
    private String status;

    public UserJF(String firstName, String lastName, String nickName, String email, String registrationDate, List<String> projectsHashs, String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickName = nickName;
        this.email = email;
        this.registrationDate = registrationDate;
        this.projectsHashs = projectsHashs;
        this.status = status;
    }

    public UserJF() {
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

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
