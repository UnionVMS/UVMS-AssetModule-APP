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

import eu.europa.ec.fisheries.wsdl.asset.module.AssetGroupListByUserRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetFromAssetIdAndDateRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetListByAssetGroupsRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.GetFlagStateByGuidAndDateRequest;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.jms.TextMessage;
import java.util.List;

public class AssetMessageEvent {

    private TextMessage message;
    private AssetId assetId;
    private AssetListQuery query;
    private List<AssetListQuery> batchQuery;
    private AssetGroupListByUserRequest request;
    private GetAssetListByAssetGroupsRequest assetListByGroup;
    private AssetFault fault;
    private String assetGuid;
    private Asset asset;
    private String username;
    private FishingGear fishingGear;
    private GetFlagStateByGuidAndDateRequest getFlagStateByGuidAndDateRequest;
    private GetAssetFromAssetIdAndDateRequest getAssetFromAssetIdAndDateRequest;


    public AssetMessageEvent(TextMessage message) {
        this.message = message;
    }

    public AssetMessageEvent(TextMessage message, GetAssetListByAssetGroupsRequest assetId) {
        this.message = message;
        this.assetListByGroup = assetId;
    }

    public AssetMessageEvent(TextMessage message, List<AssetListQuery> batchQuery) {
        this.message = message;
        this.batchQuery = batchQuery;
    }

    public AssetMessageEvent(TextMessage message, AssetId assetId) {
        this.message = message;
        this.assetId = assetId;
    }

    public AssetMessageEvent(TextMessage message, AssetListQuery query) {
        this.message = message;
        this.query = query;
    }

    public AssetMessageEvent(TextMessage message, AssetGroupListByUserRequest query) {
        this.message = message;
        this.request = query;
    }

    public AssetMessageEvent(TextMessage message, AssetFault fault) {
        this.message = message;
        this.fault = fault;
    }

    public AssetMessageEvent(TextMessage message, String assetGuid) {
        this.message = message;
        this.assetGuid = assetGuid;
    }

    public AssetMessageEvent(TextMessage message, Asset asset, String username){
        this.message = message;
        this.asset = asset;
        this.username = username;
    }

    public AssetMessageEvent(TextMessage message, FishingGear fishingGear, String username){
        this.message = message;
        this.username = username;
        this.fishingGear = fishingGear;
    }

    public TextMessage getMessage() {
        return message;
    }

    public AssetMessageEvent(TextMessage message, GetFlagStateByGuidAndDateRequest getFlagStateByGuidAndDateRequest){
        this.message = message;
        this.getFlagStateByGuidAndDateRequest = getFlagStateByGuidAndDateRequest;
    }

    public AssetMessageEvent(TextMessage message, GetAssetFromAssetIdAndDateRequest getAssetFromAssetIdAndDateRequest){
        this.message = message;
        this.getAssetFromAssetIdAndDateRequest = getAssetFromAssetIdAndDateRequest;
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

    public AssetGroupListByUserRequest getRequest() {
        return request;
    }

    public void setRequest(AssetGroupListByUserRequest request) {
        this.request = request;
    }

    public GetAssetListByAssetGroupsRequest getAssetListByGroup() {
        return assetListByGroup;
    }

    public void setAssetListByGroup(GetAssetListByAssetGroupsRequest assetListByGroup) {
        this.assetListByGroup = assetListByGroup;
    }

    public String getAssetGuid() {
        return assetGuid;
    }

    public void setAssetGuid(String assetGuid) {
        this.assetGuid = assetGuid;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public FishingGear getFishingGear() {
        return fishingGear;
    }

    public void setFishingGear(FishingGear fishingGear) {
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


    public GetFlagStateByGuidAndDateRequest getGetFlagStateByGuidAndDateRequest() {
        return getFlagStateByGuidAndDateRequest;
    }

    public void setGetFlagStateByGuidAndDateRequest(GetFlagStateByGuidAndDateRequest getFlagStateByGuidAndDateRequest) {
        this.getFlagStateByGuidAndDateRequest = getFlagStateByGuidAndDateRequest;
    }

    public GetAssetFromAssetIdAndDateRequest getGetAssetFromAssetIdAndDateRequest() {
        return getAssetFromAssetIdAndDateRequest;
    }

    public void setGetAssetFromAssetIdAndDateRequest(GetAssetFromAssetIdAndDateRequest getAssetFromAssetIdAndDateRequest) {
        this.getAssetFromAssetIdAndDateRequest = getAssetFromAssetIdAndDateRequest;
    }

    public List<AssetListQuery> getBatchQuery() {
        return batchQuery;
    }
}