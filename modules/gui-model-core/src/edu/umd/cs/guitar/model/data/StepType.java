//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.07.02 at 03:44:47 PM EDT 
//


package edu.umd.cs.guitar.model.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StepType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StepType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EventId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ReachingStep" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Parameter" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Optional" type="{}AttributesType" minOccurs="0"/>
 *         &lt;element ref="{}GUIStructure" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StepType", propOrder = {
    "eventId",
    "reachingStep",
    "parameter",
    "optional",
    "guiStructure"
})
public class StepType {

    @XmlElement(name = "EventId", required = true)
    protected String eventId;
    @XmlElement(name = "ReachingStep")
    protected boolean reachingStep;
    @XmlElement(name = "Parameter")
    protected List<String> parameter;
    @XmlElement(name = "Optional")
    protected AttributesType optional;
    @XmlElement(name = "GUIStructure")
    protected GUIStructure guiStructure;

    /**
     * Gets the value of the eventId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the value of the eventId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventId(String value) {
        this.eventId = value;
    }

    /**
     * Gets the value of the reachingStep property.
     * 
     */
    public boolean isReachingStep() {
        return reachingStep;
    }

    /**
     * Sets the value of the reachingStep property.
     * 
     */
    public void setReachingStep(boolean value) {
        this.reachingStep = value;
    }

    /**
     * Gets the value of the parameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<String>();
        }
        return this.parameter;
    }

    /**
     * Gets the value of the optional property.
     * 
     * @return
     *     possible object is
     *     {@link AttributesType }
     *     
     */
    public AttributesType getOptional() {
        return optional;
    }

    /**
     * Sets the value of the optional property.
     * 
     * @param value
     *     allowed object is
     *     {@link AttributesType }
     *     
     */
    public void setOptional(AttributesType value) {
        this.optional = value;
    }

    /**
     * Gets the value of the guiStructure property.
     * 
     * @return
     *     possible object is
     *     {@link GUIStructure }
     *     
     */
    public GUIStructure getGUIStructure() {
        return guiStructure;
    }

    /**
     * Sets the value of the guiStructure property.
     * 
     * @param value
     *     allowed object is
     *     {@link GUIStructure }
     *     
     */
    public void setGUIStructure(GUIStructure value) {
        this.guiStructure = value;
    }

    /**
     * Sets the value of the parameter property.
     * 
     * @param parameter
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParameter(List<String> parameter) {
        this.parameter = parameter;
    }

}
