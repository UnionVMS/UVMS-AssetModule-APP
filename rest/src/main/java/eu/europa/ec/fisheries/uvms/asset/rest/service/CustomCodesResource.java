package eu.europa.ec.fisheries.uvms.asset.rest.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCodesPK;
import eu.europa.ec.fisheries.uvms.asset.service.CustomCodesService;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.reflections.util.ConfigurationBuilder.build;

@Path("/customcodes")
@Stateless
@RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
@Api(value = "CustomCodes Service")
public class CustomCodesResource {

    ObjectMapper MAPPER;
    public CustomCodesResource() {
        MAPPER = new ObjectMapper();
        MAPPER.registerModule(new JavaTimeModule());
    }



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
            @ApiParam(value = "validFromDate", required = true) @PathParam(value = "validFromDate")  String   validFromDate,
            @ApiParam(value = "validToDate", required = true) @PathParam(value = "validToDate") String validToDate)
    {
        try {
            LocalDateTime fromDate = LocalDateTime.parse(validFromDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime toDate = LocalDateTime.parse(validToDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            CustomCode customCode = customCodesSvc.get(constant,code,fromDate,toDate);
            String json = MAPPER.writeValueAsString(customCode);
            return Response.status(200).entity(json).type(MediaType.APPLICATION_JSON)
                    .header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when fetching CustomCode. " + validFromDate + " " +  validToDate);
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
                           @ApiParam(value = "validFromDate", required = true) @PathParam(value = "validFromDate") String validFromDate,
                           @ApiParam(value = "validToDate", required = true) @PathParam(value = "validToDate") String validToDate)
    {
        try {

            LocalDateTime fromDate = LocalDateTime.parse(validFromDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime toDate = LocalDateTime.parse(validToDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            CustomCodesPK pk = new CustomCodesPK();
            pk.setConstant(constant);
            pk.setCode(code);
            pk.setValidFromDate(fromDate);
            pk.setValidToDate(toDate);
            Boolean exists = customCodesSvc.exists(constant, code,fromDate,toDate);

            return Response.status(200).entity(exists).type(MediaType.APPLICATION_JSON)
                    .header("MDC", MDC.get("requestId")).build();

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
            String json = MAPPER.writeValueAsString(customCodes);
            return Response.ok(json).build();
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
                                     @ApiParam(value = "validFromDate", required = true) @PathParam(value = "validFromDate") String validFromDate,
                                     @ApiParam(value = "validToDate", required = true) @PathParam(value = "validToDate") String validToDate)
    {
        try {

            LocalDateTime fromDate = LocalDateTime.parse(validFromDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime toDate = LocalDateTime.parse(validToDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            customCodesSvc.delete(constant, code,fromDate,toDate);
            return Response.ok().build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


}
