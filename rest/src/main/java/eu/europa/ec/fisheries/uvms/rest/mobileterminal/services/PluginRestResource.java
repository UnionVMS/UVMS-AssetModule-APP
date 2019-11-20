package eu.europa.ec.fisheries.uvms.rest.mobileterminal.services;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.ConfigServiceBeanMT;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/plugin")
@Stateless
@Consumes(value = { MediaType.APPLICATION_JSON })
@Produces(value = { MediaType.APPLICATION_JSON })
public class PluginRestResource {
    private static final Logger LOG = LoggerFactory.getLogger(PluginRestResource.class);

    @Inject
    private ConfigServiceBeanMT configServiceMT;

    @Context
    private HttpServletRequest request;

    @POST
    @Path("/")
    @RequiresFeature(UnionVMSFeature.mobileTerminalPlugins)
    public Response upsertPlugins(List<PluginService> pluginServiceList){
        try {
            List<MobileTerminalPlugin> pluginList = configServiceMT.upsertPlugins(pluginServiceList, request.getRemoteUser());
            return Response.ok(pluginList).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error while upserting plugins ] {}", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(ex)).build();
        }
    }

    @GET
    @Path("/plugins")
    public Response getPlugins() {
        try {
            List<MobileTerminalPlugin> list = configServiceMT.getMobileTerminalPlugins();
            return Response.ok(list).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when getting plugins ] {}", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(ex)).build();
        }
    }
}
