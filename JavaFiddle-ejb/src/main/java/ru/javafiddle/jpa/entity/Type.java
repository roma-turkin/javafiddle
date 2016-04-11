package ru.javafiddle.jpa.entity;

/**
 * Created by Fedor on 18.11.2015.
 */
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.List;

/**
 * A type of file.
 */
@Entity
@Table(name = "\"Type\"")
public class Type {

    public static final String PROJECT_FILE = "root";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "\"typeId\"")
    private int typeId;

    @Column(name = "\"typeName\"")
    private String typeName;

    @OneToMany(mappedBy = "type")
    private List<File> files;

    public Type(String typeName) {
        this.typeName = typeName;
    }

    public Type() {
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    @Override
    public String toString() {
        return "Type{" +
                "typeId=" + typeId +
                ", typeName='" + typeName + '\'' +
                '}';
    }
}
