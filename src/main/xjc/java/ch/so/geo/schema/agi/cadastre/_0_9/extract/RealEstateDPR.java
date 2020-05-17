//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.05.09 at 08:20:25 PM CEST 
//


package ch.so.geo.schema.agi.cadastre._0_9.extract;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for RealEstate_DPR complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RealEstate_DPR"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Number" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString"&gt;
 *               &lt;minLength value="12"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="IdentND" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString"&gt;
 *               &lt;minLength value="12"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="EGRID" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString"&gt;
 *               &lt;minLength value="14"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="LocalName" type="{http://geo.so.ch/schema/AGI/Cadastre/0.9/Extract}LocalNameType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="LandCoverShare" type="{http://geo.so.ch/schema/AGI/Cadastre/0.9/Extract}LandCoverShareType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="SurveyorOffice" type="{http://geo.so.ch/schema/AGI/Cadastre/0.9/Extract}OrganisationType"/&gt;
 *         &lt;element name="LandRegisterOffice" type="{http://geo.so.ch/schema/AGI/Cadastre/0.9/Extract}OrganisationType"/&gt;
 *         &lt;element name="Type" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Municipality" type="{http://www.w3.org/2001/XMLSchema}normalizedString"/&gt;
 *         &lt;element name="SubunitOfLandRegister" type="{http://www.w3.org/2001/XMLSchema}normalizedString" minOccurs="0"/&gt;
 *         &lt;element name="LandRegistryArea"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *               &lt;minExclusive value="0"/&gt;
 *               &lt;maxExclusive value="999999999"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Map" type="{http://geo.so.ch/schema/AGI/Cadastre/0.9/Extract}Map"/&gt;
 *         &lt;element name="Building" type="{http://geo.so.ch/schema/AGI/Cadastre/0.9/Extract}BuildingType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="StateOf" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="SupervisionOffice" type="{http://geo.so.ch/schema/AGI/Cadastre/0.9/Extract}OrganisationType"/&gt;
 *         &lt;element name="Limit" type="{http://www.w3.org/2001/XMLSchema}normalizedString"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RealEstate_DPR", propOrder = {
    "number",
    "identND",
    "egrid",
    "localNames",
    "landCoverShares",
    "surveyorOffice",
    "landRegisterOffice",
    "type",
    "municipality",
    "subunitOfLandRegister",
    "landRegistryArea",
    "map",
    "buildings",
    "stateOf",
    "supervisionOffice",
    "limit"
})
public class RealEstateDPR {

    @XmlElement(name = "Number")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String number;
    @XmlElement(name = "IdentND")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String identND;
    @XmlElement(name = "EGRID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String egrid;
    @XmlElement(name = "LocalName")
    protected List<LocalNameType> localNames;
    @XmlElement(name = "LandCoverShare")
    protected List<LandCoverShareType> landCoverShares;
    @XmlElement(name = "SurveyorOffice", required = true)
    protected OrganisationType surveyorOffice;
    @XmlElement(name = "LandRegisterOffice", required = true)
    protected OrganisationType landRegisterOffice;
    @XmlElement(name = "Type", required = true)
    protected String type;
    @XmlElement(name = "Municipality", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String municipality;
    @XmlElement(name = "SubunitOfLandRegister")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String subunitOfLandRegister;
    @XmlElement(name = "LandRegistryArea")
    protected int landRegistryArea;
    @XmlElement(name = "Map", required = true)
    protected Map map;
    @XmlElement(name = "Building")
    protected List<BuildingType> buildings;
    @XmlElement(name = "StateOf", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar stateOf;
    @XmlElement(name = "SupervisionOffice", required = true)
    protected OrganisationType supervisionOffice;
    @XmlElement(name = "Limit", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String limit;

    /**
     * Gets the value of the number property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the value of the number property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumber(String value) {
        this.number = value;
    }

    /**
     * Gets the value of the identND property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentND() {
        return identND;
    }

    /**
     * Sets the value of the identND property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentND(String value) {
        this.identND = value;
    }

    /**
     * Gets the value of the egrid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEGRID() {
        return egrid;
    }

    /**
     * Sets the value of the egrid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEGRID(String value) {
        this.egrid = value;
    }

    /**
     * Gets the value of the localNames property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the localNames property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocalNames().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LocalNameType }
     * 
     * 
     */
    public List<LocalNameType> getLocalNames() {
        if (localNames == null) {
            localNames = new ArrayList<LocalNameType>();
        }
        return this.localNames;
    }

    /**
     * Gets the value of the landCoverShares property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the landCoverShares property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLandCoverShares().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LandCoverShareType }
     * 
     * 
     */
    public List<LandCoverShareType> getLandCoverShares() {
        if (landCoverShares == null) {
            landCoverShares = new ArrayList<LandCoverShareType>();
        }
        return this.landCoverShares;
    }

    /**
     * Gets the value of the surveyorOffice property.
     * 
     * @return
     *     possible object is
     *     {@link OrganisationType }
     *     
     */
    public OrganisationType getSurveyorOffice() {
        return surveyorOffice;
    }

    /**
     * Sets the value of the surveyorOffice property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganisationType }
     *     
     */
    public void setSurveyorOffice(OrganisationType value) {
        this.surveyorOffice = value;
    }

    /**
     * Gets the value of the landRegisterOffice property.
     * 
     * @return
     *     possible object is
     *     {@link OrganisationType }
     *     
     */
    public OrganisationType getLandRegisterOffice() {
        return landRegisterOffice;
    }

    /**
     * Sets the value of the landRegisterOffice property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganisationType }
     *     
     */
    public void setLandRegisterOffice(OrganisationType value) {
        this.landRegisterOffice = value;
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
     * Gets the value of the municipality property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMunicipality() {
        return municipality;
    }

    /**
     * Sets the value of the municipality property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMunicipality(String value) {
        this.municipality = value;
    }

    /**
     * Gets the value of the subunitOfLandRegister property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubunitOfLandRegister() {
        return subunitOfLandRegister;
    }

    /**
     * Sets the value of the subunitOfLandRegister property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubunitOfLandRegister(String value) {
        this.subunitOfLandRegister = value;
    }

    /**
     * Gets the value of the landRegistryArea property.
     * 
     */
    public int getLandRegistryArea() {
        return landRegistryArea;
    }

    /**
     * Sets the value of the landRegistryArea property.
     * 
     */
    public void setLandRegistryArea(int value) {
        this.landRegistryArea = value;
    }

    /**
     * Gets the value of the map property.
     * 
     * @return
     *     possible object is
     *     {@link Map }
     *     
     */
    public Map getMap() {
        return map;
    }

    /**
     * Sets the value of the map property.
     * 
     * @param value
     *     allowed object is
     *     {@link Map }
     *     
     */
    public void setMap(Map value) {
        this.map = value;
    }

    /**
     * Gets the value of the buildings property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the buildings property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBuildings().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BuildingType }
     * 
     * 
     */
    public List<BuildingType> getBuildings() {
        if (buildings == null) {
            buildings = new ArrayList<BuildingType>();
        }
        return this.buildings;
    }

    /**
     * Gets the value of the stateOf property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStateOf() {
        return stateOf;
    }

    /**
     * Sets the value of the stateOf property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStateOf(XMLGregorianCalendar value) {
        this.stateOf = value;
    }

    /**
     * Gets the value of the supervisionOffice property.
     * 
     * @return
     *     possible object is
     *     {@link OrganisationType }
     *     
     */
    public OrganisationType getSupervisionOffice() {
        return supervisionOffice;
    }

    /**
     * Sets the value of the supervisionOffice property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganisationType }
     *     
     */
    public void setSupervisionOffice(OrganisationType value) {
        this.supervisionOffice = value;
    }

    /**
     * Gets the value of the limit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLimit() {
        return limit;
    }

    /**
     * Sets the value of the limit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLimit(String value) {
        this.limit = value;
    }

}
