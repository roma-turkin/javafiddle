package Entities;

/**
 * Created by Fedor on 18.11.2015.
 */
import javax.persistence.*;

@Entity
@Table

public class Lib_Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int ID;
    @ManyToOne
    private Project project;
    @ManyToOne
    private Lib lib;

    public Lib_Project(Project project, Lib lib) {
        this.project = project;
        this.lib = lib;
    }

    public Lib_Project() {
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Lib getLib() {
        return lib;
    }

    public void setLib(Lib lib) {
        this.lib = lib;
    }

    @Override
    public String toString() {
        return "Lib_Project{" +
                "ID=" + ID +
                ", project=" + project.getProject_id() +
                ", lib=" + lib.getLib_id() +
                '}';
    }
}
