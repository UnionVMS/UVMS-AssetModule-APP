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
package eu.europa.fisheries.uvms.asset.service;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import eu.europa.ec.fisheries.uvms.asset.service.constants.ServiceConstants;
import eu.europa.ec.fisheries.uvms.asset.service.property.ParameterKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import eu.europa.ec.fisheries.uvms.config.service.ParameterServiceBean;
import eu.europa.ec.fisheries.uvms.config.service.entity.Parameter;

@RunWith(MockitoJUnitRunner.class)
public class ParameterServiceTest {

    @Mock
    EntityManager em;

    @InjectMocks
    private ParameterServiceBean bean;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testParameterNone() {

    }

    //@Test
    public void testParameterByName() throws Exception {
        String value = "testValue";
        Parameter parameter = new Parameter();
        parameter.setParamValue(value);
        TypedQuery<Parameter> query = mock(TypedQuery.class);
        when(em.createNamedQuery(ServiceConstants.FIND_BY_NAME)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(parameter);

        String beanValue = bean.getStringValue(ParameterKey.EU_USE.getKey());
        assertSame(value, beanValue);
    }

}