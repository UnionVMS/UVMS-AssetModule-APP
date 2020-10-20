/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2020.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.asset.rest.client.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetRestClientException;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.AssetErrorResponseDto;
import eu.europa.ec.fisheries.uvms.asset.rest.client.AssetClient;
import eu.europa.ec.fisheries.uvms.asset.rest.client.config.AssetRestClientConfig;
import eu.europa.ec.fisheries.uvms.config.event.ConfigSettingEvent;
import eu.europa.ec.fisheries.uvms.config.event.ConfigSettingUpdatedEvent;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.BatchAssetListResponseElement;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class AssetRestClientImpl implements AssetClient {

    private static final String FIND_ASSET_HISTORY_BY_CFR_PATH = "/find-asset-history-by-cfr";
    private static final String FIND_ASSET_HISTORY_BY_CRITERIA_PATH = "/find-asset-history-by-criteria";
    private static final String FIND_ASSET_BY_ID_PATH = "/find-asset-by-id";
    private static final String GET_ASSET_LIST_BATCH_BY_CONNECT_IDS_PATH = "/get-asset-list-by-connect-ids";
    private static final String GET_ASSET_GROUP_LIST_BY_ASSET_GUID_PATH = "/get-asset-groups-by-asset-guid";

    private static final String REPORT_DATE = "reportDate";
    private static final String CFR = "cfr";
    private static final String REG_COUNTRY = "regCountry";
    private static final String IRCS = "ircs";
    private static final String EXT_MARK = "extMark";
    private static final String ICCAT = "iccat";
    private static final String UVI = "uvi";
    private static final String ASSET_GUID = "assetGuid";

    private AssetRestClientConfig config;

    private WebTarget webTarget;

    @Inject
    public AssetRestClientImpl(AssetRestClientConfig config) {
        this.config = config;
    }

    @PostConstruct
    public void init() throws ConfigServiceException {
        configureNewClient();
    }

    @SneakyThrows
    @Override
    public List<Asset> findHistoryOfAssetByCfr(String cfr) {
        try {
            Response response = webTarget
                    .path(FIND_ASSET_HISTORY_BY_CFR_PATH)
                    .queryParam(CFR, cfr)
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get(Response.class);

            return handleResponse(response);
        } catch (ResponseProcessingException e) {
            log.error("Error processing response from server");
            throw new AssetRestClientException("Error response processing from server", e);
        } catch (ProcessingException e) {
            log.error("I/O error processing response");
            throw new AssetRestClientException("I/O error processing response ", e);
        } catch (WebApplicationException e) {
            log.error("Error response from server");
            throw new AssetRestClientException("Error response from server", e);
        }
    }

    @SneakyThrows
    @Override
    public List<Asset> findHistoryOfAssetBy(String reportDate, String cfr, String regCountry, String ircs, String extMark, String iccat, String uvi) {
        try {
            Response response = webTarget
                    .path(FIND_ASSET_HISTORY_BY_CRITERIA_PATH)
                    .queryParam(REPORT_DATE, reportDate)
                    .queryParam(CFR, cfr)
                    .queryParam(REG_COUNTRY, regCountry)
                    .queryParam(IRCS, ircs)
                    .queryParam(EXT_MARK, extMark)
                    .queryParam(ICCAT, iccat)
                    .queryParam(UVI, uvi)
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get(Response.class);

            return handleResponse(response);
        } catch (ResponseProcessingException e) {
            log.error("Error processing response from server ", e);
            throw new AssetRestClientException("Error response processing from server", e);
        } catch (ProcessingException e) {
            log.error("I/O error processing response ", e);
            throw new AssetRestClientException("I/O error processing response ", e);
        } catch (WebApplicationException e) {
            log.error("Error response from server ", e);
            throw new AssetRestClientException("Error response from server", e);
        }
    }

    @SneakyThrows
    @Override
    public List<BatchAssetListResponseElement> getAssetListBatch(List<AssetListQuery> assetBatchRequest) {
        try {
            Response response = webTarget
                    .path(GET_ASSET_LIST_BATCH_BY_CONNECT_IDS_PATH)
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.json(assetBatchRequest), Response.class);

            return handleAssetListBatchResponse(response);
        } catch (ResponseProcessingException e) {
            log.error("Error processing response from server ", e);
            throw new AssetRestClientException("Error response processing from server", e);
        } catch (ProcessingException e) {
            log.error("I/O error processing response ", e);
            throw new AssetRestClientException("I/O error processing response ", e);
        } catch (WebApplicationException e) {
            log.error("Error response from server ", e);
            throw new AssetRestClientException("Error response from server", e);
        }
    }

    @SneakyThrows
    @Override
    public Asset getAsset(AssetId assetId) {
        try {
            Response response = webTarget
                    .path(FIND_ASSET_BY_ID_PATH)
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.json(assetId), Response.class);

            return handleAssetResponse(response);
        } catch (ResponseProcessingException e) {
            log.error("Error processing response from server");
            throw new AssetRestClientException("Error response processing from server", e);
        } catch (ProcessingException e) {
            log.error("I/O error processing response");
            throw new AssetRestClientException("I/O error processing response ", e);
        } catch (WebApplicationException e) {
            log.error("Error response from server");
            throw new AssetRestClientException("Error response from server", e);
        }
    }

    @SneakyThrows
    @Override
    public List<AssetGroup> getAssetGroupListByAssetGuid(String assetGuid) {
        try {
            Response response = webTarget
                    .path(GET_ASSET_GROUP_LIST_BY_ASSET_GUID_PATH)
                    .queryParam(ASSET_GUID, assetGuid)
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get(Response.class);

            return handleAssetGroupListResponse(response);
        } catch (ResponseProcessingException e) {
            log.error("Error processing response from server");
            throw new AssetRestClientException("Error response processing from server", e);
        } catch (ProcessingException e) {
            log.error("I/O error processing response");
            throw new AssetRestClientException("I/O error processing response ", e);
        } catch (WebApplicationException e) {
            log.error("Error response from server");
            throw new AssetRestClientException("Error response from server", e);
        }
    }

    private List<Asset> handleResponse(Response response) throws AssetRestClientException {
        handleNotOKStatusCode(response);
        List<Asset> assetHistory = response.readEntity(new GenericType<List<Asset>>() {
        });
        response.close();
        return assetHistory;
    }

    private List<AssetGroup> handleAssetGroupListResponse(Response response) throws AssetRestClientException {
        handleNotOKStatusCode(response);
        List<AssetGroup> assetGroups = response.readEntity(new GenericType<List<AssetGroup>>() {
        });
        response.close();
        return assetGroups;
    }

    private Asset handleAssetResponse(Response response) throws AssetRestClientException {
        handleNotOKStatusCode(response);
        Asset asset = response.readEntity(new GenericType<Asset>() {
        });
        response.close();
        return asset;
    }

    private List<BatchAssetListResponseElement> handleAssetListBatchResponse(Response response) throws AssetRestClientException {
        handleNotOKStatusCode(response);
        List<BatchAssetListResponseElement> assetListBatchResponse = response.readEntity(new GenericType<List<BatchAssetListResponseElement>>() {
        });
        response.close();
        return assetListBatchResponse;
    }

    private void handleNotOKStatusCode(Response response) throws AssetRestClientException {
        if (response.getStatus() != 200) {
            AssetErrorResponseDto assetErrorResponseDto = response.readEntity(new GenericType<AssetErrorResponseDto>() {
            });
            log.debug("Asset Service responded with error code {} - {}", assetErrorResponseDto.getCode(), assetErrorResponseDto.getMessage());
            throw new AssetRestClientException("Asset service response: " + assetErrorResponseDto.getMessage());
        }
    }

    public void setConfig(@Observes @ConfigSettingUpdatedEvent ConfigSettingEvent settingEvent) throws ConfigServiceException {
        if (config.isEndpointUpdateEvent(settingEvent.getKey())) {
            configureNewClient();
        }
    }

    private void configureNewClient() throws ConfigServiceException {
        webTarget = null;
        String url = config.getAssetGatewayEndpoint();
        Client client = ClientBuilder.newClient();
        ContextResolver<ObjectMapper> objectMapperContextResolver = new ContextResolver<ObjectMapper>() {
            @Override
            public ObjectMapper getContext(Class<?> type) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                mapper.registerModule(new JaxbAnnotationModule());
                return mapper;
            }
        };
        client.register(objectMapperContextResolver);
        webTarget = client.target(url);
    }
}
