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
package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.dto;

import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalSource;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mock.MockData;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.MobileTerminalServiceBean;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.dto.MTResponseDto;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.error.MTResponseCode;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.services.MobileTerminalRestResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

public class ResponseTest {

    @Mock
    private MobileTerminalServiceBean mobileTerminalServiceBean;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private MobileTerminalRestResource mobileTerminalRestResource;

//    private MobileTerminalRestResource SERVICE_NULL = new MobileTerminalRestResource();
//    private static final Integer LIST_SIZE = 3;

    private static final Integer MOBILE_TERMINAL_ID_INT = 1;
    private static final String MOBILE_TERMINAL_ID = "NKJSDGHKJy9239";

//    private final ResponseCode ERROR_RESULT;

//    private final ResponseDto SUCCESS_RESULT_CREATE;
    private final MTResponseDto SUCCESS_RESULT_LIST_RESPONSE;
    private final MTResponseDto SUCCESS_RESULT_UPDATE;
    private final MTResponseDto SUCCESS_RESULT_GET_BY_ID;

    private final MobileTerminalType MOBILE_TERMINAL_DTO = MockData.createMobileTerminalDto(MOBILE_TERMINAL_ID_INT);
    private final MobileTerminalListResponse MOBILE_TERMINAL_LIST_RESPONSE = MockData.createMobileTerminalListResponse();

    public ResponseTest() {
//        ERROR_RESULT = ResponseCode.UNDEFINED_ERROR;
        SUCCESS_RESULT_UPDATE = new MTResponseDto<>(MOBILE_TERMINAL_DTO, MTResponseCode.OK);
        SUCCESS_RESULT_LIST_RESPONSE = new MTResponseDto<>(MOBILE_TERMINAL_LIST_RESPONSE, MTResponseCode.OK);
//        SUCCESS_RESULT_CREATE = new ResponseDto<>(MOBILE_TERMINAL_DTO, ResponseCode.OK);
        SUCCESS_RESULT_GET_BY_ID = new MTResponseDto<>(MOBILE_TERMINAL_DTO, MTResponseCode.OK);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /*@Test
    public void testGetMobileTerminalById() throws Exception {
        doReturn(MOBILE_TERMINAL_DTO).when(mobileTerminalServiceBean).getMobileTerminalByIdFromInternalOrExternalSource(MOBILE_TERMINAL_ID);
        MTResponseDto result = mobileTerminalRestResource.getMobileTerminalByIdFromInternalOrExternalSource(MOBILE_TERMINAL_ID);
        Mockito.verify(mobileTerminalServiceBean).getMobileTerminalByIdFromInternalOrExternalSource(MOBILE_TERMINAL_ID);
        assertEquals(SUCCESS_RESULT_GET_BY_ID.toString(), result.toString());
    }*/

    @Test
    public void testGetMobileTerminalList() throws Exception {
        doReturn(MOBILE_TERMINAL_LIST_RESPONSE).when(mobileTerminalServiceBean).getMobileTerminalList(null);
        MTResponseDto result = mobileTerminalRestResource.getMobileTerminalList(null);
        Mockito.verify(mobileTerminalServiceBean).getMobileTerminalList(null);
        assertEquals(SUCCESS_RESULT_LIST_RESPONSE.toString(), result.toString());
    }

    @Test
    public void testUpdateMobileTeriminal() throws Exception {
        doReturn(MOBILE_TERMINAL_DTO).when(mobileTerminalServiceBean).updateMobileTerminal(MOBILE_TERMINAL_DTO, "", MobileTerminalSource.INTERNAL, "TEST");
        doReturn("TEST").when(request).getRemoteUser();
        MTResponseDto result = mobileTerminalRestResource.updateMobileTerminal("", MOBILE_TERMINAL_DTO);
        Mockito.verify(mobileTerminalServiceBean).updateMobileTerminal(MOBILE_TERMINAL_DTO, "", MobileTerminalSource.INTERNAL, "TEST");
        assertEquals(SUCCESS_RESULT_UPDATE.toString(), result.toString());
    }

    /*@Test
    public void testCreateMobileTeriminal() throws Exception {
        doReturn(MOBILE_TERMINAL_DTO).when(mobileTerminalServiceBean).createAndPersistMobileTerminal(MOBILE_TERMINAL_DTO, MobileTerminalSource.INTERNAL, "TEST");
        doReturn("TEST").when(request).getRemoteUser();
        MTResponseDto result = mobileTerminalRestResource.createAndPersistMobileTerminal(MOBILE_TERMINAL_DTO);
        Mockito.verify(mobileTerminalServiceBean).createAndPersistMobileTerminal(MOBILE_TERMINAL_DTO, MobileTerminalSource.INTERNAL, "TEST");
        assertEquals(SUCCESS_RESULT_UPDATE.toString(), result.toString());
    }*/

    @Test
    public void checkDtoReturnsValid() {
        String VALUE = "HELLO_DTO";
        MTResponseDto dto = new MTResponseDto<>(VALUE, MTResponseCode.OK);
        assertEquals(dto.getCode().intValue(), MTResponseCode.OK.getCode());
        assertEquals(dto.getData(), VALUE);

        dto = new MTResponseDto<>(null, MTResponseCode.UNDEFINED_ERROR);
        assertEquals(dto.getCode().intValue(), MTResponseCode.UNDEFINED_ERROR.getCode());
        Assert.assertNull(dto.getData());
    }
}
