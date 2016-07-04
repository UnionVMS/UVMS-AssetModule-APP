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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException;
import eu.europa.ec.fisheries.uvms.asset.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.asset.exception.UnauthorizedException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.rest.dto.ResponseCodeConstant;
import eu.europa.ec.fisheries.uvms.asset.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.asset.rest.service.mockdata.TestMockData;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;

import javax.servlet.http.HttpServletRequest;

/***/
public class AssetResourceTest {

    private static final String ASSETID_GUID = "134u9u";

    private static final Integer ASSET_LIST_SIZE = 3;

    ListAssetResponse ASSET_DTO_LIST = TestMockData.getAssetDtoList(ASSET_LIST_SIZE);
    Asset ASSET_DTO = TestMockData.getAssetDto(1);

    private final ResponseDto ERROR_RESULT;
    private final ResponseDto SUCCESS_RESULT;
    private final ResponseDto SUCCESS_RESULT_CREATE_UPDATE;
    private final ResponseDto SUCCESS_RESULT_ASSET_LIST;
    private final ResponseDto SUCCESS_RESULT_ASSET_DTO;
    private static final String UVMS = "UVMS";

    AssetResource ASSET_SERVICE_NULL = new AssetResource();

    @Mock
    AssetService assetService;

    @Mock
    HttpServletRequest servletRequest;

    @InjectMocks
    AssetResource assetResource;

    public AssetResourceTest() {

        ERROR_RESULT = new ResponseDto(ResponseCodeConstant.UNDEFINED_ERROR);
        SUCCESS_RESULT = new ResponseDto(ResponseCodeConstant.OK);
        SUCCESS_RESULT_ASSET_LIST = new ResponseDto(ASSET_DTO_LIST, ResponseCodeConstant.OK);
        SUCCESS_RESULT_ASSET_DTO = new ResponseDto(ASSET_DTO, ResponseCodeConstant.OK);
        SUCCESS_RESULT_CREATE_UPDATE = new ResponseDto(ASSET_DTO, ResponseCodeConstant.OK);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test get asset list with a happy outcome
     *
     * @throws eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException
     */
    @Test
    public void testGetAssetList() throws AssetException {
        AssetListQuery assetListQuery = new AssetListQuery();
        when(assetService.getAssetList(assetListQuery)).thenReturn(ASSET_DTO_LIST);
        ResponseDto result = assetResource.getAssetList(assetListQuery);
        assertEquals(SUCCESS_RESULT_ASSET_LIST.toString(), result.toString());

    }

    /**
     * Test get asset list when the injected EJB is null
     *
     * @throws eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException
     */
    @Test
    public void testGetAssetListNull() throws AssetServiceException {
        AssetListQuery assetListQuery = new AssetListQuery();
        ResponseDto result = ASSET_SERVICE_NULL.getAssetList(assetListQuery);
        assertEquals(ERROR_RESULT.toString(), result.toString());

    }

    /**
     * Test get asset by id with a happy outcome
     *
     * @throws eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException
     */

    @Test
    public void testGetAssetById() throws AssetException {
        Mockito.when(assetService.getAssetByGuid(ASSETID_GUID)).thenReturn(ASSET_DTO);
        ResponseDto result = assetResource.getAssetById(ASSETID_GUID);
        Mockito.verify(assetService).getAssetByGuid(ASSETID_GUID);
        assertEquals(SUCCESS_RESULT_ASSET_DTO.toString(), result.toString());

    }

    /**
     * Test get asset by id when the injected EJB is null
     *
     * @throws eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException
     */

    @Test
    public void testGetAssetByIdNull() throws AssetServiceException {
        ResponseDto result = ASSET_SERVICE_NULL.getAssetById(ASSETID_GUID);
        assertEquals(ERROR_RESULT.toString(), result.toString());
    }

    // /**
    // * Test createAsset with a happy outcome
    // *
    // * @throws AssetServiceException
    // */
    @Test
    public void testCreateAsset() throws AssetException {
        doReturn(ASSET_DTO).when(assetService).createAsset(ASSET_DTO, UVMS);
        doReturn(UVMS).when(servletRequest).getRemoteUser();

        ResponseDto result = assetResource.createAsset(ASSET_DTO);
        Mockito.verify(assetService).createAsset(ASSET_DTO, UVMS);
        assertEquals(SUCCESS_RESULT_CREATE_UPDATE.toString(), result.toString());
    }
    //
    // /**
    // * Test create asset when the injected EJB is null
    // */

    @Test
    public void testCreateAssetNull() {
        ResponseDto result = ASSET_SERVICE_NULL.createAsset(ASSET_DTO);
        assertEquals(ERROR_RESULT.toString(), result.toString());
    }

    /**
     * Test update asset with a
     *
     * @throws eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException
     */
    @Test
    public void testUpdateAsset() throws AssetException {
        doReturn(ASSET_DTO).when(assetService).updateAsset(ASSET_DTO, UVMS, "A comment.");
        doReturn(UVMS).when(servletRequest).getRemoteUser();
        ResponseDto result = assetResource.updateAsset(ASSET_DTO, "A comment.");
        Mockito.verify(assetService).updateAsset(ASSET_DTO, UVMS, "A comment.");
        assertEquals(SUCCESS_RESULT_CREATE_UPDATE.toString(), result.toString());
    }

    /**
     * Test update asset when the injected EJB is null
     */
    @Test
    public void testUpdateAssetNull() {
        doReturn(UVMS).when(servletRequest).getRemoteUser();
        ResponseDto result = ASSET_SERVICE_NULL.updateAsset(ASSET_DTO, "A comment.");
        assertEquals(ERROR_RESULT.toString(), result.toString());
    }

    @Test
    public void testThrowAssetServiceException() throws AssetException {
        AssetServiceException assetServiceException = new AssetServiceException("Error");
        doThrow(assetServiceException).when(assetService).getAssetByGuid("11");

        ResponseDto result = assetResource.getAssetById("11");
        Mockito.verify(assetService).getAssetByGuid("11");
        assertEquals(ResponseCodeConstant.SERVICE_ERROR.getCode(), result.getCode());
    }

    @Test
    public void testThrowAssetModelMapperException() throws AssetException {
        AssetModelMapperException exception = new AssetModelMapperException("Error");
        doThrow(exception).when(assetService).getAssetByGuid("11");

        ResponseDto result = assetResource.getAssetById("11");
        Mockito.verify(assetService).getAssetByGuid("11");
        assertEquals(ResponseCodeConstant.MAPPING_ERROR.getCode(), result.getCode());
    }

    @Test
    public void testThrowInputArgumentException() throws AssetException {
        InputArgumentException exception = new InputArgumentException("Error");
        doThrow(exception).when(assetService).getAssetByGuid("11");

        ResponseDto result = assetResource.getAssetById("11");
        Mockito.verify(assetService).getAssetByGuid("11");
        assertEquals(ResponseCodeConstant.INPUT_ERROR.getCode(), result.getCode());
    }


    @Test
    public void testThrowAssetException() throws AssetException {
        AssetException exception = new AssetException("Error");
        doThrow(exception).when(assetService).getAssetByGuid("11");

        ResponseDto result = assetResource.getAssetById("11");
        Mockito.verify(assetService).getAssetByGuid("11");
        assertEquals(ResponseCodeConstant.ASSET_ERROR.getCode(), result.getCode());
    }

    @Test
    public void testThrowRuntimeException() throws AssetException {
        RuntimeException exception = new RuntimeException("Error");
        doThrow(exception).when(assetService).getAssetByGuid("11");

        ResponseDto result = assetResource.getAssetById("11");
        Mockito.verify(assetService).getAssetByGuid("11");
        assertEquals(ResponseCodeConstant.UNDEFINED_ERROR.getCode(), result.getCode());
    }

    @Test
    public void testThrowUnauthorizedException() throws AssetException {
        RuntimeException exception = new RuntimeException("Error");
        UnauthorizedException unauthorizedException = new UnauthorizedException("Error");
        exception.initCause(unauthorizedException);
        doThrow(exception).when(assetService).getAssetByGuid("11");

        ResponseDto result = assetResource.getAssetById("11");
        Mockito.verify(assetService).getAssetByGuid("11");
        assertEquals(ResponseCodeConstant.UNAUTHORIZED.getCode(), result.getCode());
    }

}