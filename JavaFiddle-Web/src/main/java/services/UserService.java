package ru.javafiddle.web.services;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(UserRegistrationData userRegistrationData, @Context UriInfo uriInfo) throws UriBuilderException {

        User newUser;

        try {
            newUser = userBean.register(userRegistrationData);
        } catch (InvalidArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(userRegistrationData).build();//!TODO make it possible to detect which field is invalid
        }

        String nickName = String.valueOf(newUser.getNickname());
        URI uri = uriInfo.getAbsolutePathBuilder().path(nickName).build();
        return Response.created(uri)
                .entity(newUser)
                .build();
    }

    @GET
    @Path("/{nickName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserInfo(@PathParam("nickName") String nickName) {

        User user;

        try {
            user = userBean.getUserInfo(nickName);
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(user).build();
    }

    @PUT
    @Path("/{nickName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUserInfo(@PathParam("nickName") String nickName, UserRegistrationData userRegistrationData) {

        User user;

        try {
            user = userBean.setUserInfo(nickName, userRegistrationData);
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(user).build();
    }

    @DELETE
    @Path("/{nickName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("nickName") String nickName) {

        User user;

        try {
            user = userBean.deleteUser(nickName);
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(user).build();
    }
}