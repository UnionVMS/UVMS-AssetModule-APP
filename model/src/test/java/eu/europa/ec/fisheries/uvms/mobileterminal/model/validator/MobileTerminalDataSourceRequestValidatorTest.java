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
package eu.europa.ec.fisheries.uvms.mobileterminal.model.validator;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.exception.MobileTerminalModelException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MobileTerminalDataSourceRequestValidatorTest {
	
	@Mock
	private	MobileTerminalType mobTermType;
	
    @Before
    public void setUp() {
    	MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testValidateComChannels() {
    	ComChannelType channel = new ComChannelType();
    	channel.setName("VMS");
		ComChannelAttribute dnid = new ComChannelAttribute();
		dnid.setType("DNID");
		channel.getAttributes().add(dnid);
		
		List<ComChannelType> comchannelList = new ArrayList<ComChannelType>();
		comchannelList.add(channel);
		when(mobTermType.getChannels()).thenReturn(comchannelList);
		
    	try {
    		MobileTerminalDataSourceRequestValidator.validateComChannels(mobTermType);
    		fail("Missing field");
    	} catch (MobileTerminalModelException e) {
    		assertTrue(true);
    	}
    	
    	ComChannelAttribute memberNumber = new ComChannelAttribute();
		memberNumber.setType("MEMBER_NUMBER");
		channel.getAttributes().add(memberNumber);
		
		try {
    		MobileTerminalDataSourceRequestValidator.validateComChannels(mobTermType);
    	} catch (MobileTerminalModelException e) {
    		fail("Should validate");
    	}
    	
    	ComChannelAttribute dnid2 = new ComChannelAttribute();
		dnid2.setType("DNID");
		channel.getAttributes().add(dnid2);
		
		try {
    		MobileTerminalDataSourceRequestValidator.validateComChannels(mobTermType);
    		fail("Multiple fields");
    	} catch (MobileTerminalModelException e) {
			assertTrue(true);
    	}
    }
    
    @Test
    public void testValidateMobileTerminalAttributeMultipleOceans() throws MobileTerminalModelException {
    	List<MobileTerminalAttribute> attrList = new ArrayList<>();
    	MobileTerminalAttribute attr = new MobileTerminalAttribute();
    	attr.setType("MULTIPLE_OCEAN");
    	attrList.add(attr);
    	attrList.add(attr);
    	MobileTerminalDataSourceRequestValidator.validateMobileTerminalAttributes(attrList);
    }
    
    @Test(expected=MobileTerminalModelException.class)
    public void testValidateMobileTerminalAttributeNonUnique() throws MobileTerminalModelException {
    	List<MobileTerminalAttribute> attrList = new ArrayList<>();
    	MobileTerminalAttribute attr = new MobileTerminalAttribute();
    	attr.setType("ANTENNA");
    	attrList.add(attr);
    	attrList.add(attr);
    	MobileTerminalDataSourceRequestValidator.validateMobileTerminalAttributes(attrList);
    }
    
    @Test
    public void testValidateMobileTerminalAttributes() throws MobileTerminalModelException {
    	List<MobileTerminalAttribute> attrList = new ArrayList<>();
    	MobileTerminalAttribute attr = new MobileTerminalAttribute();
    	attr.setType("ANTENNA");
    	attrList.add(attr);
    	MobileTerminalDataSourceRequestValidator.validateMobileTerminalAttributes(attrList);
    }
    
    @Test
    public void testValidateMobileTerminalAttributesEmpty() throws MobileTerminalModelException {
    	List<MobileTerminalAttribute> attrList = new ArrayList<>();
    	MobileTerminalDataSourceRequestValidator.validateMobileTerminalAttributes(attrList);
    }
    
    @Test
    public void testValidateMobileTerminalId() {
    	
		try {
			MobileTerminalDataSourceRequestValidator.validateMobileTerminalId(null);
			fail("MobileTerminalId NULL");
		} catch (MobileTerminalModelException e) {
			assertTrue(true);
		}
		
		MobileTerminalId id = new MobileTerminalId();
		try {
			MobileTerminalDataSourceRequestValidator.validateMobileTerminalId(id);
			fail("MobileTerminalId GUID NULL ");
		} catch (MobileTerminalModelException e) {
			assertTrue(true);
		}
		
		id.setGuid("");
		try {
			MobileTerminalDataSourceRequestValidator.validateMobileTerminalId(id);
			fail("MobileTerminalId GUID Empty");
		} catch (MobileTerminalModelException e) {
			assertTrue(true);
		}
		id.setGuid("ABCD-EFGH-IJKL-MNOP-1234-5678-0000");
		
		try {
			MobileTerminalDataSourceRequestValidator.validateMobileTerminalId(id);
		} catch (MobileTerminalModelException e) {
			fail("MobileTerminalId valid");
		}
    }
}