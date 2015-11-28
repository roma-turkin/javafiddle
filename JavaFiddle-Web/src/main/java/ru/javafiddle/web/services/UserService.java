package ru.javafiddle.web.services;

import ru.javafiddle.ejb.beans.UserBean;

import ru.javafiddle.jpa.entity.User;

import ru.javafiddle.web.models.UserJF;
import ru.javafiddle.web.models.UserRegistrationData;

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
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;
import java.net.URI;


/**
 * Created by artyom on 15.11.15.
 */
@Path("/users")
public class UserService {

    @EJB
    private UserBean userBean;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(UserRegistrationData userRegistrationData, @Context UriInfo uriInfo) throws UriBuilderException {

        try {

            userBean.register(userRegistrationData.getFirstName(),
                    userRegistrationData.getLastName(),
                    userRegistrationData.getNickName(),
                    userRegistrationData.getEmail(),
                    userRegistrationData.getPassword());

            String nickName = userRegistrationData.getNickName();
            URI uri = uriInfo.getAbsolutePathBuilder().path(nickName).build();

            return Response.created(uri).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();//!TODO make it possible to detect which field is invalid
        } catch (Exception e) {
            return Response.serverError().build();
        }

    }

    @GET
    @Path("/{nickName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserInfo(@PathParam("nickName") String nickName) {

        try {
            User user = userBean.getUser(nickName);

            UserJF userJF = new UserJF(user.getFirstName(),
                    user.getLastName(),
                    user.getNickName(),
                    user.getEmail(),
                    userBean.getUserProjects(nickName));

            return Response.ok(userJF).build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }

    }

    @PUT
    @Path("/{nickName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserInfo(@PathParam("nickName") String nickName, UserRegistrationData userRegistrationData) {

        try {

            userBean.setUser(userRegistrationData.getFirstName(),
                    userRegistrationData.getLastName(),
                    userRegistrationData.getEmail(),
                    userRegistrationData.getPassword());

            return Response.ok().build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/{nickName}")
    public Response deleteUser(@PathParam("nickName") String nickName) {

        try {

            userBean.deleteUser(nickName);
            return Response.ok().build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }
}