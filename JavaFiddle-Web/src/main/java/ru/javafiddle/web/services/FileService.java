package ru.javafiddle.web.services;



import ru.javafiddle.ejb.beans.FilesBean;

import ru.javafiddle.jpa.entity.File;

import ru.javafiddle.web.models.FileJF;

import javax.ejb.EJB;
import javax.persistence.Access;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by artyom on 19.11.15.
 */
@Path("/")
public class FileService {

    @EJB
    FilesBean filesBean;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFiles(@PathParam("projectHash") String projectHash) {

        try {
            File[]      files  = filesBean.getProjectFiles(projectHash);
            FileJF[]    filesJF= new FileJF[files.length];
            for(int i=0; i<files.length; i++) {
                filesJF[i] = new FileJF(files[i].getFile_id(),
                        files[i].getFile_name(),
                        files[i].getFile(),
                        files[i].getType().getTypeName(),
                        files[i].getPath());
            }
            return Response.ok(filesJF).build();
        } catch(AccessRightsException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        } catch(NotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch(Exception e) {
            return Response.serverError().build();
        }

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addFile(@PathParam("projectHash") String projectHash, FileJF newFile) {

        try {
            filesBean.addFile(projectHash,
                    newFile.getName(),
                    newFile.getData(),
                    newFile.getType(),
                    newFile.getPath());
            return Response.ok().build();
        } catch(AccessRightsException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        } catch(NameSpaceException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        } catch(Exception e) {
            return Response.serverError().build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveProjectFiles(@PathParam("projectHash") String projectHash, FileJF[] projectFiles) {

        try {
            for(FileJF f: projectFiles){
                Integer fileId = f.getFileId();
                if(fileId == null) { //file has not been added yet
                    filesBean.addFile(projectHash,
                            f.getName(),
                            f.getData(),
                            f.getType(),
                            f.getPath());
                } else {
                    filesBean.updateFile(projectHash,
                            fileId,
                            f.getName(),
                            f.getData(),
                            f.getType(),
                            f.getPath());
                }
            }
            return Response.ok().build();
        } catch(AccessRightsException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        } catch(Exception e) {
            return Response.serverError().build();
        }
    }

    @Path("/{fileId}")
    @DELETE
    public Response deleteFile(@PathParam("projectHash") String projectHash, @PathParam("fileId") int fileId) {

        try {
            filesBean.deleteFile(projectHash, fileId);
            return Response.ok().build();
        } catch(NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch(AccessRightsException e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        } catch(Exception e) {
            return Response.serverError().build();
        }
    }

}
