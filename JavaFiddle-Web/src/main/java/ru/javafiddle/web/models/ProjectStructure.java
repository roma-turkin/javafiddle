package ru.javafiddle.web.models;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by artyom on 19.03.16.
 */
@XmlRootElement
public class ProjectStructure {

    private Integer fileId;
    private String  name;
    private String  type;
    private List<ProjectStructure> childFiles;

    public ProjectStructure(Integer fileId, String name, String type, List<ProjectStructure> childFiles) {
        this.fileId = fileId;
        this.name = name;
        this.type = type;
        this.childFiles = childFiles;
    }

    public ProjectStructure(Integer fileId, String name, String type) {
        this.fileId = fileId;
        this.name = name;
        this.type = type;
        this.childFiles = new LinkedList<>();
    }

    public ProjectStructure(String name) {
        this.name = name;
        this.childFiles = new LinkedList<>();
    }

    public ProjectStructure() {
        this.childFiles = new LinkedList<>();
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

    public List<ProjectStructure> getChildFiles() {
        return childFiles;
    }

    public void setChildFiles(List<ProjectStructure> childFiles) {
        this.childFiles = childFiles;
    }
}
