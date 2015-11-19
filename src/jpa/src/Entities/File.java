package Entities;

/**
 * Created by Fedor on 18.11.2015.
 */
import javax.persistence.*;

@Entity
@Table

public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int file_id;
    private String file_name;
    @Lob
    private byte[] file;
    @ManyToOne
    private Project project;
    @ManyToOne
    private Type type;
    private String path;

    public File(String file_name, byte[] file, Project project, Type type, String path) {
        this.file_name = file_name;
        this.file = file;
        this.project = project;
        this.type = type;
        this.path = path;
    }

    public File() {
    }

    public int getFile_id() {
        return file_id;
    }

    public void setFile_id(int file_id) {
        this.file_id = file_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
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
                "file_id=" + file_id +
                ", file_name='" + file_name + '\'' +
                ", project=" + project.getProject_id() +
                ", type=" + type.getType_id() +
                ", path='" + path +
                '}';
    }
}
