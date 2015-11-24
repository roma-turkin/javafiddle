package ru.javafiddle.web.services;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * It's a bad practise in RESTful API to name links with verbs
 * But I have no ideas how to afoid this
 */
@Path("/")
public class CompileAndRunService {

    @EJB
    CompileAndRunBean compileAndRunBean;

    @Path("/compile")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response compile(ProjectJF Project) {

        //It's a question what ProjectJF should contain
        try {
            String compilationOutput = compileAndRunBean.compile(Project);
            return Response.ok().entity(compilationOutput).build();
        }catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @Path("/run")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response run(ProjectJF Project) {
        //ExecutionResult must contain Streams and may be project files
        //becouse we cun write in file
        try {
            ExecutionResult executionResult = compileAndRunBean.run(Project);
            return Response.ok().entity(executionResult).build();
        }catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @Path("/compileAndRun")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response compileAndRun(ProjectJF Project) {

        try {
            CompileAndRunResult compileAndRunResult = compileAndRunBean.compileAndRun(Project);
            return Response.ok().entity(compileAndRunResult).build();
        }catch (Exception e) {
            return Response.serverError().build();
        }
    }

}
