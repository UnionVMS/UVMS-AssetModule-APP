package eu.europa.ec.fisheries.uvms.asset.client;

import eu.europa.ec.fisheries.uvms.asset.client.model.*;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;

import javax.ejb.Local;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Local
public interface AssetClient {
    AssetDTO getAssetById(AssetIdentifier type, String value);

    List<AssetDTO> getAssetList(AssetQuery query);

    List<AssetDTO> getAssetList(AssetQuery query, boolean dynamic);

    List<AssetDTO> getAssetList(AssetQuery query, int page, int size, boolean dynamic);

    List<AssetGroup> getAssetGroupsByUser(String user);

    List<AssetGroup> getAssetGroupByAssetId(UUID assetId);

    List<AssetDTO> getAssetsByGroupIds(List<UUID> groupIds);

    AssetBO upsertAsset(AssetBO asset);

    void upsertAssetAsync(AssetBO asset) throws MessageException;

    String ping();

    CustomCode createCustomCode(CustomCode customCode);

    List<String> getConstants();

    List<CustomCode> getCodesForConstant(String constant);

    Boolean isCodeValid(String constant, String code, OffsetDateTime date);

    List<CustomCode> getCodeForDate(String constant, String code, OffsetDateTime date);

    CustomCode replace(CustomCode customCode);

    AssetMTEnrichmentResponse collectAssetMT(AssetMTEnrichmentRequest request);
}
