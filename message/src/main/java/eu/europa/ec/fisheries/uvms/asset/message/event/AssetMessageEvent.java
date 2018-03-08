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
package eu.europa.ec.fisheries.uvms.asset.message.event;

import eu.europa.ec.fisheries.uvms.asset.types.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.jms.TextMessage;

public class AssetMessageEvent {

    private TextMessage message;
    public AssetMessageEvent(TextMessage message) {
        this.message = message;
    }




    private AssetId assetId;
    private AssetListQuery query;
    private AssetFault fault;
    private String assetGuid;
    private AssetDTO asset;
    private String username;
    private FishingGearDTO fishingGear;




    public AssetMessageEvent(TextMessage message, AssetId assetId) {
        this.message = message;
        this.assetId = assetId;
    }

    public AssetMessageEvent(TextMessage message, AssetListQuery query) {
        this.message = message;
        this.query = query;
    }


    public AssetMessageEvent(TextMessage message, AssetFault fault) {
        this.message = message;
        this.fault = fault;
    }

    public AssetMessageEvent(TextMessage message, String assetGuid) {
        this.message = message;
        this.assetGuid = assetGuid;
    }

    public AssetMessageEvent(TextMessage message, AssetDTO asset, String username){
        this.message = message;
        this.asset = asset;
        this.username = username;
    }

    public AssetMessageEvent(TextMessage message, FishingGearDTO fishingGear, String username){
        this.message = message;
        this.username = username;
        this.fishingGear = fishingGear;
    }

    public TextMessage getMessage() {
        return message;
    }

    public void setMessage(TextMessage message) {
        this.message = message;
    }

    public AssetId getAssetId() {
        return assetId;
    }

    public AssetListQuery getQuery() {
        return query;
    }

    public AssetFault getFault() {
        return fault;
    }

    public void setFault(AssetFault fault) {
        this.fault = fault;
    }

    public String getAssetGuid() {
        return assetGuid;
    }

    public void setAssetGuid(String assetGuid) {
        this.assetGuid = assetGuid;
    }

    public AssetDTO getAsset() {
        return asset;
    }

    public void setAsset(AssetDTO asset) {
        this.asset = asset;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public FishingGearDTO getFishingGear() {
        return fishingGear;
    }

    public void setFishingGear(FishingGearDTO fishingGear) {
        this.fishingGear = fishingGear;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }





}