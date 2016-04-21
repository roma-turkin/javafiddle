package ru.javafiddle.web.services;

import ru.javafiddle.core.ejb.AccessBean;
import ru.javafiddle.core.ejb.GroupBean;
import ru.javafiddle.core.ejb.ProjectBean;

import ru.javafiddle.core.ejb.UserBean;
import ru.javafiddle.jpa.entity.Access;
import ru.javafiddle.jpa.entity.Group;
import ru.javafiddle.jpa.entity.Project;
import ru.javafiddle.jpa.entity.User;
import ru.javafiddle.web.models.GroupMember;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * Created by artyom on 24.11.15.
 */
 
public class GroupService {

    @EJB
    GroupBean groupBean;

    @EJB
    ProjectBean projectBean;

    @EJB
    UserBean userBean;

    @EJB
    AccessBean accessBean;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewGroupMember(GroupMember newGroupMember, @PathParam("projectHash") String projectHash) {

        Project project = projectBean.getProjectByProjectHash(projectHash);
        if (project == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("There's no project with hash " + projectHash)
                    .build();
        }

        Group group = project.getGroup();

        User user = userBean.getUser(newGroupMember.getUserNickName());
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("There's no user with nick " + newGroupMember.getUserNickName())
                    .build();
        }

        Access access = accessBean.getAccess(newGroupMember.getAccessRights());
        if (access == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("There's no such access " + newGroupMember.getAccessRights())
                    .build();
        }

        try {
            groupBean.addMember(group, user, access);
        } catch (Exception e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroupMembers(@PathParam("projectHash") String projectHash) {

        Project project = projectBean.getProjectByProjectHash(projectHash);
        if (project == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("There's no project with hash " + projectHash)
                    .build();
        }

        //Map<nickname, access>
        Map<String, String> groupMembers = groupBean.getMemberAccessMap(project.getGroup().getGroupId());

        return Response.ok(groupMembers).build();
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteGroupMember(GroupMember groupMember, @PathParam("projectHash") String projectHash) {

        Project project = projectBean.getProjectByProjectHash(projectHash);
        if (project == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("There's no project with hash " + projectHash)
                    .build();
        }

        groupBean.deleteMember(project.getGroup().getGroupId(), groupMember.getUserNickName());

        return Response.ok().build();
    }

    //there's no method in GroupBean, which allows us to implement this functionality
    //!TODO when GROUPBEAN will be implemented
//    @PUT
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Response updateGroupMember(GroupMember groupMember, @PathParam("projectHash") String projectHash) {
//
//        groupBean.
//
//        try{
//
//            int groupId = projectBean.getGroupId(projectHash);
//            groupsBean.updateMember(groupId,
//                    groupMember.getUserNickName(),
//                    groupMember.getAccessRights());
//
//            return Response.ok().build();
//
//        } catch (NotFoundException e) {
//            return Response.status(Response.Status.BAD_REQUEST).build();
//        } catch (Exception e) {
//            return Response.serverError().build();
//        }
//    }

}
