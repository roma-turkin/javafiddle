package ru.javafiddle.jpa.entity;

/**
 * Created by Fedor on 18.11.2015.
 */
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.Lob;
import java.util.Arrays;

@Entity
@Table

public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int fileId;
    private String fileName;
    @Lob
    private byte[] data;
    @ManyToOne
    private Project project;
    @ManyToOne
    private Type type;
    private String path;

    public File(String fileName, byte[] data, Project project, Type type, String path) {
        this.fileName = fileName;
        this.data = data;
        this.project = project;
        this.type = type;
        this.path = path;
    }

    public File() {
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "File{" +
                "fileId=" + fileId +
                ", fileName='" + fileName + '\'' +
                ", project=" + project.getProjectId() +
                ", type=" + type.getTypeId() +
                ", path='" + path + '\'' +
                '}';
    }
}
