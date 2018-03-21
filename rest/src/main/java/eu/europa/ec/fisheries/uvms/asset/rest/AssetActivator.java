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
package eu.europa.ec.fisheries.uvms.asset.rest;

import eu.europa.ec.fisheries.uvms.asset.rest.service.AssetGroupResource;
import eu.europa.ec.fisheries.uvms.asset.rest.service.AssetResource;
import eu.europa.ec.fisheries.uvms.asset.rest.service.ConfigResource;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeatureFilter;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/rest")
public class AssetActivator extends Application {

    private final Set<Object> singletons = new HashSet<>();
    private final Set<Class<?>> set = new HashSet<>();

    public AssetActivator() {
        set.add(AssetResource.class);
        set.add(AssetGroupResource.class);
        set.add(ConfigResource.class);
        set.add(UnionVMSFeatureFilter.class);
        set.add(ObjectMapperContextResolver.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return set;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

}