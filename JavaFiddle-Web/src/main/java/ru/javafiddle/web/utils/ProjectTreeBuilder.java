package ru.javafiddle.web.utils;

import ru.javafiddle.jpa.entity.File;
import ru.javafiddle.web.exceptions.InvalidProjectStructureException;
import ru.javafiddle.web.models.ProjectTreeNode;

import java.util.List;


/**
 * Created by artyom on 30.03.16.
 */
public class ProjectTreeBuilder {

    private ProjectTreeNode rootNode = new ProjectTreeNode();

    public ProjectTreeNode build(List<File> projectFiles) throws Exception {

        for (File f: projectFiles) {
            ProjectTreeNode newProjectTreeNode = new ProjectTreeNode(f.getFileId(),
                    f.getFileName(),
                    f.getType().getTypeName());

            appendNode(newProjectTreeNode, f.getPath());
        }

        return rootNode;
    }

    /**
     * Method appends a copy of given node to the project tree according to the path.\n
     * If there are uninitialized nodes in path, they will be initialized with name,\n
     * specified in the path
     * @param newProjectTreeNode
     * @param path
     * @throws Exception
     */
    private void appendNode(ProjectTreeNode newProjectTreeNode, String path) throws Exception {

        if (newProjectTreeNode == null || path == null) {
            throw new NullPointerException();
        }

        String[] pathComponents = path.split("/");
        //with tmp node we descend down the tree according to path
        ProjectTreeNode tmp = rootNode;

        for (int i = 0; i < pathComponents.length - 1; i++) {

            if (!isNodeInitialized(tmp)) {
                tmp.setName(pathComponents[i]);
                tmp = addChildNode(tmp);
                continue;
            }

            if (tmp.hasName(pathComponents[i])) {
                ProjectTreeNode foundNode;
                foundNode = tmp.searchForChild(pathComponents[i + 1]);
                tmp = (foundNode != null) ? foundNode : addChildNode(tmp);
                continue;
            }

            throw new InvalidProjectStructureException(tmp.toString());
        }


        //Here we can't simply write tmp = newProjectTreeNode
        //couse childNodes can be already initialized
        tmp.setFileId(newProjectTreeNode.getFileId());
        tmp.setType(newProjectTreeNode.getType());
        tmp.setName(newProjectTreeNode.getName());
    }

    private boolean isNodeInitialized(ProjectTreeNode projectTreeNode) {
        return projectTreeNode.getName() != null;
    }

    private ProjectTreeNode addChildNode(ProjectTreeNode parentNode) {
        ProjectTreeNode childNode = new ProjectTreeNode();
        parentNode.getChildNodes().add(childNode);
        return childNode;
    }
}
