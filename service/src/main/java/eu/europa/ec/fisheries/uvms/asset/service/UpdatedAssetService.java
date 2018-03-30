package eu.europa.ec.fisheries.uvms.asset.service;

import javax.ejb.Local;

@Local
public interface UpdatedAssetService {
    void assetWasUpdated(String cfr);
}
