package services;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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

        String projectHash;

        try {
            projectHash = projectBean.createProject(projectInfo);
        } catch(Exception e){
            return Response.serverError().build();
        }

        URI uri = uriInfo.getAbsolutePathBuilder().path(projectHash).build();
        return Response.created(uri).build();
    }
}
