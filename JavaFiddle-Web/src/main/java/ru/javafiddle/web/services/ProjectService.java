package ru.javafiddle.web.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import ru.javafiddle.core.ejb.FileBean;
import ru.javafiddle.core.ejb.ProjectBean;

import ru.javafiddle.core.ejb.UserBean;
import ru.javafiddle.jpa.entity.File;
import ru.javafiddle.web.models.ProjectInfo;
import ru.javafiddle.web.models.ProjectStructure;

import javax.ejb.EJB;
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
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;

/**
 * Created by artyom on 19.11.15.
 */
@Path("/projects")
public class ProjectService {

    @EJB
    ProjectBean projectBean;

    @EJB
    FileBean fileBean;

    @EJB
    UserBean userBean;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserProjectsList() {
        String nickName = userBean.getCurUserNick();
        List<String> projectHashes = userBean.getUserProjects(nickName);
        String json = new Gson().toJson(projectHashes);
        return Response.ok(json).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{projectHash}")
    public Response getProjectStructure(@PathParam("projectHash") String projectHash) throws Exception {

        List<File> projectFiles = fileBean.getProjectFiles(projectHash);
        ProjectStructure projectStructure = new ProjectStructure();

        for(File f: projectFiles) {

            String path = f.getPath();
            String[] pathComponents = path.split("/");

            ProjectStructure tmp = projectStructure;
            for(int i=0; i<pathComponents.length -1; i++) {

                if(tmp.getName() == null) {
                    tmp.setName(pathComponents[i]);
                    ProjectStructure newChild = new ProjectStructure();
                    tmp.getChildFiles().add(newChild);
                    tmp = newChild;
                    continue;
                }

                if(tmp.getName().compareTo(pathComponents[i]) == 0) {
                    List<ProjectStructure> childFiles = tmp.getChildFiles();
                    boolean found = false;
                    for(ProjectStructure ps: childFiles) {
                        if(ps.getName().compareTo(pathComponents[i+1]) == 0) {
                            tmp = ps;
                            found = true;
                            break;
                        }
                    }
                    if(!found) {
                        ProjectStructure newChild = new ProjectStructure();
                        tmp.getChildFiles().add(newChild);
                        tmp = newChild;
                    }
                    continue;
                }

                throw new Exception("Invalid project stucture" + tmp.getName());

            }

            tmp.setName(f.getFileName());
            tmp.setType(f.getType().getTypeName());
            tmp.setFileId(f.getFileId());
        }

        return Response.ok(projectStructure).build();
    }

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
