/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.asset.util;

import java.util.Objects;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;

public class AssetComparator {

    private AssetComparator() {}
    
    // Compare all attributes except id, historyId, updateTime, updatedBy and eventCode (set by server)
    public static boolean assetEquals(Asset asset1, Asset asset2) {
        if (asset1 == null || asset2 == null) {
            return false;
        }
        if (asset1 == asset2) {
            return true;
        }
        return Objects.equals(asset1.getIrcsIndicator(), asset2.getIrcsIndicator()) &&
                Objects.equals(asset1.getErsIndicator(), asset2.getErsIndicator()) &&
                Objects.equals(asset1.getAisIndicator(), asset2.getAisIndicator()) &&
                Objects.equals(asset1.getVmsIndicator(), asset2.getVmsIndicator()) &&
                Objects.equals(asset1.getHullMaterial(), asset2.getHullMaterial()) &&
                Objects.equals(asset1.getCommissionDate(), asset2.getCommissionDate()) &&
                Objects.equals(asset1.getConstructionYear(), asset2.getConstructionYear()) &&
                Objects.equals(asset1.getConstructionPlace(), asset2.getConstructionPlace()) &&
                Objects.equals(asset1.getSource(), asset2.getSource()) &&
                Objects.equals(asset1.getVesselType(), asset2.getVesselType()) &&
                Objects.equals(asset1.getVesselDateOfEntry(), asset2.getVesselDateOfEntry()) &&
                Objects.equals(asset1.getCfr(), asset2.getCfr()) &&
                Objects.equals(asset1.getImo(), asset2.getImo()) &&
                Objects.equals(asset1.getIrcs(), asset2.getIrcs()) &&
                Objects.equals(asset1.getMmsi(), asset2.getMmsi()) &&
                Objects.equals(asset1.getIccat(), asset2.getIccat()) &&
                Objects.equals(asset1.getUvi(), asset2.getUvi()) &&
                Objects.equals(asset1.getGfcm(), asset2.getGfcm()) &&
                Objects.equals(asset1.getActive(), asset2.getActive()) &&
                Objects.equals(asset1.getFlagStateCode(), asset2.getFlagStateCode()) &&
                Objects.equals(asset1.getName(), asset2.getName()) &&
                Objects.equals(asset1.getExternalMarking(), asset2.getExternalMarking()) &&
                Objects.equals(asset1.getAgentIsAlsoOwner(), asset2.getAgentIsAlsoOwner()) &&
                Objects.equals(asset1.getLengthOverAll(), asset2.getLengthOverAll()) &&
                Objects.equals(asset1.getLengthBetweenPerpendiculars(), asset2.getLengthBetweenPerpendiculars()) &&
                Objects.equals(asset1.getSafteyGrossTonnage(), asset2.getSafteyGrossTonnage()) &&
                Objects.equals(asset1.getOtherTonnage(), asset2.getOtherTonnage()) &&
                Objects.equals(asset1.getGrossTonnage(), asset2.getGrossTonnage()) &&
                asset1.getGrossTonnageUnit() == asset2.getGrossTonnageUnit() &&
                Objects.equals(asset1.getPortOfRegistration(), asset2.getPortOfRegistration()) &&
                Objects.equals(asset1.getPowerOfAuxEngine(), asset2.getPowerOfAuxEngine()) &&
                Objects.equals(asset1.getPowerOfMainEngine(), asset2.getPowerOfMainEngine()) &&
                Objects.equals(asset1.getHasLicence(), asset2.getHasLicence()) &&
                Objects.equals(asset1.getLicenceType(), asset2.getLicenceType()) &&
                Objects.equals(asset1.getMainFishingGearCode(), asset2.getMainFishingGearCode()) &&
                Objects.equals(asset1.getSubFishingGearCode(), asset2.getSubFishingGearCode()) &&
                Objects.equals(asset1.getGearFishingType(), asset2.getGearFishingType()) &&
                Objects.equals(asset1.getOwnerName(), asset2.getOwnerName()) &&
                Objects.equals(asset1.getHasVms(), asset2.getHasVms()) &&
                Objects.equals(asset1.getOwnerAddress(), asset2.getOwnerAddress()) &&
                Objects.equals(asset1.getAssetAgentAddress(), asset2.getAssetAgentAddress()) &&
                Objects.equals(asset1.getCountryOfImportOrExport(), asset2.getCountryOfImportOrExport()) &&
                Objects.equals(asset1.getTypeOfExport(), asset2.getTypeOfExport()) &&
                Objects.equals(asset1.getAdministrativeDecisionDate(), asset2.getAdministrativeDecisionDate()) &&
                Objects.equals(asset1.getSegment(), asset2.getSegment()) &&
                Objects.equals(asset1.getSegmentOfAdministrativeDecision(), asset2.getSegmentOfAdministrativeDecision()) &&
                Objects.equals(asset1.getPublicAid(), asset2.getPublicAid()) &&
                Objects.equals(asset1.getRegistrationNumber(), asset2.getRegistrationNumber()) &&
                Objects.equals(asset1.getProdOrgCode(), asset2.getProdOrgCode()) &&
                Objects.equals(asset1.getProdOrgName(), asset2.getProdOrgName());
    }
}