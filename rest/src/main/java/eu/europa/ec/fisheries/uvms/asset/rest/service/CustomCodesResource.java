package eu.europa.ec.fisheries.uvms.asset.rest.service;


import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCodesPK;
import eu.europa.ec.fisheries.uvms.asset.service.CustomCodesService;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Path("/customcodes")
@Stateless
@RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
@Api(value = "CustomCodes Service")
public class CustomCodesResource {


    private static final Logger LOG = LoggerFactory.getLogger(ConfigResource.class);

    @Inject
    private CustomCodesService customCodesSvc;

    @POST
    @ApiOperation(value = "Create a record", notes = "Create a custom constant code", response = CustomCode.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when createing custom code"),
            @ApiResponse(code = 200, message = "Success when createing custom code")})
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response createCustomCode(
             @ApiParam(value = "customCode", required = true) CustomCode customCode) {
        try {

            CustomCode customCodes = customCodesSvc.create(customCode);
            return Response.ok(customCodes).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @ApiOperation(value = "Retrieve a customcode", notes = "Retrieve a customcode", response = CustomCode.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving code list for given constant list"),
            @ApiResponse(code = 200, message = "Codes for constant  successfully retrieved")})
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/{constant}/{code}/{validFromDate}/{validToDate}")
    public Response retrieveCustomCode(
            @ApiParam(value = "constant", required = true) @PathParam("constant") String constant,
            @ApiParam(value = "code", required = true) @PathParam("code") String code,
            @ApiParam(value = "validFromDate", required = true) @PathParam(value = "validFromDate")  Date   validFromDate,
            @ApiParam(value = "validToDate", required = true) @PathParam(value = "validToDate") Date validToDate)
    {
        try {

            CustomCodesPK pk = new CustomCodesPK();
            pk.setConstant(constant);
            pk.setCode(code);

            LocalDateTime f = Instant.ofEpochMilli(validFromDate.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            LocalDateTime t = Instant.ofEpochMilli(validToDate.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();


            pk.setValidFromDate(f);
            pk.setValidToDate(t);



            CustomCode customCode = customCodesSvc.get(pk);
            return Response.ok(customCode).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @ApiOperation(value = "Retrieve a customcode", notes = "Retrieve a customcode", response = Boolean.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving code list for given constant list"),
            @ApiResponse(code = 200, message = "Codes for constant  successfully retrieved")})
    @Path("/exists/{constant}/{code}/{validFromDate}/{validToDate}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response exists(@ApiParam(value = "constant", required = true) @PathParam("constant") String constant,
                           @ApiParam(value = "code", required = true) @PathParam("code") String code,
                           @ApiParam(value = "validFromDate", required = true) @PathParam(value = "validFromDate") Date validFromDate,
                           @ApiParam(value = "validToDate", required = true) @PathParam(value = "validToDate") Date validToDate)
    {
        try {

            LocalDateTime fromDate = Instant.ofEpochMilli(validFromDate.getTime())
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime();
            LocalDateTime toDate = Instant.ofEpochMilli(validToDate.getTime())
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime();


            Boolean exists = customCodesSvc.exists(constant, code,fromDate,toDate);
            return Response.ok(exists).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


    @GET
    @ApiOperation(value = "Get a list of constants", notes = "Get a list of constants from Custom Code", response = String.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving constant list"),
            @ApiResponse(code = 200, message = "Constants successfully retrieved")})
    @Path("/listconstants")
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
    @ApiOperation(value = "Get a list of codes for given", notes = "Returned as json parse tree in client", response = String.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving code list for given constant list"),
            @ApiResponse(code = 200, message = "Codes for constant  successfully retrieved")})
    @Path("/listcodesforconstant/{constant}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getCodesForConstant(@PathParam("constant") String constant) {
        try {
            List<CustomCode> customCodes = customCodesSvc.getAllFor(constant);
            return Response.ok(customCodes).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @ApiOperation(value = "Remove a customcode", notes = "Remove a customcode", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving code list for given constant list"),
            @ApiResponse(code = 200, message = "Codes for constant  successfully retrieved")})
    @Path("/{constant}/{code}{validFromDate}{validToDate}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response deleteCustomCode(@ApiParam(value = "constant", required = true) @PathParam("constant") String constant,
                                     @ApiParam(value = "code", required = true) @PathParam("code") String code,
                                     @ApiParam(value = "validFromDate", required = true) @PathParam(value = "validFromDate") Date validFromDate,
                                     @ApiParam(value = "validToDate", required = true) @PathParam(value = "validToDate") Date validToDate

    )
    {
        try {

            LocalDateTime fromDate = Instant.ofEpochMilli(validFromDate.getTime())
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime();
            LocalDateTime toDate = Instant.ofEpochMilli(validToDate.getTime())
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime();

            customCodesSvc.delete(constant, code,fromDate,toDate);
            return Response.ok().build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


}
