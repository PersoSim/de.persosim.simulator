package de.persosim.simulator.control.soap.service;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import jakarta.annotation.Nullable;

@XmlRootElement
public class PersoSimRemoteControlResult implements Serializable
{
	private static final long serialVersionUID = -4294208857545442456L;
	private int resultCode;
	private String resultMessage;
	private String resultAsHex;
	private String resultPrettyPrint;

	/**
	 * Empty constructor for (de-)serialization
	 */
	public PersoSimRemoteControlResult()
	{
		// do nothing
	}

	public int getResultCode()
	{
		return resultCode;
	}

	public void setResultCode(int resultCode)
	{
		this.resultCode = resultCode;
	}

	public String getResultMessage()
	{
		return resultMessage;
	}

	public void setResultMessage(String resultMessage)
	{
		this.resultMessage = resultMessage;
	}

	@Nullable
	@XmlElement(nillable = true)
	public String getResultAsHex()
	{
		return resultAsHex;
	}

	public void setResultAsHex(String resultAsHex)
	{
		this.resultAsHex = resultAsHex;
	}

	@Nullable
	@XmlElement(nillable = true)
	public String getResultPrettyPrint()
	{
		return resultPrettyPrint;
	}

	public void setResultPrettyPrint(String resultPrettyPrint)
	{
		this.resultPrettyPrint = resultPrettyPrint;
	}
}
