package ru.javafiddle.web.services;

import ru.javafiddle.core.ejb.FileBean;
import ru.javafiddle.core.ejb.CompileAndRunBean;

import ru.javafiddle.web.models.FileJF;
import ru.javafiddle.web.models.ProjectJF;

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

    @EJB
    FileBean filesBean;

    @Path("/compile")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response compile(ProjectJF project) {

        //It's a question what ProjectJF should contain
        try {
            String projectHash = project.getProjectHash();
            String compilationOutput = null;
            if(projectHash == null){
                //!user is guest!
                //!what to do
            }else{
                //save firstly
                for(FileJF f: project.getProjectFiles()){
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

                compilationOutput = compileAndRunBean.compile(projectHash);
            }
            return Response.ok().entity(compilationOutput).build();
        }catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @Path("/run")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response run(ProjectJF project) {
        //ExecutionResult must contain Streams and may be project files
        //becouse we cun write in file
        try {
            String projectHash = project.getProjectHash();
            String executionResult = null;

            if(projectHash == null){
                //!TODO guest user
                return null;
            } else {
                executionResult = compileAndRunBean.run(projectHash);
                return Response.ok().entity(executionResult).build();
            }

        }catch (Exception e) {
            return Response.serverError().build();
        }
    }

//    @Path("/compileAndRun")
//    @GET
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response compileAndRun(ProjectJF Project) {
//
//        try {
//            String projectHash = project.getProjectHash();
//            CompileAndRunResult compileAndRunResult = compileAndRunBean.compileAndRun(Project);
//            return Response.ok().entity(compileAndRunResult).build();
//        }catch (Exception e) {
//            return Response.serverError().build();
//        }
//    }

}
