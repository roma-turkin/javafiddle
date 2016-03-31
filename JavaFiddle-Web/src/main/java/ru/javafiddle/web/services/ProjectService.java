package ru.javafiddle.web.services;

import ru.javafiddle.core.ejb.GroupBean;
import ru.javafiddle.core.ejb.ProjectBean;

import ru.javafiddle.core.ejb.UserBean;
import ru.javafiddle.jpa.entity.Project;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    UserBean userBean;

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


    @Path("/{projectHash}/files")
    public FileService initFileService() {
        return new FileService();
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
