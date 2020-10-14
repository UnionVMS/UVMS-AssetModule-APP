/*
 * ﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
 * © European Union, 2015-2016. This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM
 * Suite is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should
 * have received a copy of the GNU General Public License along with the IFDM Suite. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.rest.asset.mapper;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.bind.adapter.JsonbAdapter;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;

public class CustomAssetAdapter implements JsonbAdapter<Asset, JsonObject> {
    
    @Override
    public JsonObject adaptToJson(Asset asset) throws Exception {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (asset.getId() != null) {
            builder.add("id", asset.getId().toString());
        }
        if (asset.getHistoryId() != null) {
            builder.add("historyId", asset.getHistoryId().toString());
        }
        if (asset.getName() != null) {
            builder.add("name", asset.getName());
        }
        if (asset.getIrcs() != null) {
            builder.add("ircs", asset.getIrcs());
        }
        if (asset.getNationalId() != null) {
            builder.add("nationalId", asset.getNationalId());
        }
        return builder.build();
    }

    @Override
    public Asset adaptFromJson(JsonObject obj) throws Exception {
        return null;
    }
}