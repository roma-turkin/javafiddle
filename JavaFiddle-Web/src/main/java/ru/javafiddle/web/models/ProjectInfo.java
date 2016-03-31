package ru.javafiddle.web.models;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by artyom on 22.11.15.
 */
@XmlRootElement
public class ProjectInfo {

    private String projectHash;
    private String projectName;
    private String userNickName;
    private Integer groupId;

    public ProjectInfo(String projectHash, String projectName, String userNickName, Integer groupId) {
        this.projectHash = projectHash;
        this.projectName = projectName;
        this.userNickName = userNickName;
        this.groupId = groupId;
    }

    public ProjectInfo() {
    }

    public String getProjectHash() {
        return projectHash;
    }

    public void setProjectHash(String projectHash) {
        this.projectHash = projectHash;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}
