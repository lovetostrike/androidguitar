//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.07.02 at 03:44:47 PM EDT 
//


package edu.umd.cs.guitar.model.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OpenWindow" type="{}ComponentListType"/>
 *         &lt;element name="CloseWindow" type="{}ComponentListType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "openWindow",
    "closeWindow"
})
@XmlRootElement(name = "LogWidget")
public class LogWidget {

    @XmlElement(name = "OpenWindow", required = true)
    protected ComponentListType openWindow;
    @XmlElement(name = "CloseWindow", required = true)
    protected ComponentListType closeWindow;

    /**
     * Gets the value of the openWindow property.
     * 
     * @return
     *     possible object is
     *     {@link ComponentListType }
     *     
     */
    public ComponentListType getOpenWindow() {
        return openWindow;
    }

    /**
     * Sets the value of the openWindow property.
     * 
     * @param value
     *     allowed object is
     *     {@link ComponentListType }
     *     
     */
    public void setOpenWindow(ComponentListType value) {
        this.openWindow = value;
    }

    /**
     * Gets the value of the closeWindow property.
     * 
     * @return
     *     possible object is
     *     {@link ComponentListType }
     *     
     */
    public ComponentListType getCloseWindow() {
        return closeWindow;
    }

    /**
     * Sets the value of the closeWindow property.
     * 
     * @param value
     *     allowed object is
     *     {@link ComponentListType }
     *     
     */
    public void setCloseWindow(ComponentListType value) {
        this.closeWindow = value;
    }

}
