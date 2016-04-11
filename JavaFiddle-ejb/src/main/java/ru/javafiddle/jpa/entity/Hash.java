package ru.javafiddle.jpa.entity;

/**
 * Created by Fedor on 18.11.2015.
 */
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Project's hash.
 */
@Entity
@Cacheable(false)
@Table(name = "\"Hash\"")
public class Hash {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "\"id\"")
    private int id;

    @Column(name = "\"hash\"")
    private String hash;

    @OneToOne(mappedBy = "hash")
    private Project project;

    public Hash(Project project, String hash) {
        this.project = project;
        this.hash = hash;
    }

    public Hash() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "Hashes{" +
                "id=" + id +
                ", project=" + project.getProjectId() +
                ", hash='" + hash +
                '}';
    }
}