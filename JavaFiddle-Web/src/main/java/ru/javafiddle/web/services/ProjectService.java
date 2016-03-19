package ru.javafiddle.web.services;

import ru.javafiddle.core.ejb.ProjectBean;

import ru.javafiddle.web.models.ProjectInfo;

import javax.ejb.EJB;
import javax.print.attribute.standard.Media;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * Created by artyom on 19.11.15.
 */
@Path("/projects")
public class ProjectService {

    @EJB
    ProjectBean projectBean;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProject(ProjectInfo projectInfo, @Context UriInfo uriInfo) {

        try {
            String projectHash = "INVALID PROJECT HASH";
//!TODO
//            projectHash = projectBean.createProject(projectInfo.getUserNickName(),
//                    projectInfo.getProjectHash(),
//                    projectInfo.getProjectName());

            URI uri = uriInfo.getAbsolutePathBuilder().path(projectHash).build();
            return Response.created(uri).build();

        } catch(Exception e){
            return Response.serverError().build();
        }

    }

    @DELETE
    @Path("/{projectHash}")
    public Response deleteProject(@PathParam("projectHash") String projectHash) {

         try{

             projectBean.deleteProject(projectHash);

             return Response.ok().build();

         }catch(NotFoundException e){
             return Response.status(Response.Status.BAD_REQUEST).build();
         }catch(Exception e){
             return Response.serverError().build();
         }
    }

    @PUT
    @Path("/{projectHash}")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response renameProject(@PathParam("projectHash") String projectHash, String newProjectName) {

        try{

            projectBean.changeProjectName(projectHash, newProjectName);

            return Response.ok().build();

        }catch(NotFoundException e){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }catch(Exception e){
            return Response.serverError().build();
        }

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
