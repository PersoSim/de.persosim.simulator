package de.persosim.simulator.perso.export;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.persosim.simulator.protocols.GenericOid;

@JsonPropertyOrder({ //
		"oid", //
		"privilegedOnly", //
		"id", //
		"content" //
})
public class Key
{
	private GenericOid oid;
	private Boolean privilegedOnly;
	private Integer id;
	private String content;

	public Key()
	{
		// do nothing; default constructor necessary for JSON (de-)serialization
	}

	public Key(GenericOid oid, Boolean privilegedOnly, Integer id, String content)
	{
		this.oid = oid;
		this.privilegedOnly = privilegedOnly;
		this.id = id;
		this.content = content;
	}

	public String getOid()
	{
		return oid.toDotString();
	}

	@JsonIgnore
	public GenericOid getOidInternal()
	{
		return oid;
	}

	public void setOid(GenericOid oid)
	{
		this.oid = oid;
	}

	public Boolean getPrivilegedOnly()
	{
		return privilegedOnly;
	}

	public void setPrivilegedOnly(Boolean privilegedOnly)
	{
		this.privilegedOnly = privilegedOnly;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}
}
