package ru.javafiddle.web.services;

import ru.javafiddle.core.ejb.FileBean;

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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by artyom on 19.11.15.
 */
@Path("/files")
public class FileService {

    @EJB
    FileBean filesBean;

    @GET
    @Path("/{fileId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFile(@PathParam("fileId") int fileId) {

        File file = filesBean.getFile(fileId);
        String data = new String(file.getData(), StandardCharsets.UTF_8);
        FileJF json = new FileJF(file.getFileId(),
                file.getFileName(),
                data,
                file.getType().getTypeName(),
                file.getPath());

        return Response.ok(json).build();
    }

    @PUT
    @Path("/{fileId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveFile(@PathParam("fileId") int fileId, FileJF fileJF) {

        filesBean.updateFile(fileJF.getFileId(),
                fileJF.getName(),
                fileJF.getData().getBytes(StandardCharsets.UTF_8),
                fileJF.getType(),
                fileJF.getPath());

        return Response.ok().build();
    }

    //According to the current javascript logic these methods seem to be unnecessary
    //Nevertheles, let them be commented until hole block of working with files
    // will be implemented on the javascript side
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getFiles(@PathParam("projectHash") String projectHash) {
//
//        try {
//            List<File>      files  = filesBean.getProjectFiles(projectHash);
//            List<FileJF>    filesJF= new ArrayList<FileJF>();
//            for(int i=0; i<files.size(); i++) {
//                filesJF.add(new FileJF(files.get(i).getFileId(),
//                        files.get(i).getFileName(),
//                        new String(files.get(i).getData(),StandardCharsets.UTF_8),
//                        files.get(i).getType().getTypeName(),
//                        files.get(i).getPath()));
//            }
//            return Response.ok(filesJF).build();
////        } catch(AccessRightsException e) {
////            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
//        } catch(NotFoundException e) {
//            return Response.status(Response.Status.BAD_REQUEST).build();
//        } catch(Exception e) {
//            return Response.serverError().build();
//        }
//
//    }
//
//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Response addFile(@PathParam("projectHash") String projectHash, FileJF newFile) {
//
//        try {
//            filesBean.addFile(projectHash,
//                    newFile.getName(),
//                    newFile.getData().getBytes(),
//                    newFile.getType(),
//                    newFile.getPath());
//            return Response.ok().build();
////        } catch(AccessRightsException e) {
////            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
////        } catch(NameSpaceException e) {
////            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
//        } catch(Exception e) {
//            return Response.serverError().build();
//        }
//    }
//
//    @PUT
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Response saveProjectFiles(@PathParam("projectHash") String projectHash, FileJF[] projectFiles) {
//
//        try {
//            for(FileJF f: projectFiles){
//                Integer fileId = f.getFileId();
//                if(fileId == null) { //file has not been added yet
//                    filesBean.addFile(projectHash,
//                            f.getName(),
//                            f.getData().getBytes(),
//                            f.getType(),
//                            f.getPath());
//                } else {
//                    filesBean.updateFile(projectHash,
//                            fileId,
//                            f.getName(),
//                            f.getData().getBytes(),
//                            f.getType(),
//                            f.getPath());
//                }
//            }
//            return Response.ok().build();
////        } catch(AccessRightsException e) {
////            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
//        } catch(Exception e) {
//            return Response.serverError().build();
//        }
//    }

    @Path("/{fileId}")
    @DELETE
    public Response deleteFile(@PathParam("projectHash") String projectHash, @PathParam("fileId") int fileId) {

        try {
            filesBean.deleteFile(projectHash, fileId);
            return Response.ok().build();
        } catch(NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
//        } catch(AccessRightsException e) {
//            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        } catch(Exception e) {
            return Response.serverError().build();
        }
    }

}
