//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.07.28 at 05:34:43 PM CEST 
//


package ch.ehi.oereb.schemas.oereb._1_0.extractdata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CantonCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CantonCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ZH"/>
 *     &lt;enumeration value="BE"/>
 *     &lt;enumeration value="LU"/>
 *     &lt;enumeration value="UR"/>
 *     &lt;enumeration value="SZ"/>
 *     &lt;enumeration value="OW"/>
 *     &lt;enumeration value="NW"/>
 *     &lt;enumeration value="GL"/>
 *     &lt;enumeration value="ZG"/>
 *     &lt;enumeration value="FR"/>
 *     &lt;enumeration value="SO"/>
 *     &lt;enumeration value="BS"/>
 *     &lt;enumeration value="BL"/>
 *     &lt;enumeration value="SH"/>
 *     &lt;enumeration value="AR"/>
 *     &lt;enumeration value="AI"/>
 *     &lt;enumeration value="SG"/>
 *     &lt;enumeration value="GR"/>
 *     &lt;enumeration value="AG"/>
 *     &lt;enumeration value="TG"/>
 *     &lt;enumeration value="TI"/>
 *     &lt;enumeration value="VD"/>
 *     &lt;enumeration value="VS"/>
 *     &lt;enumeration value="NE"/>
 *     &lt;enumeration value="GE"/>
 *     &lt;enumeration value="JU"/>
 *     &lt;enumeration value="FL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CantonCode")
@XmlEnum
public enum CantonCodeType {

    ZH,
    BE,
    LU,
    UR,
    SZ,
    OW,
    NW,
    GL,
    ZG,
    FR,
    SO,
    BS,
    BL,
    SH,
    AR,
    AI,
    SG,
    GR,
    AG,
    TG,
    TI,
    VD,
    VS,
    NE,
    GE,
    JU,
    FL;

    public String value() {
        return name();
    }

    public static CantonCodeType fromValue(String v) {
        return valueOf(v);
    }

}
