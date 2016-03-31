package ru.javafiddle.web.services;

import ru.javafiddle.core.ejb.ProjectBean;
import ru.javafiddle.core.ejb.UserBean;
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
@Path("/user")
public class UserService {

    @EJB
    private UserBean userBean;

    @EJB
    private ProjectBean projectBean;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(UserRegistrationData userRegistrationData, @Context UriInfo uriInfo) throws UriBuilderException {

        User toBeRegisteredUser = new User();
        //!TODO implement class or method with such conversion
        toBeRegisteredUser.setNickName(userRegistrationData.getNickName());
        toBeRegisteredUser.setFirstName(userRegistrationData.getFirstName());
        toBeRegisteredUser.setLastName(userRegistrationData.getLastName());
        toBeRegisteredUser.setEmail(userRegistrationData.getEmail());
        toBeRegisteredUser.setPasswordHash(userRegistrationData.getPassword());

        User registerdedUser = userBean.register(toBeRegisteredUser);

        if (registerdedUser == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        String nickName = registerdedUser.getNickName();
        URI uri = uriInfo.getAbsolutePathBuilder().path(nickName).build();

        return Response.created(uri).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserInfo() {

        String nickName = userBean.getCurUserNick();

        User user = userBean.getUser(nickName);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        //!TODO implement class or method with such conversion
        UserJF userJF = new UserJF(user.getFirstName(),
                user.getLastName(),
                user.getNickName(),
                user.getEmail(),
                user.getRegistrationDate(),
                projectBean.getUserProjects(user),
                user.getStatus().getStatusName());

        return Response.ok(userJF).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserInfo(UserRegistrationData userRegistrationData) {

        String nickName = userBean.getCurUserNick();

        User toBeUpdatedUser = userBean.getUser(nickName);

        if (toBeUpdatedUser == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        //!TODO implement class or method with such conversion
        toBeUpdatedUser.setNickName(userRegistrationData.getNickName());
        toBeUpdatedUser.setFirstName(userRegistrationData.getFirstName());
        toBeUpdatedUser.setLastName(userRegistrationData.getLastName());
        toBeUpdatedUser.setEmail(userRegistrationData.getEmail());
        toBeUpdatedUser.setPasswordHash(userRegistrationData.getPassword());


        userBean.updateUser(toBeUpdatedUser);

        return Response.ok().build();
    }

    @DELETE
    public Response deleteUser() {

        String nickName = userBean.getCurUserNick();

        User toBeDeletedUser = userBean.getUser(nickName);

        if (toBeDeletedUser == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        userBean.deleteUser(toBeDeletedUser);

        return Response.ok().build();
    }
}