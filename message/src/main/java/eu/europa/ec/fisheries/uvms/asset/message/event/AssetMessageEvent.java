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
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetListByAssetGroupsRequest;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetFault;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import javax.jms.TextMessage;

public class AssetMessageEvent {

    private TextMessage message;
    private AssetId assetId;
    private AssetListQuery query;
    private AssetGroupListByUserRequest request;
    private GetAssetListByAssetGroupsRequest assetListByGroup;
    private AssetFault fault;
    private String assetGuid;

    public AssetMessageEvent(TextMessage message) {
        this.message = message;
    }

    public AssetMessageEvent(TextMessage message, GetAssetListByAssetGroupsRequest assetId) {
        this.message = message;
        this.assetListByGroup = assetId;
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
}