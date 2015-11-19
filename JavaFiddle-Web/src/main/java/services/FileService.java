package services;



import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
    public Response getfiles(@PathParam("projectHash") String projectHash) {

        try {
            FileJF[] files = filesBean.getProjectFiles(projectHash);
            return Response.ok(files).build();
        } catch(NotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch(Exception e) {
            return Response.serverError().build();
        }

    }
    
}
