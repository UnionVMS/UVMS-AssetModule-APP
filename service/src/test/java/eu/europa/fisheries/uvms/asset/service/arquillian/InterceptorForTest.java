package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.types.AssetFault;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetSuccessfulTestEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.SuccessfulTestEvent;

import javax.ejb.LocalBean;
import javax.enterprise.event.Observes;
import javax.inject.Singleton;

/**
 * *
 */


@LocalBean
@Singleton
public class InterceptorForTest {


    private boolean failed;
    private AssetFault assetFault;
    private SuccessfulTestEvent successfulTestEvent;

    public void listenForAssetMessageErrorEvent(@Observes @AssetMessageErrorEvent AssetMessageEvent message) {
        failed = true;
//        assetFault = message.getFault();
    }

    public void listenForAssetSuccessfulTestEvent(@Observes @AssetSuccessfulTestEvent SuccessfulTestEvent successfulTestEvent) {
        this.successfulTestEvent = successfulTestEvent;
    }

    public boolean isFailed() {
        return failed;
    }

    public AssetFault getAssetFault() {
        return assetFault;
    }

    public SuccessfulTestEvent getSuccessfulTestEvent() {

        return successfulTestEvent;
    }

    public void recycle() {
        failed = false;
        assetFault = null;
        successfulTestEvent = null;
    }


}
