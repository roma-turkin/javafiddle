package ru.javafiddle.web.models;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by artyom on 28.11.15.
 */
@XmlRootElement
public class ProjectJF {

    private String      projectHash;
    private FileJF[]    projectFiles;
    private String[]    projectLibraries;

    public ProjectJF(String projectHash, FileJF[] projectFiles, String[] projectLibraries) {
        this.projectHash = projectHash;
        this.projectFiles = projectFiles;
        this.projectLibraries = projectLibraries;
    }

    public ProjectJF() {
    }

    public String getProjectHash() {
        return projectHash;
    }

    public void setProjectHash(String projectHash) {
        this.projectHash = projectHash;
    }

    public FileJF[] getProjectFiles() {
        return projectFiles;
    }

    public void setProjectFiles(FileJF[] projectFiles) {
        this.projectFiles = projectFiles;
    }

    public String[] getProjectLibraries() {
        return projectLibraries;
    }

    public void setProjectLibraries(String[] projectLibraries) {
        this.projectLibraries = projectLibraries;
    }
}
