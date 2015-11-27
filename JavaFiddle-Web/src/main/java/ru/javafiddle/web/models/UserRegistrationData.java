package ru.javafiddle.web.models;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by artyom on 22.11.15.
 */
@XmlRootElement
public class UserRegistrationData {

    private String firstName;
    private String lastName;
    private String nickName;
    private String email;
    private String password;

    public UserRegistrationData(String firstName, String lastName, String nickName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickName = nickName;
        this.email = email;
        this.password = password;
    }

    public UserRegistrationData() { }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
