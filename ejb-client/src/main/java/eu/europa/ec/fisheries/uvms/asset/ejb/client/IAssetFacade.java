package eu.europa.ec.fisheries.uvms.asset.ejb.client;


import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;

import java.util.List;

public interface IAssetFacade {
    List<Asset> findHistoryOfAssetByCfr(String cfr);
    List<Asset> findHistoryOfAssetBy(String reportDate, String cfr, String regCountry, String ircs, String extMark, String iccat,String uvi);
    ListAssetResponse getAssetList(AssetListQuery query);
    List<Asset> getAssetGroup(List<AssetGroup> assetGroupQuery);
}
