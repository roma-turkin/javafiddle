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
    private int id;
    @ManyToOne
    private Project project;
    @ManyToOne
    private Library library;

    public LibraryToProject(Project project, Library library) {
        this.project = project;
        this.library = library;
    }

    public LibraryToProject() {
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Library getLib() {
        return library;
    }

    public void setLib(Library library) {
        this.library = library;
    }

    @Override
    public String toString() {
        return "LibraryToProject{" +
                "id=" + id +
                ", project=" + project.getProjectId() +
                ", library=" + library.getLibraryId() +
                '}';
    }
}
