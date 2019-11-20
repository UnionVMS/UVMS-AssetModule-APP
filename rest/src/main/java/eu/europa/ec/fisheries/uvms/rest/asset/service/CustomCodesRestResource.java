package eu.europa.ec.fisheries.uvms.rest.asset.service;

import eu.europa.ec.fisheries.uvms.asset.CustomCodesService;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCodesPK;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import io.swagger.annotations.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Path("/customcodes")
@Stateless
@RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
@Api(value = "CustomCodes Service")
@Consumes(value = {MediaType.APPLICATION_JSON})
@Produces(value = {MediaType.APPLICATION_JSON})
public class CustomCodesRestResource {

    private static final Logger LOG = LoggerFactory.getLogger(CustomCodesRestResource.class);

    @Inject
    private CustomCodesService customCodesSvc;

    @POST
    @ApiOperation(value = "Create a record", notes = "Create a custom constants code", response = CustomCode.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when create custom code"),
            @ApiResponse(code = 200, message = "Success when create custom code")})
    public Response createCustomCode(
            @ApiParam(value = "customCode", required = true) CustomCode customCode) throws Exception {
        try {
            CustomCode createdCustomCode = customCodesSvc.create(customCode);
            return Response.ok(createdCustomCode).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            throw e;
        }
    }

    @PUT
    @ApiOperation(value = "Store latest permutation of a customCode, original is destroyed", notes = "replace", response = CustomCode.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when createing custom code"),
            @ApiResponse(code = 200, message = "Success when createing custom code")})
    @Path("replace")
    public Response replace(
            @ApiParam(value = "customCode", required = true) CustomCode customCode)  throws Exception {
        try {
            CustomCode replacedCustomCode = customCodesSvc.replace(customCode);
            return Response.ok(replacedCustomCode).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            throw e;
        }
    }

    @GET
    @ApiOperation(value = "Retrieve a customcode", notes = "Retrieve a customcode", response = CustomCode.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving code list for given constants list"),
            @ApiResponse(code = 200, message = "Codes for constants  successfully retrieved")})
    @Path("/{constant}/{code}")
    public Response retrieveCustomCode(
            @ApiParam(value = "constants", required = true) @PathParam("constant") String constant,
            @ApiParam(value = "code", required = true) @PathParam("code") String code,
            @ApiParam(value = "validFromDate", required = true) @QueryParam(value = "validFromDate")  String   validFromDate,
            @ApiParam(value = "validToDate", required = true) @QueryParam(value = "validToDate") String validToDate)  throws Exception
    {
        try {
            OffsetDateTime fromDate = (validFromDate == null ? CustomCodesPK.STANDARD_START_DATE : OffsetDateTime.parse(validFromDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            OffsetDateTime toDate = (validToDate == null ? CustomCodesPK.STANDARD_END_DATE : OffsetDateTime.parse(validToDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            CustomCode customCode = customCodesSvc.get(constant,code,fromDate,toDate);
            return Response.ok(customCode).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when fetching CustomCode. " + validFromDate + " " +  validToDate);
            throw e;
        }
    }

    @GET
    @ApiOperation(value = "Check if customCode exists", notes = "Check if customCode exists", response = Boolean.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving code list for given constants list"),
            @ApiResponse(code = 200, message = "Codes for constants  successfully retrieved")})
    @Path("/{constant}/{code}/exists")
    public Response exists(@ApiParam(value = "constants", required = true) @PathParam("constant") String constant,
                           @ApiParam(value = "code", required = true) @PathParam("code") String code,
                           @ApiParam(value = "validFromDate", required = true) @QueryParam(value = "validFromDate") String validFromDate,
                           @ApiParam(value = "validToDate", required = true) @QueryParam(value = "validToDate") String validToDate)  throws Exception
    {
        try {

            OffsetDateTime fromDate = (validFromDate == null ? CustomCodesPK.STANDARD_START_DATE : OffsetDateTime.parse(validFromDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            OffsetDateTime toDate = (validToDate == null ? CustomCodesPK.STANDARD_END_DATE : OffsetDateTime.parse(validToDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            CustomCodesPK pk = new CustomCodesPK();
            pk.setConstant(constant);
            pk.setCode(code);
            pk.setValidFromDate(fromDate);
            pk.setValidToDate(toDate);
            Boolean exists = customCodesSvc.exists(constant, code,fromDate,toDate);

            return Response.status(200).entity(exists).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            throw e;
        }
    }

    @GET
    @ApiOperation(value = "retrieve Customcode for specified date", notes = "retrieve Customcode for specified date", response = CustomCode.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when processing request"),
            @ApiResponse(code = 200, message = "Successfully proccessed request")})
    @Path("/{constant}/{code}/getfordate")
    public Response getForDate(@ApiParam(value = "constants", required = true) @PathParam("constant") String constant,
                           @ApiParam(value = "code", required = true) @PathParam("code") String code,
                           @ApiParam(value = "validToDate", required = true) @QueryParam(value = "date") String date)  throws Exception
    {
        try {

            OffsetDateTime aDate = (date == null ? OffsetDateTime.now() : OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            List<CustomCode> customCodes = customCodesSvc.getForDate(constant, code,aDate);

            return Response.ok(customCodes).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            throw e;
        }
    }

    @GET
    @ApiOperation(value = "Check if a Custom Code exists for given date", notes = "Check if a Custom Code exists for given date", response = Boolean.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when processing request"),
            @ApiResponse(code = 200, message = "Successfully proccessed request")})
    @Path("/{constant}/{code}/verify")
    public Response verify(@ApiParam(value = "constants", required = true) @PathParam("constant") String constant,
                               @ApiParam(value = "code", required = true) @PathParam("code") String code,
                               @ApiParam(value = "validToDate", required = true) @QueryParam(value = "date") String date)  throws Exception
    {
        try {

            OffsetDateTime aDate = (date == null ? OffsetDateTime.now() : OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            Boolean exists = customCodesSvc.verify(constant, code, aDate);

            return Response.ok(exists).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            throw e;
        }
    }

    @GET
    @ApiOperation(value = "Get a list of constants", notes = "Get a list of constants from Custom Code", response = String.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving constants list"),
            @ApiResponse(code = 200, message = "Constants successfully retrieved")})
    @Path("/listconstants")
    public Response getAllConstants()  throws Exception{
        try {
            List<String> constants = customCodesSvc.getAllConstants();
            return Response.ok(constants).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            throw e;
        }
    }

    @GET
    @ApiOperation(value = "Get a list of codes for given", notes = "Returned as json parse tree in client", response = String.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving code list for given constants list"),
            @ApiResponse(code = 200, message = "Codes for constants  successfully retrieved")})
    @Path("/listcodesforconstant/{constant}")
    public Response getCodesForConstant(@PathParam("constant") String constant)   throws Exception{
        try {
            List<CustomCode> customCodes = customCodesSvc.getAllFor(constant);
            return Response.ok(customCodes).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            throw e;
        }
    }

    @DELETE
    @ApiOperation(value = "Remove a customcode", notes = "Remove a customcode", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving code list for given constants list"),
            @ApiResponse(code = 200, message = "Codes for constants  successfully retrieved")})
    @Path("/{constant}/{code}")
    public Response deleteCustomCode(@ApiParam(value = "constants", required = true) @PathParam("constant") String constant,
                                     @ApiParam(value = "code", required = true) @PathParam("code") String code,
                                     @ApiParam(value = "validFromDate", required = true) @QueryParam(value = "validFromDate") String validFromDate,
                                     @ApiParam(value = "validToDate", required = true) @QueryParam(value = "validToDate") String validToDate)  throws Exception
    {
        try {

            OffsetDateTime fromDate = (validFromDate == null ? CustomCodesPK.STANDARD_START_DATE : OffsetDateTime.parse(validFromDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            OffsetDateTime toDate = (validToDate == null ? CustomCodesPK.STANDARD_END_DATE : OffsetDateTime.parse(validToDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            customCodesSvc.delete(constant, code,fromDate,toDate);
            return Response.ok().header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            throw e;
        }
    }
}
