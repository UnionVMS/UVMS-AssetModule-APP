
package eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MobileTerminalSource.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MobileTerminalSource"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="INTERNAL"/&gt;
 *     &lt;enumeration value="NATIONAL"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "MobileTerminalSource")
@XmlEnum
public enum MobileTerminalSource {

    INTERNAL,
    NATIONAL;

    public String value() {
        return name();
    }

    public static MobileTerminalSource fromValue(String v) {
        return valueOf(v);
    }

}
