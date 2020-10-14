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

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import eu.europa.ec.fisheries.uvms.rest.security.InternalRestTokenHandler;
import eu.europa.ec.fisheries.uvms.tests.BuildAssetServiceDeployment;

public abstract class AbstractAssetRestTest extends BuildAssetServiceDeployment {

    private String token;

    @Inject
    private InternalRestTokenHandler tokenHandler;

    protected WebTarget getWebTargetExternal() {
        Client client = ClientBuilder.newClient();
        client.register(JsonBConfigurator.class);
        return client.target("http://localhost:28080/test/rest");  //external
        //return client.target("http://localhost:8080/test/rest");    //internal
    }

    //jersey does not like sse so to fix this we need the sse test to reside on the server environment instead of @RunAsClient
    //also, if we switch from jersey to resteasy (like we have in all the other modules) for the client everything stops working with status code 405
    protected WebTarget getWebTargetInternal() {
        Client client = ClientBuilder.newClient();
        client.register(JsonBConfigurator.class);
        return client.target("http://localhost:8080/test/rest");    //internal
    }

    protected String getTokenExternal() {
        if (token == null) {
            token = ClientBuilder.newClient()
                    .target("http://localhost:28080/unionvms/user/token")
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class);
        }
        return token;
    }

    //see the comment above getWebbTargetInternal
    protected String getTokenInternal() {
        if (token == null) {
            token = ClientBuilder.newClient()
                    .target("http://localhost:8080/unionvms/user/token")
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class);
        }
        return token;
    }

    protected String getTokenInternalRest() {
        return tokenHandler.createAndFetchToken("user");
    }
}
