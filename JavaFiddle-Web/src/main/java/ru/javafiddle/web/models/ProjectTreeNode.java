package ru.javafiddle.web.models;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by artyom on 19.03.16.
 */
@XmlRootElement
public class ProjectTreeNode {

    private Integer fileId;
    private String  name;
    private String  type;
    private List<ProjectTreeNode> childNodes;

    public ProjectTreeNode(Integer fileId, String name, String type, List<ProjectTreeNode> childNodes) {
        this.fileId = fileId;
        this.name = name;
        this.type = type;
        this.childNodes = childNodes;
    }

    public ProjectTreeNode(Integer fileId, String name, String type) {
        this.fileId = fileId;
        this.name = name;
        this.type = type;
        this.childNodes = new LinkedList<>();
    }

    public ProjectTreeNode(String name) {
        this.name = name;
        this.childNodes = new LinkedList<>();
    }

    public ProjectTreeNode() {
        this.childNodes = new LinkedList<>();
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ProjectTreeNode> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(List<ProjectTreeNode> childNodes) {
        this.childNodes = childNodes;
    }

    public boolean hasName(String anotherName) {
        return name.compareTo(anotherName) == 0;
    }

    public ProjectTreeNode searchForChild(String childName) {
        for (ProjectTreeNode childNode: childNodes) {
            if (childNode.hasName(childName)) {
                return childNode;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "ProjectTreeNode{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", fileId=" + fileId +
                '}';
    }
}
