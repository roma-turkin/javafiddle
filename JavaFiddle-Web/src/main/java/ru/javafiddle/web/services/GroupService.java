package ru.javafiddle.web.services;

import ru.javafiddle.ejb.beans.GroupsBean;
import ru.javafiddle.ejb.beans.ProjectBean;

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
@Path("/")
public class GroupService {

    @EJB
    GroupsBean groupsBean;

    @EJB
    ProjectBean projectBean;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewGroupMember(GroupMember newGroupMember, @PathParam("projectHash") String projectHash) {

        try{

            int groupId = projectBean.getGroupId(projectHash);
            groupsBean.addMember(groupId,
                    newGroupMember.getUserNickName(),
                    newGroupMember.getAccessRights());

            return Response.ok().build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroupMembers(@PathParam("projectHash") String projectHash) {

        try{

            int groupId = projectBean.getGroupId(projectHash);
            //Map<nickname, access>
            Map<String, String> groupMembers = groupsBean.getAllMembers(groupId);

            return Response.ok(groupMembers).build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteGroupMember(GroupMember groupMember, @PathParam("projectHash") String projectHash) {

        try{

            int groupId         = projectBean.getGroupId(projectHash);
            String userNickName = groupMember.getUserNickName();
            groupsBean.deleteMember(groupId,
                    userNickName);

            return Response.ok().build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateGroupMember(GroupMember groupMember, @PathParam("projectHash") String projectHash) {

        try{

            int groupId = projectBean.getGroupId(projectHash);
            groupsBean.updateMember(groupId,
                    groupMember.getUserNickName(),
                    groupMember.getAccessRights());

            return Response.ok().build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

}
