package ru.javafiddle.web.services;

import com.google.gson.Gson;
import ru.javafiddle.core.ejb.FileBean;
import ru.javafiddle.core.ejb.ProjectBean;
import ru.javafiddle.core.ejb.UserBean;
import ru.javafiddle.core.ejb.GroupBean;

import ru.javafiddle.jpa.entity.File;
import ru.javafiddle.jpa.entity.Project;

import ru.javafiddle.jpa.entity.User;
import ru.javafiddle.web.exceptions.InvalidProjectStructureException;
import ru.javafiddle.web.models.ProjectInfo;
import ru.javafiddle.web.models.ProjectTreeNode;
import ru.javafiddle.web.utils.ProjectTreeBuilder;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

//Comment until agreement in algorithm
//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Response createProject(ProjectInfo projectInfo, @Context UriInfo uriInfo) {
//
//        Group group = groupBean.getGroupByGroupId(projectInfo.getGroupId());
//
//        if (group == null) {
//            group = groupBean.createGroup(new Group())
//        }
//
//        groupBean.getGroupByGroupId(projectInfo.)
//
//        try {
//            String projectHash;
//
//            projectHash = projectBean.createProject(projectInfo.getUserNickName(),
//                    projectInfo.getProjectHash(),
//                    projectInfo.getProjectName());
//
//            URI uri = uriInfo.getAbsolutePathBuilder().path(projectHash).build();
//            return Response.created(uri).build();
//
//        } catch(Exception e){
//            return Response.serverError().build();
//        }
//
//    }

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
            return Response.status(Response.Status.NOT_FOUND).build();
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

}
