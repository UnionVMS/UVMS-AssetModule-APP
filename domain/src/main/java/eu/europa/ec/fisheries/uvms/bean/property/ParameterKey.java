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
package eu.europa.ec.fisheries.uvms.bean.property;

public enum ParameterKey {
    NATIONAL_USE("asset.national.use"),
    EU_USE("asset.eu.use"),
    //THIRDCOUNTRY_USE("vessel.thirdcountry.use"), THIRDCOUNTRY_JMS_JNDI("vessel.thirdcountry.jms.jndi"),
    NATIONAL_SERVICE_ENDPOINT("NATIONAL_SERVICE_ENDPOINT"),
    DEFAULT_FLAG_STATE("asset.default.flagstate"),
    //XEU PARAMS
    XEU_SERVICE_ENDPOINT("asset.eu.service.endpoint"),
    CERT_HEADER("asset.eu.service.client.cert.header"),
    CERT_VALUE("asset.eu.service.client.cert.value"),
    FLUX_DATAFLOW("asset.eu.service.dataflow"),
    FLUX_AD("asset.eu.service.flux.ad"),
    NATIONAL_VESSEL_COMP_SERVICE_ENDPOINT("NATIONAL_VESSEL_COMP_SERVICE_ENDPOINT"),
    NATIONAL_VESSEL_NATIONS("NATIONAL_VESSEL_NATIONS"),
    NATIONAL_GENERAL_NOTIFICATION_SERVICE_ENDPOINT("NATIONAL_GENERAL_NOTIFICATION_SERVICE_ENDPOINT"),
    NATIONAL_EQUIPMENT_SERVICE_ENDPOINT("NATIONAL_EQUIPMENT_SERVICE_ENDPOINT");

    

	private final String key;

    private ParameterKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}