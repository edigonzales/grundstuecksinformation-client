//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.07.28 at 05:34:43 PM CEST 
//


package ch.ehi.oereb.schemas.gml._3_2;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TimeInstantType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TimeInstantType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractTimeGeometricPrimitiveType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}timePosition"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimeInstantType", propOrder = {
    "timePosition"
})
public class TimeInstantTypeType
    extends AbstractTimeGeometricPrimitiveTypeType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElementRef(name = "timePosition", namespace = "http://www.opengis.net/gml/3.2", type = TimePosition.class)
    protected TimePosition timePosition;

    /**
     * Gets the value of the timePosition property.
     * 
     * @return
     *     possible object is
     *     {@link TimePosition }
     *     
     */
    public TimePosition getTimePosition() {
        return timePosition;
    }

    /**
     * Sets the value of the timePosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimePosition }
     *     
     */
    public void setTimePosition(TimePosition value) {
        this.timePosition = value;
    }

}
