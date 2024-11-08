package de.persosim.simulator.perso.export;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ //
		"keys" })
public class OverlayProfile extends ProfileBase
{
	private List<Key> keys = new ArrayList<>();


	public OverlayProfile()
	{
		// do nothing; default constructor necessary for JSON (de-)serialization
	}

	public OverlayProfile(List<Key> keys)
	{
		if (keys != null)
			this.keys = keys;
	}

	public List<Key> getKeys()
	{
		return keys;
	}

	public void setKeys(List<Key> keys)
	{
		this.keys = keys;
	}
}
