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
package eu.europa.ec.fisheries.uvms.asset.message;

public class AssetConstants {

    public static final String CONNECTION_FACTORY = "java:/ConnectionFactory";

    public static final String CONNECTION_TYPE = "javax.jms.MessageListener";
    public static final String DESTINATION_TYPE_QUEUE = "javax.jms.Queue";

    public static final String QUEUE_ASSET = "java:/jms/queue/UVMSAsset"; //response queue
    public static final String QUEUE_ASSET_EVENT = "java:/jms/queue/UVMSAssetEvent"; //request queue, create event
    public static final String QUEUE_NAME_ASSET_EVENT = "UVMSAssetEvent";

    public static final String QUEUE_DATASOURCE_INTERNAL = "java:/jms/queue/UVMSAssetModel"; //asset internal db source
    public static final String QUEUE_DATASOURCE_NATIONAL = "java:/jms/queue/UVMSAssetNational"; //asset national source
    public static final String QUEUE_DATASOURCE_XEU = "java:/jms/queue/UVMSAssetXEU"; //asset eu source

    public static final String AUDIT_MODULE_QUEUE = "java:/jms/queue/UVMSAuditEvent";

}