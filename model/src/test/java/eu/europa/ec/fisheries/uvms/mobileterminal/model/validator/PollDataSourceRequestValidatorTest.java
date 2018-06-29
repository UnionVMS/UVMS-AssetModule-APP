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

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
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
public class PollDataSourceRequestValidatorTest {
	
	@Mock
	private	PollRequestType requestType;
	
    @Before
    public void setUp() {
    	MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testCheckSamplingPollParams() throws MobileTerminalModelException {
    	List<PollAttribute> attrList = new ArrayList<>();
    	when(requestType.getAttributes()).thenReturn(attrList);
    	try {
    		PollDataSourceRequestValidator.checkSamplingPollParams(requestType);
    		fail("Sampling poll.");
    	} catch (MobileTerminalModelException e) {
			assertTrue(true);
    	}
    	
    	PollAttribute startDate = new PollAttribute();
    	startDate.setKey(PollAttributeType.START_DATE);
    	PollAttribute stopDate = new PollAttribute();
    	stopDate.setKey(PollAttributeType.END_DATE);
    	attrList.add(startDate);
    	attrList.add(stopDate);
    	
    	PollDataSourceRequestValidator.checkSamplingPollParams(requestType);
    }
    
    @Test
    public void testCheckProgramPollParams() throws MobileTerminalModelException {
    	List<PollAttribute> attrList = new ArrayList<>();
    	when(requestType.getAttributes()).thenReturn(attrList);
    	
    	try {
    		PollDataSourceRequestValidator.checkProgramPollParams(requestType);
    		fail("ProgramPoll poll.");
    	} catch (MobileTerminalModelException e) {
			assertTrue(true);
    	}
    	
    	PollAttribute startDate = new PollAttribute();
    	startDate.setKey(PollAttributeType.START_DATE);
    	PollAttribute stopDate = new PollAttribute();
    	stopDate.setKey(PollAttributeType.END_DATE);
    	PollAttribute frequency = new PollAttribute();
    	frequency.setKey(PollAttributeType.FREQUENCY);
    	attrList.add(startDate);
    	attrList.add(stopDate);
    	attrList.add(frequency);
    	
    	PollDataSourceRequestValidator.checkProgramPollParams(requestType);
    }
    
    @Test
    public void testCheckConfigurationPollParams() throws MobileTerminalModelException {
    	List<PollAttribute> attrList = new ArrayList<>();
    	when(requestType.getAttributes()).thenReturn(attrList);
    	
    	try {
    		PollDataSourceRequestValidator.checkConfigurationPollParams(requestType);
    		fail("Configuration poll.");
    	} catch (MobileTerminalModelException e) {
			assertTrue(true);
    	}
    	
    	PollAttribute reportFrequency = new PollAttribute();
    	reportFrequency.setKey(PollAttributeType.REPORT_FREQUENCY);
    	PollAttribute gracePeriod = new PollAttribute();
    	gracePeriod.setKey(PollAttributeType.GRACE_PERIOD);
    	PollAttribute inPortGrace = new PollAttribute();
    	inPortGrace.setKey(PollAttributeType.IN_PORT_GRACE);
    	PollAttribute dnid = new PollAttribute();
    	dnid.setKey(PollAttributeType.DNID);
    	PollAttribute memberNumber = new PollAttribute();
    	memberNumber.setKey(PollAttributeType.MEMBER_NUMBER);
    	attrList.add(reportFrequency);
    	attrList.add(gracePeriod);
    	attrList.add(inPortGrace);
    	attrList.add(dnid);
    	attrList.add(memberNumber);
    	
    	PollDataSourceRequestValidator.checkConfigurationPollParams(requestType);
    }
    
    @Test
    public void testMobileTerminal() {
    	List<PollMobileTerminal> mobTermList = new ArrayList<>();
		when(requestType.getMobileTerminals()).thenReturn(mobTermList);
		try {
			PollDataSourceRequestValidator.validateMobileTerminals(requestType);
			fail("No mobterminals to poll");
		} catch (MobileTerminalModelException e) {
			assertTrue(true);
		}
		
		PollMobileTerminal pollMobTerm = new PollMobileTerminal();
		PollMobileTerminal extraPollMobTerm = new PollMobileTerminal();
		when(requestType.getPollType()).thenReturn(PollType.SAMPLING_POLL);
		
		mobTermList.add(pollMobTerm);
		mobTermList.add(extraPollMobTerm);
		
		try {
			PollDataSourceRequestValidator.validateMobileTerminals(requestType);
			fail("Sampling poll too many");
		} catch (MobileTerminalModelException e) {
			assertTrue(true);
		}
    }
    
    @Test
    public void testMobileTerminalConfigurationPoll() {
    	List<PollMobileTerminal> mobTermList = new ArrayList<>();
		when(requestType.getMobileTerminals()).thenReturn(mobTermList);
		
		PollMobileTerminal pollMobTerm = new PollMobileTerminal();
		PollMobileTerminal extraPollMobTerm = new PollMobileTerminal();
		
		mobTermList.add(pollMobTerm);
		mobTermList.add(extraPollMobTerm);
    	
    	List<PollAttribute> pollAttrList = new ArrayList<>();
		
		when(requestType.getAttributes()).thenReturn(pollAttrList);
		when(requestType.getPollType()).thenReturn(PollType.CONFIGURATION_POLL);
		
		try {
			PollDataSourceRequestValidator.validateMobileTerminals(requestType);
		} catch (MobileTerminalModelException e) {
			fail("Valid configuration poll without dnid/memberNumber");
		}
		
		PollAttribute dnid = new PollAttribute();
		dnid.setKey(PollAttributeType.DNID);
		pollAttrList.add(dnid);
		
		try {
			PollDataSourceRequestValidator.validateMobileTerminals(requestType);
			fail("Configuration poll too many");
		} catch (MobileTerminalModelException e) {
			mobTermList.remove(extraPollMobTerm);
		}
		
		try {
			PollDataSourceRequestValidator.validateMobileTerminals(requestType);
		} catch (MobileTerminalModelException e) {
			fail("Valid single configuration poll");
		}
    }

	@Test
	public void testHasUser() throws MobileTerminalModelException {
		PollDataSourceRequestValidator.validateHasUser("TEST_USER");
	}
    
    @Test(expected = MobileTerminalModelException.class)
    public void testHasUser_NULL() throws MobileTerminalModelException {
		PollDataSourceRequestValidator.validateHasUser(null);
    }

	@Test(expected = MobileTerminalModelException.class)
	public void testHasUser_EMPTY() throws MobileTerminalModelException {
		PollDataSourceRequestValidator.validateHasUser("");
	}
}