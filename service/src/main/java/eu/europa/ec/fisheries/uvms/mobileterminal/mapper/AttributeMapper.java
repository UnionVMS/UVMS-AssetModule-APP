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
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by osdjup on 2016-11-16.
 */
public class AttributeMapper {

    public static final String DNID = "DNID";
    public static final String FREQUENCY_EXPECTED = "FREQUENCY_EXPECTED";
    public static final String FREQUENCY_IN_PORT = "FREQUENCY_IN_PORT";
    public static final String LES_DESCRIPTION = "LES_DESCRIPTION";
    public static final String FREQUENCY_GRACE_PERIOD = "FREQUENCY_GRACE_PERIOD";
    public static final String MEMBER_NUMBER = "MEMBER_NUMBER";
    public static final String INSTALLED_BY = "INSTALLED_BY";
    public static final String INSTALLED_ON = "INSTALLED_ON";
    public static final String UNINSTALLED_ON = "UNINSTALLED_ON";
    public static final String START_DATE = "START_DATE";
    public static final String END_DATE = "END_DATE";

    static List<ComChannelAttribute> mapAttributeStringToComChannelAttribute(Channel channel) {
        List<ComChannelAttribute> attributeList = new ArrayList<>();
        //adding all the values that where previsouly in on long string in the DB
        attributeList.add(insertKeyAndValueIntoComChannelAttribute(DNID,channel.getDNID()));
        attributeList.add(insertKeyAndValueIntoComChannelAttribute(FREQUENCY_EXPECTED,"" + channel.getExpectedFrequency().getSeconds()));
        attributeList.add(insertKeyAndValueIntoComChannelAttribute(FREQUENCY_IN_PORT, "" + channel.getExpectedFrequencyInPort().getSeconds()));
        attributeList.add(insertKeyAndValueIntoComChannelAttribute(LES_DESCRIPTION, channel.getLesDescription()));
        attributeList.add(insertKeyAndValueIntoComChannelAttribute(FREQUENCY_GRACE_PERIOD, "" + channel.getFrequencyGracePeriod().getSeconds()));
        attributeList.add(insertKeyAndValueIntoComChannelAttribute(MEMBER_NUMBER, channel.getMemberNumber()));
        attributeList.add(insertKeyAndValueIntoComChannelAttribute(INSTALLED_BY, channel.getInstalledBy()));
        attributeList.add(insertKeyAndValueIntoComChannelAttribute(INSTALLED_ON, DateUtils.parseOffsetDateTimeToString(channel.getInstallDate())));
        attributeList.add(insertKeyAndValueIntoComChannelAttribute(UNINSTALLED_ON, DateUtils.parseOffsetDateTimeToString(channel.getUninstallDate())));
        attributeList.add(insertKeyAndValueIntoComChannelAttribute(START_DATE, DateUtils.parseOffsetDateTimeToString(channel.getStartDate())));
        attributeList.add(insertKeyAndValueIntoComChannelAttribute(END_DATE, DateUtils.parseOffsetDateTimeToString(channel.getEndDate())));

        return attributeList;
    }

    private static ComChannelAttribute insertKeyAndValueIntoComChannelAttribute(String key, String value){
        ComChannelAttribute attr = new ComChannelAttribute();
        attr.setType(key);
        attr.setValue(value);
        return attr;
    }
}
