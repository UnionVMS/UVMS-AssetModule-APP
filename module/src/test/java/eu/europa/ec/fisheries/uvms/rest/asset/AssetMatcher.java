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
package eu.europa.ec.fisheries.uvms.rest.asset;

import java.time.temporal.ChronoUnit;
import java.util.Objects;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;

public class AssetMatcher extends TypeSafeDiagnosingMatcher<Asset> {

    private Asset asset;

    public AssetMatcher(Asset asset) {
        this.asset = asset;
    }

    public static AssetMatcher assetEquals(Asset asset) {
        return new AssetMatcher(asset);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("assets are equal");
    }

    @Override
    protected boolean matchesSafely(Asset otherAsset, Description mismatchDescription) {
        boolean equals = true;
        if (!Objects.equals(asset.getId(), otherAsset.getId())) {
            equals = false;
            mismatchDescription.appendText(String.format("ID is not equal. Expected: %s, Actual: %s", asset.getId(),
                    otherAsset.getId()));
        }
        if (!Objects.equals(asset.getHistoryId(), otherAsset.getHistoryId())) {
            equals = false;
            mismatchDescription.appendText(String.format("History id is not equal. Expected: %s, Actual: %s", asset
                    .getHistoryId(), otherAsset.getHistoryId()));
        }
        if (!Objects.equals(asset.getCfr(), otherAsset.getCfr())) {
            equals = false;
            mismatchDescription.appendText(String.format("CFR is not equal. Expected %s, Actual: %s", asset.getCfr(),
                    otherAsset.getCfr()));
        }
        if (!Objects.equals(asset.getIrcs(), otherAsset.getIrcs())) {
            equals = false;
            mismatchDescription.appendText(String.format("IRCS is not equal. Expected %s, Actual: %s", asset.getIrcs(),
                    otherAsset.getIrcs()));
        }
        if (!Objects.equals(asset.getMmsi(), otherAsset.getMmsi())) {
            equals = false;
            mismatchDescription.appendText(String.format("MMSI is not equal. Expected %s, Actual: %s", asset.getMmsi(),
                    otherAsset.getMmsi()));
        }
        if (!Objects.equals(asset.getFlagStateCode(), otherAsset.getFlagStateCode())) {
            equals = false;
            mismatchDescription.appendText(String.format("Flag state is not equal. Expected %s, Actual: %s", asset
                    .getFlagStateCode(), otherAsset.getFlagStateCode()));
        }
        if (!Objects.equals(asset.getUpdateTime().truncatedTo(ChronoUnit.MILLIS),
                otherAsset.getUpdateTime().truncatedTo(ChronoUnit.MILLIS))) {
            equals = false;
            mismatchDescription.appendText(String.format("Update time is not equal. Expected %s, Actual: %s", asset
                    .getUpdateTime(), otherAsset.getUpdateTime()));
        }
        if (!Objects.equals(asset.getName(), otherAsset.getName())) {
            equals = false;
            mismatchDescription.appendText(String.format("Name is not equal. Expected %s, Actual: %s", asset
                    .getName(), otherAsset.getName()));
        }
        return equals;
    }

}
