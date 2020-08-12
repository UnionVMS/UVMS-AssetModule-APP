package eu.europa.ec.fisheries.uvms.rest.asset;

import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("movement/rest/internal")
@Stateless
public class MovementMock {

    @PUT
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/remapMovementConnectInMovement")
    public Response remapMovementConnectInMovement(@QueryParam(value = "MovementConnectFrom") String movementConnectFrom, @QueryParam(value = "MovementConnectTo") String movementConnectTo) {

        return Response.ok(55).build();
    }

    @DELETE
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/removeMovementConnect")
    public Response removeMovementConnect(@QueryParam(value = "MovementConnectId") String movementConnectId) {

        return Response.ok().build();
    }
}
