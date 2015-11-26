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

@Entity
@Table

public class LibraryToProject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int ID;
    @ManyToOne
    private Project project;
    @ManyToOne
    private Library lib;

    public LibraryToProject(Project project, Library lib) {
        this.project = project;
        this.lib = lib;
    }

    public LibraryToProject() {
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

    public Library getLib() {
        return lib;
    }

    public void setLib(Library lib) {
        this.lib = lib;
    }

    @Override
    public String toString() {
        return "LibraryToProject{" +
                "ID=" + ID +
                ", project=" + project.getProject_id() +
                ", lib=" + lib.getLib_id() +
                '}';
    }
}
