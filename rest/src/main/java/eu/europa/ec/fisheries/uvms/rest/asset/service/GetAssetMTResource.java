package eu.europa.ec.fisheries.uvms.rest.asset.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.uvms.asset.bean.AssetMTBean;
import eu.europa.ec.fisheries.uvms.asset.dto.SpatialAssetMTEnrichmentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/assetMTBean")
@Stateless
public class GetAssetMTResource {

    private static final Logger LOG = LoggerFactory.getLogger(AssetResource.class);

    private ObjectMapper MAPPER = new ObjectMapper();

    @Inject
    AssetMTBean assetMTBean;


    //@ formatter:off

    /**
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Gets a specific asset revision by history id
     */
    @GET
    @Path("enrich")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response enrich(
            @DefaultValue("") @QueryParam("movementsourcename") String movementSourceName,
            @DefaultValue("") @QueryParam("plugintype") String rawMovementPluginType,

            @DefaultValue("") @QueryParam("cfr") String assetidtype_cfr,
            @DefaultValue("") @QueryParam("ircs") String assetidtype_ircs,
            @DefaultValue("") @QueryParam("imo") String assetidtype_imo,
            @DefaultValue("") @QueryParam("mmsi") String assetidtype_mmsi,

            @DefaultValue("") @QueryParam("serialnumber") String mobtermidtype_serialnumber,
            @DefaultValue("") @QueryParam("les") String mobtermidtype_les,
            @DefaultValue("") @QueryParam("dnid") String mobtermidtype_dnid,
            @DefaultValue("") @QueryParam("membernumber") String mobtermidtype_membernumber

    ) {
        try {
            SpatialAssetMTEnrichmentResponse response  = assetMTBean.getRequiredEnrichment(
                    movementSourceName,
                    rawMovementPluginType,
                    assetidtype_cfr,
                    assetidtype_ircs,
                    assetidtype_imo,
                    assetidtype_mmsi,
                    mobtermidtype_serialnumber,
                    mobtermidtype_les,
                    mobtermidtype_dnid,
                    mobtermidtype_membernumber);
            return Response.ok(response).build();
        } catch (Exception e) {
            LOG.error(e.toString(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(/*"Here I am: " +  */e /*+ e.getStackTrace()*/).build();
        }
    }
    //@ formatter:on

}
