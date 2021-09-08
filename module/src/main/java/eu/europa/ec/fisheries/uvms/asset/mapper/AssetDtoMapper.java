package eu.europa.ec.fisheries.uvms.asset.mapper;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.AssetDto;

import java.util.ArrayList;
import java.util.List;

public class AssetDtoMapper {

    private AssetDtoMapper() {}

    public static List<AssetDto> mapToAssetDtos(List<Asset> assets){
        List<AssetDto> dtos = new ArrayList<>(assets.size());
        for (Asset asset : assets) {
            dtos.add(mapToAssetDto(asset));
        }
        return dtos;
    }

    public static AssetDto mapToAssetDto(Asset asset){
        AssetDto dto = new AssetDto();

        dto.setActive(asset.getActive());
        dto.setAdministrativeDecisionDate(asset.getAdministrativeDecisionDate());
        dto.setAgentIsAlsoOwner(asset.getAgentIsAlsoOwner());
        dto.setAisIndicator(asset.getAisIndicator());
        dto.setAssetAgentAddress(asset.getAssetAgentAddress());
        dto.setCfr(asset.getCfr());
        dto.setComment(asset.getComment());
        dto.setCommissionDate(asset.getCommissionDate());
        dto.setConstructionPlace(asset.getConstructionPlace());
        dto.setConstructionYear(asset.getConstructionYear());
        dto.setCountryOfImportOrExport(asset.getCountryOfImportOrExport());
        dto.setErsIndicator(asset.getErsIndicator());
        dto.setEventCode(asset.getEventCode());
        dto.setExternalMarking(asset.getExternalMarking());
        dto.setFlagStateCode(asset.getFlagStateCode());
        dto.setGearFishingType(asset.getGearFishingType());
        dto.setGfcm(asset.getGfcm());
        dto.setGrossTonnage(asset.getGrossTonnage());
        dto.setGrossTonnageUnit(asset.getGrossTonnageUnit());
        dto.setHasLicence(asset.getHasLicence());
        dto.setHasVms(asset.getHasVms());
        dto.setHistoryId(asset.getHistoryId());
        dto.setHullMaterial(asset.getHullMaterial());
        dto.setIccat(asset.getIccat());
        dto.setId(asset.getId());
        dto.setImo(asset.getImo());
        dto.setIrcs(asset.getIrcs());
        dto.setIrcsIndicator(asset.getIrcsIndicator());
        dto.setLengthBetweenPerpendiculars(asset.getLengthBetweenPerpendiculars());
        dto.setLengthOverAll(asset.getLengthOverAll());
        dto.setLicenceType(asset.getLicenceType());
        dto.setMainFishingGearCode(asset.getMainFishingGearCode());
        dto.setMmsi(asset.getMmsi());
        dto.setName(asset.getName());
        dto.setOtherTonnage(asset.getOtherTonnage());
        dto.setOwnerAddress(asset.getOwnerAddress());
        dto.setOwnerName(asset.getOwnerName());
        dto.setPortOfRegistration(asset.getPortOfRegistration());
        dto.setPowerOfAuxEngine(asset.getPowerOfAuxEngine());
        dto.setPowerOfMainEngine(asset.getPowerOfMainEngine());
        dto.setProdOrgCode(asset.getProdOrgCode());
        dto.setProdOrgName(asset.getProdOrgName());
        dto.setPublicAid(asset.getPublicAid());
        dto.setRegistrationNumber(asset.getRegistrationNumber());
        dto.setSafteyGrossTonnage(asset.getSafteyGrossTonnage());
        dto.setSegment(asset.getSegment());
        dto.setSegmentOfAdministrativeDecision(asset.getSegmentOfAdministrativeDecision());
        dto.setSource(asset.getSource());
        dto.setSubFishingGearCode(asset.getSubFishingGearCode());
        dto.setTypeOfExport(asset.getTypeOfExport());
        dto.setUpdatedBy(asset.getUpdatedBy());
        dto.setUpdateTime(asset.getUpdateTime());
        dto.setUvi(asset.getUvi());
        dto.setVesselDateOfEntry(asset.getVesselDateOfEntry());
        dto.setVesselType(asset.getVesselType());
        dto.setVmsIndicator(asset.getVmsIndicator());

        return dto;
    }
}
