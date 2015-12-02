package ru.javafiddle.web.models;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by artyom on 28.11.15.
 */
@XmlRootElement
public class FileJF {

    private Integer fileId;
    private String  name;
    private byte[]  data;
    private String  type;
    private String  path;

    public FileJF() {
    }

    public FileJF(int fileId, String name, byte[] data, String type, String path) {
        this.fileId = fileId;
        this.name = name;
        this.data = data;
        this.type = type;
        this.path = path;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
