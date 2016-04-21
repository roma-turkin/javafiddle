package ru.javafiddle.web.services;

import com.google.gson.Gson;
import ru.javafiddle.core.ejb.AccessBean;
import ru.javafiddle.core.ejb.FileBean;
import ru.javafiddle.core.ejb.HashBean;
import ru.javafiddle.core.ejb.ProjectBean;
import ru.javafiddle.core.ejb.TypeBean;
import ru.javafiddle.core.ejb.UserBean;
import ru.javafiddle.core.ejb.GroupBean;

import ru.javafiddle.core.ejb.UserGroupBean;
import ru.javafiddle.jpa.entity.Access;
import ru.javafiddle.jpa.entity.File;
import ru.javafiddle.jpa.entity.Group;
import ru.javafiddle.jpa.entity.Hash;
import ru.javafiddle.jpa.entity.Library;
import ru.javafiddle.jpa.entity.Project;

import ru.javafiddle.jpa.entity.Type;
import ru.javafiddle.jpa.entity.User;
import ru.javafiddle.web.exceptions.InvalidProjectStructureException;
import ru.javafiddle.web.models.ProjectInfo;
import ru.javafiddle.web.models.ProjectTreeNode;
import ru.javafiddle.web.utils.ProjectTreeBuilder;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by artyom on 19.11.15.
 */
@Path("/projects")
public class ProjectService {

    @EJB
    ProjectBean projectBean;

    @EJB
    GroupBean groupBean;

    @EJB
    FileBean fileBean;

    @EJB
    UserBean userBean;

    @EJB
    HashBean hashBean;

    @EJB
    UserGroupBean userGroupBean;

    @EJB
    AccessBean accessBean;

    @EJB
    TypeBean typeBean;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserProjectsList() {
        String nickName = userBean.getCurUserNick();
        User curUser = userBean.getUser(nickName);
        List<String> projectHashes = projectBean.getUserProjects(curUser);
        String json = new Gson().toJson(projectHashes);
        return Response.ok(json).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{projectHash}")
    public Response getProjectStructure(@PathParam("projectHash") String projectHash) {

        List<File> projectFiles = fileBean.getProjectFiles(projectHash);
        ProjectTreeBuilder projectTreeBuilder = new ProjectTreeBuilder();
        ProjectTreeNode projectTree;

        try {
            projectTree = projectTreeBuilder.build(projectFiles);
        } catch (InvalidProjectStructureException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }

        return Response.ok(projectTree).build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProject(ProjectInfo projectInfo, @Context UriInfo uriInfo) {

        try {
            //get Group
            Integer groupId = projectInfo.getGroupId();
            Group group = (groupId == null) ? getDefaultGroup() : groupBean.getGroupByGroupId(groupId);

            //if hash field is not null then clone the project
            String hash = projectInfo.getProjectHash();
            if (hash != null) {
                Project project = projectBean.getProjectByProjectHash(hash);
                if (project == null) { //if specified hash does not exist return NOT_FOUND status
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("There's no project with hash " + hash)
                            .build();
                }
                project.setGroup(group); //set specified group
                cloneProject(project);
                return Response.ok().build();
            }

            //try to create new project with given name
            String projectName = projectInfo.getProjectName();

            if (projectName != null) {

                Project project = new Project(projectName, group);
                project = projectBean.createProject(project);
                createProjectFile(project);
                return Response.ok().build();
            }

            // if both name and hash are null it is impossible to create project
            return Response.status(Response.Status.BAD_REQUEST).build();

        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @DELETE
    @Path("/{projectHash}")
    public Response deleteProject(@PathParam("projectHash") String projectHash) {

        projectBean.deleteProject(projectHash);

        return Response.ok().build();
    }

    @PUT
    @Path("/{projectHash}")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response renameProject(@PathParam("projectHash") String projectHash, String newProjectName) {

        Project project = projectBean.getProjectByProjectHash(projectHash);
        if (project == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("There's no project with hash " + projectHash)
                    .build();
        }

        project.setProjectName(newProjectName);

        projectBean.updateProject(project);

        return Response.ok().build();
    }

    @Path("/{projectHash}/libraries")
    public LibraryService initLibraryService() {
        return new LibraryService();
    }

    @Path("/{projectHash}/groups")
    public GroupService initGroupSrvice() {
        return new GroupService();
    }

    private Group getDefaultGroup() {

        User user = userBean.getUser(userBean.getCurUserNick());
        Group group = groupBean.getGroupByName(Group.DEFAULT_GROUP_NAME, user);

        if (group == null) {
            group = groupBean.createGroup(new Group(Group.DEFAULT_GROUP_NAME));
            Access access = accessBean.getAccess(Access.FULL);
            userGroupBean.createUserGroup(user, group, access);
        }

        return group;
    }

    private void createProjectFile(Project project) {

        File projectFile = new File();
        projectFile.setFileName(project.getProjectName());
        projectFile.setPath(project.getProjectName() + "/");
        projectFile.setType(typeBean.getType(Type.PROJECT_FILE_TYPEID));
        projectFile.setProject(project);

        fileBean.createFile(projectFile);
    }

    private void cloneProject(Project project) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Project clonedProject = new Project();
        clonedProject.setProjectName(project.getProjectName());
        clonedProject.setGroup(project.getGroup());
        clonedProject.setLibraries(project.getLibraries());

        clonedProject = projectBean.createProject(clonedProject);

        //copy all files
        for(File file: project.getFiles()) {
            File newFile = new File();
            newFile.setType(file.getType());
            newFile.setData(file.getData().clone());
            newFile.setPath(file.getPath());
            newFile.setProject(clonedProject);
            newFile.setFileName(file.getFileName());
            fileBean.createFile(newFile);
        }
    }

}
