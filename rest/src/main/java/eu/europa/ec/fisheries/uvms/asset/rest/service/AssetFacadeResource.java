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
package eu.europa.ec.fisheries.uvms.asset.rest.service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

import eu.europa.ec.fisheries.uvms.asset.facade.AssetFacadeNew;
import eu.europa.ec.fisheries.uvms.asset.rest.error.AssetError;
import eu.europa.ec.fisheries.uvms.asset.rest.exception.AssetFacadeException;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/asset-gateway")
@Stateless
public class AssetFacadeResource {

    final static Logger LOG = LoggerFactory.getLogger(AssetFacadeResource.class);

    @Inject
    private AssetFacadeNew assetFacade;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/find-asset-history-by-cfr")
    public List<Asset> findHistoryOfAssetByCfr(@QueryParam("cfr") String cfr) throws AssetFacadeException {
        try {
            LOG.info("Getting asset list by cfr:{}", cfr);
            return assetFacade.findHistoryOfAssetByCfr(cfr);
        } catch (SQLGrammarException e) { // should be caught at dao level or facade
            throw new AssetFacadeException(AssetError.SQL_ERROR, e.getMessage());
        } catch (Exception e) {
            throw new AssetFacadeException(AssetError.UNKNOWN_ERROR, e.getMessage());
        }
    }


    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/find-asset-history-by-criteria")
    public List<Asset> findHistoryOfAssetBy(@QueryParam("reportDate") String reportDate,
                                            @QueryParam("cfr") String cfr,
                                            @QueryParam("regCountry") String regCountry,
                                            @QueryParam("ircs") String ircs,
                                            @QueryParam("extMark") String extMark,
                                            @QueryParam("iccat") String iccat) throws AssetFacadeException {
        try {
            LOG.info("Getting asset list by cfr:{}", cfr);
            return assetFacade.findHistoryOfAssetBy(reportDate, cfr, regCountry, ircs, extMark, iccat);
        } catch (SQLGrammarException e) { // should be caught at dao level or facade
            throw new AssetFacadeException(AssetError.SQL_ERROR, e.getMessage());
        } catch (Exception e) {
            throw new AssetFacadeException(AssetError.UNKNOWN_ERROR, e.getMessage());
        }
    }


}