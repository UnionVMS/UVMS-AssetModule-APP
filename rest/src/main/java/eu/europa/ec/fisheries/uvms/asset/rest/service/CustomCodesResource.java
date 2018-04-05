package eu.europa.ec.fisheries.uvms.asset.rest.service;


import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCodes;
import eu.europa.ec.fisheries.uvms.asset.service.CustomCodesService;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/customcodes")
@Stateless
@RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
@Api(value = "CustomCodes Service")
public class CustomCodesResource {


    private static final Logger LOG = LoggerFactory.getLogger(ConfigResource.class);

    @Inject
    private CustomCodesService customCodesSvc;

    @POST
    @ApiOperation(value = "Create a record", notes = "Create a custom constant code", response = CustomCodes.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when createing custom code"),
            @ApiResponse(code = 200, message = "Success when createing custom code") })
    @Path("/{constant}/{code}/{description}/{embeddedjson}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response createCustomCode(
            @ApiParam(value = "constant", required = true)  @PathParam(value="constant") String constant
            ,@ApiParam(value = "code", required = true)  @PathParam(value="code")  String code
            ,@ApiParam(value = "description", required = true)  @PathParam(value="description")  String description
            , @ApiParam(value = "embeddedjson", required = true)  @PathParam(value="embeddedjson") String embeddedjson) {
        try {
             CustomCodes customCodes = customCodesSvc.create(constant,code,description,embeddedjson);
            return Response.ok(customCodes).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }




    @GET
    @ApiOperation(value = "Get a list of constants", notes = "Get a list of constants from Custom Code", response = String.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving constant list"),
            @ApiResponse(code = 200, message = "Constants successfully retrieved") })
    @Path("/")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getAllConstants() {
        try {
            List<String> constants = customCodesSvc.getAllConstants();
            return Response.ok(constants).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @ApiOperation(value = "Get a list of codes for given", notes = "Returned as json parse tree in client´´", response = String.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving code list for given constant list"),
            @ApiResponse(code = 200, message = "Codes for constant  successfully retrieved") })
    @Path("/getcodesforconstant/{constant}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getCodesForConstant( @PathParam("constant") String constant) {
        try {
            List<CustomCodes> customCodes = customCodesSvc.getAllFor(constant);
            return Response.ok(customCodes).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }



}
