
package eu.europa.ec.fisheries.schema.mobileterminal.types.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PluginCapabilityType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PluginCapabilityType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="POLLABLE"/&gt;
 *     &lt;enumeration value="CONFIGURABLE"/&gt;
 *     &lt;enumeration value="ONLY_SINGLE_OCEAN"/&gt;
 *     &lt;enumeration value="MULTIPLE_OCEAN"/&gt;
 *     &lt;enumeration value="SAMPLING"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "PluginCapabilityType")
@XmlEnum
public enum PluginCapabilityType {

    POLLABLE,
    CONFIGURABLE,
    ONLY_SINGLE_OCEAN,
    MULTIPLE_OCEAN,
    SAMPLING;

    public String value() {
        return name();
    }

    public static PluginCapabilityType fromValue(String v) {
        return valueOf(v);
    }

}
