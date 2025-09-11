
package de.persosim.simulator.control.soap.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für persoSimRemoteControlResult complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="persoSimRemoteControlResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="resultAsHex" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="resultCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="resultMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="resultPrettyPrint" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "persoSimRemoteControlResult", propOrder = {
    "resultAsHex",
    "resultCode",
    "resultMessage",
    "resultPrettyPrint"
})
public class PersoSimRemoteControlResult {

    protected String resultAsHex;
    protected int resultCode;
    protected String resultMessage;
    protected String resultPrettyPrint;

    /**
     * Ruft den Wert der resultAsHex-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultAsHex() {
        return resultAsHex;
    }

    /**
     * Legt den Wert der resultAsHex-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultAsHex(String value) {
        this.resultAsHex = value;
    }

    /**
     * Ruft den Wert der resultCode-Eigenschaft ab.
     * 
     */
    public int getResultCode() {
        return resultCode;
    }

    /**
     * Legt den Wert der resultCode-Eigenschaft fest.
     * 
     */
    public void setResultCode(int value) {
        this.resultCode = value;
    }

    /**
     * Ruft den Wert der resultMessage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultMessage() {
        return resultMessage;
    }

    /**
     * Legt den Wert der resultMessage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultMessage(String value) {
        this.resultMessage = value;
    }

    /**
     * Ruft den Wert der resultPrettyPrint-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultPrettyPrint() {
        return resultPrettyPrint;
    }

    /**
     * Legt den Wert der resultPrettyPrint-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultPrettyPrint(String value) {
        this.resultPrettyPrint = value;
    }

}
