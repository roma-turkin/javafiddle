package ru.javafiddle.web.models;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by artyom on 22.11.15.
 */
@XmlRootElement
public class ProjectInfo {

    private String projectHash;
    private String projectName;
    private int groupId;

    public ProjectInfo(String projectHash, String projectName, int groupId) {
        this.projectHash = projectHash;
        this.projectName = projectName;
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

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
