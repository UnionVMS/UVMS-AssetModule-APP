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

package eu.europa.ec.fisheries.uvms.asset.constants;

public enum ParameterKey {

	NATIONAL_USE("ASSET.NATIONAL.USE"),

	EU_USE("ASSET.EU.USE"),

	// THIRDCOUNTRY_USE("vessel.thirdcountry.use"),
	// THIRDCOUNTRY_JMS_JNDI("vessel.thirdcountry.jms.jndi"),

	NATIONAL_SERVICE_ENDPOINT("NATIONAL_SERVICE_ENDPOINT"),

	DEFAULT_FLAG_STATE("ASSET.DEFAULT.FLAGSTATE"),

	// XEU PARAMS

	XEU_SERVICE_ENDPOINT("ASSET.EU.SERVICE.ENDPOINT"),

	CERT_HEADER("ASSET.EU.SERVICE.CLIENT.CERT.HEADER"),

	CERT_VALUE("ASSET.EU.SERVICE.CLIENT.CERT.VALUE"),

	FLUX_DATAFLOW("ASSET.EU.SERVICE.DATAFLOW"),

	FLUX_AD("ASSET.EU.SERVICE.FLUX.AD"),

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