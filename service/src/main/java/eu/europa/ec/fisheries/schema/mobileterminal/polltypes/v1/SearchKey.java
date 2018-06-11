package eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/*

OBS THIS CLASS SHOULD MAYBE COME FROM XSD gen instead



 */


/**
 * <p>Java class for SearchKey.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SearchKey"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="POLL_ID"/&gt;
 *     &lt;enumeration value="CONNECT_ID"/&gt;
 *     &lt;enumeration value="POLL_TYPE"/&gt;
 *     &lt;enumeration value="TERMINAL_TYPE"/&gt;
 *     &lt;enumeration value="USER"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */


@XmlType(name = "SearchKey")
@XmlEnum
public enum SearchKey {

    POLL_ID,
    CONNECT_ID,
    POLL_TYPE,
    TERMINAL_TYPE,
    USER;

    public String value() {
        return name();
    }

    public static SearchKey fromValue(String v) {
        return valueOf(v);
    }

}
