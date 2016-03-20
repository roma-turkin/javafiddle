package ru.javafiddle.web.services;

import ru.javafiddle.core.ejb.LibraryBean;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by artyom on 22.11.15.
 */
public class LibraryService {

    @EJB
    LibraryBean librariesBean;

    @POST
    @Path("/{libraryName}")
    public Response addLibrary(@PathParam("projectHash") String projectHash,
                               @PathParam("libraryName") String libraryName) {

        try{

            librariesBean.add(projectHash, libraryName);

            return Response.ok().build();

        }catch(Exception e){
            return Response.serverError().build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLibraries(@PathParam("projectHash") String projectHash) {

        try{

            List<String> libraries = librariesBean.getAll(projectHash);

            return Response.ok(libraries).build();

        }catch(Exception e){
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/{libraryName}")
    public Response removeLibrary(@PathParam("projectHash") String projectHash,
                                  @PathParam("libraryName") String libraryName) {

        try{

            librariesBean.remove(projectHash, libraryName);

            return Response.ok().build();

        }catch(Exception e){
            return Response.serverError().build();
        }

    }

    @GET
    @Path("/libraries")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLibrariesList(@PathParam("projectHash") String projectHash) {

        try{
            //getAll() means all available libraries
            List<String> libraries = librariesBean.getAll();

            return Response.ok(libraries).build();

        }catch(Exception e){
            return Response.serverError().build();
        }
    }

}
