package ru.javafiddle.core.ejb;

import javax.persistence.EntityManager;
import javax.ejb.Stateless;
import javax.persistence.PersistenceContext;


import ru.javafiddle.jpa.entity.Hash;
import ru.javafiddle.jpa.entity.Library;
import ru.javafiddle.jpa.entity.LibraryToProject;
import ru.javafiddle.jpa.entity.Project;

import java.util.LinkedList;
import java.util.List;

@Stateless
public class LibraryBean {

    @PersistenceContext
    EntityManager em;

    public LibraryBean(){}

    public void add(String projectHash, String libraryName) {


        Hash hash = (Hash)em.createQuery("SELECT h FROM Hash h where h.hash =:projecthash")
                .setParameter("projecthash", projectHash)
                .getSingleResult();

        Library lib = (Library)em.createQuery("SELECT l FROM Library l where l.libraryName =:libraryname")
                .setParameter("libraryname", libraryName)
                .getSingleResult();

        Project project = hash.getProject();

        LibraryToProject lbt = new LibraryToProject();
        lbt.setProject(project);
        lbt.setLib(lib);

        em.persist(lbt);

    }

    public List<String> getAll(String projectHash) {

        Hash hash = (Hash)em.createQuery("SELECT h FROM Hash h where h.hash =:projecthash")
                .setParameter("projecthash", projectHash)
                .getSingleResult();

        Project project = hash.getProject();

        List<LibraryToProject> libs = project.getLibraries();
        List<String> libNames = new LinkedList<>();

        for (LibraryToProject l:libs) {

            libNames.add(l.getLib().getLibraryName());
        }

        return libNames;




    }

    public void remove(String projectHash, String libraryName) {

        Hash hash = (Hash)em.createQuery("SELECT h FROM Hash h where h.hash =:projecthash")
                .setParameter("projecthash", projectHash)
                .getSingleResult();

        Project project = hash.getProject();

        List<LibraryToProject> libs = project.getLibraries();
        for (LibraryToProject l:libs) {

            if (l.getLib().getLibraryName().equals(libraryName))
                em.remove(l);
        }

    }

    public List<String> getAll() {

        List<String> listOfLibraries = (LinkedList<String>)em.createQuery("SELECT h.libraryName FROM Library h")
                .getSingleResult();

        return listOfLibraries;
    }
}