package de.persosim.simulator.exportprofile;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ //
		"id", //
		"content" //
})
public class Key {
	int id;
	String content;

	public Key() {
		// do nothing; default constructor necessary for JSON (de-)serialization
	}

	public Key(int id, String content) {
		this.id = id;
		this.content = content;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
