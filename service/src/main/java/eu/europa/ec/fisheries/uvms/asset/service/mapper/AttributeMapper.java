/*
 Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
 © European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
 redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
 the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
 copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.mobileterminal.mapper;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by osdjup on 2016-11-16.
 */
public class AttributeMapper {

    public static List<Map<String, String>> mapAttributeStrings(List<String> attributeStrings) {
        List<Map<String, String>> attributes = new ArrayList<>();
        for (String attributeString : attributeStrings) {
            attributes.add(mapAttributeString(attributeString));
        }

        return attributes;
    }

    public static Map<String, String> mapAttributeString(String attributeString) {
        Map<String, String> attributes = new HashMap<>();

        String[] parts = attributeString.split(";");
        for (String attribute : parts) {
            String[] pair =attribute.split("=");
            attributes.put(pair[0], pair[1]);
        }

        return attributes;
    }

    static List<ComChannelAttribute> mapAttributeStringToComChannelAttribute(String attributeString) {
        List<ComChannelAttribute> attributeList = new ArrayList<>();
        Map<String, String> attributes = mapAttributeString(attributeString);
        for (String key : attributes.keySet()) {
            ComChannelAttribute attribute = new ComChannelAttribute();
            attribute.setType(key);
            attribute.setValue(attributes.get(key));
            attributeList.add(attribute);
        }

        return attributeList;
    }

    static List<MobileTerminalAttribute> mapAttributeStringToTerminalAttribute(String attributeString) {
        List<MobileTerminalAttribute> attributeList = new ArrayList<>();
        Map<String, String> attributes = mapAttributeString(attributeString);
        for (String key : attributes.keySet()) {
            MobileTerminalAttribute attribute = new MobileTerminalAttribute();
            attribute.setType(key);
            attribute.setValue(attributes.get(key));
            attributeList.add(attribute);
        }
        return attributeList;
    }
}
