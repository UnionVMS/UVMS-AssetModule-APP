package eu.europa.ec.fisheries.uvms.rest.asset.service;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.message.event.UpdatedAssetEvent;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetMergeInfo;
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.json.bind.Jsonb;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

@ApplicationScoped
@Path("sse")
@RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
public class SSEResource {

    private final static Logger LOG = LoggerFactory.getLogger(SSEResource.class);

    private Sse sse;
    private OutboundSseEvent.Builder eventBuilder;
    private SseBroadcaster sseBroadcaster;
    private Jsonb jsonb = new JsonBConfigurator().getContext(null);

    @Context
    public void setSse(Sse sse) {
        this.sse = sse;
        this.eventBuilder = sse.newEventBuilder();
        this.sseBroadcaster = sse.newBroadcaster();
    }

    public void updatedAsset(@Observes(during = TransactionPhase.AFTER_SUCCESS) @UpdatedAssetEvent Asset asset){
        try {
            if (asset != null) {
                String outboundJson = jsonb.toJson(asset);
                OutboundSseEvent sseEvent = eventBuilder
                        .name("Updated Asset")
                        .id("" + System.currentTimeMillis())
                        .mediaType(MediaType.APPLICATION_JSON_PATCH_JSON_TYPE)
                        .data(String.class, outboundJson)
                        //.reconnectDelay(3000) //this one is optional and governs how long the client should wait b4 attempting to reconnect to this server
                        .comment("Updated Asset")
                        .build();
                sseBroadcaster.broadcast(sseEvent);
            }
        }catch (Exception e){
            LOG.error("Error while broadcasting SSE: ", e);
            throw new RuntimeException(e);
        }
    }

    public void mergeAsset(@Observes(during = TransactionPhase.AFTER_SUCCESS) @UpdatedAssetEvent AssetMergeInfo mergeInfo){
        try {
            if (mergeInfo != null) {
                String outboundJson = jsonb.toJson(mergeInfo);
                OutboundSseEvent sseEvent = eventBuilder
                        .name("Merged Asset")
                        .id("" + System.currentTimeMillis())
                        .mediaType(MediaType.APPLICATION_JSON_PATCH_JSON_TYPE)
                        .data(String.class, outboundJson)
                        //.reconnectDelay(3000) //this one is optional and governs how long the client should wait b4 attempting to reconnect to this server
                        .comment("Merged Asset")
                        .build();
                sseBroadcaster.broadcast(sseEvent);
            }
        }catch (Exception e){
            LOG.error("Error while broadcasting SSE: ", e);
            throw new RuntimeException(e);
        }
    }

    @GET
    @Path("subscribe")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void listen(@Context SseEventSink sseEventSink) {
        sseEventSink.send(sse.newEvent("Welcome to UVMS Asset SSE notifications."));
        sseBroadcaster.register(sseEventSink);
        sseEventSink.send(sse.newEvent("You are now registered for receiving updates to assets."));
    }
}
