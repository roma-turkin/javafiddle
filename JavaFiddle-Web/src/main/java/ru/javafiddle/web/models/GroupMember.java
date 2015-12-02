package ru.javafiddle.web.models;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by artyom on 24.11.15.
 */
@XmlRootElement
public class GroupMember {

    private String userNickName;
    private String accessRights;

    public GroupMember() {
    }

    public GroupMember(String userNickName, String accessRights) {
        this.userNickName = userNickName;
        this.accessRights = accessRights;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getAccessRights() {
        return accessRights;
    }

    public void setAccessRights(String accessRights) {
        this.accessRights = accessRights;
    }
}
