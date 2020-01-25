//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.07.28 at 05:34:43 PM CEST 
//


package ch.ehi.oereb.schemas.gml._3_2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CompositeSurfaceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompositeSurfaceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractSurfaceType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}surfaceMember" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/gml/3.2}AggregationAttributeGroup"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompositeSurfaceType", propOrder = {
    "surfaceMember"
})
public class CompositeSurfaceTypeType
    extends AbstractSurfaceTypeType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElementRef(name = "surfaceMember", namespace = "http://www.opengis.net/gml/3.2", type = SurfaceMember.class)
    protected List<SurfaceMember> surfaceMember;
    @XmlAttribute(name = "aggregationType")
    protected AggregationTypeType aggregationType;

    /**
     * Gets the value of the surfaceMember property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the surfaceMember property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSurfaceMember().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SurfaceMember }
     * 
     * 
     */
    public List<SurfaceMember> getSurfaceMember() {
        if (surfaceMember == null) {
            surfaceMember = new ArrayList<SurfaceMember>();
        }
        return this.surfaceMember;
    }

    /**
     * Gets the value of the aggregationType property.
     * 
     * @return
     *     possible object is
     *     {@link AggregationTypeType }
     *     
     */
    public AggregationTypeType getAggregationType() {
        return aggregationType;
    }

    /**
     * Sets the value of the aggregationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AggregationTypeType }
     *     
     */
    public void setAggregationType(AggregationTypeType value) {
        this.aggregationType = value;
    }

}
