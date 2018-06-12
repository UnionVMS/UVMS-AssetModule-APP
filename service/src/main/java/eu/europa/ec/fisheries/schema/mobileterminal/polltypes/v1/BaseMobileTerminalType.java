
package eu.europa.ec.fisheries.schema.mobileterminal.types.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * <p>Java class for BaseMobileTerminalType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BaseMobileTerminalType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="mobileTerminalId" type="{urn:types.mobileterminal.schema.fisheries.ec.europa.eu:v1}MobileTerminalId"/&gt;
 *         &lt;element name="connectId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="plugin" type="{urn:types.mobileterminal.schema.fisheries.ec.europa.eu:v1}Plugin"/&gt;
 *         &lt;element name="source" type="{urn:types.mobileterminal.schema.fisheries.ec.europa.eu:v1}MobileTerminalSource"/&gt;
 *         &lt;element name="inactive" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="archived" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseMobileTerminalType", propOrder = {
    "mobileTerminalId",
    "connectId",
    "type",
    "plugin",
    "source",
    "inactive",
    "archived"
})
@XmlSeeAlso({
    MobileTerminalType.class
})
public class BaseMobileTerminalType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected MobileTerminalId mobileTerminalId;
    @XmlElement(required = true)
    protected String connectId;
    @XmlElement(required = true)
    protected String type;
    @XmlElement(required = true)
    protected Plugin plugin;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected MobileTerminalSource source;
    protected boolean inactive;
    protected boolean archived;

    /**
     * Gets the value of the mobileTerminalId property.
     * 
     * @return
     *     possible object is
     *     {@link MobileTerminalId }
     *     
     */
    public MobileTerminalId getMobileTerminalId() {
        return mobileTerminalId;
    }

    /**
     * Sets the value of the mobileTerminalId property.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileTerminalId }
     *     
     */
    public void setMobileTerminalId(MobileTerminalId value) {
        this.mobileTerminalId = value;
    }

    /**
     * Gets the value of the connectId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConnectId() {
        return connectId;
    }

    /**
     * Sets the value of the connectId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConnectId(String value) {
        this.connectId = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the plugin property.
     * 
     * @return
     *     possible object is
     *     {@link Plugin }
     *     
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Sets the value of the plugin property.
     * 
     * @param value
     *     allowed object is
     *     {@link Plugin }
     *     
     */
    public void setPlugin(Plugin value) {
        this.plugin = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link MobileTerminalSource }
     *     
     */
    public MobileTerminalSource getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link MobileTerminalSource }
     *     
     */
    public void setSource(MobileTerminalSource value) {
        this.source = value;
    }

    /**
     * Gets the value of the inactive property.
     * 
     */
    public boolean isInactive() {
        return inactive;
    }

    /**
     * Sets the value of the inactive property.
     * 
     */
    public void setInactive(boolean value) {
        this.inactive = value;
    }

    /**
     * Gets the value of the archived property.
     * 
     */
    public boolean isArchived() {
        return archived;
    }

    /**
     * Sets the value of the archived property.
     * 
     */
    public void setArchived(boolean value) {
        this.archived = value;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
