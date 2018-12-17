package eu.europa.ec.fisheries.uvms.asset.ejb.client;


import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface IAssetFacade {

    List<Asset> findHistoryOfAssetByCfr(String cfr);
    List<Asset> findHistoryOfAssetBy(String reportDate, String cfr, String regCountry, String ircs, String extMark, String iccat);

}
