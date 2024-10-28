package de.persosim.simulator.exportprofile;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ //
		"fileId", //
		"shortFileId", //
		"content" //
})
public class File {
	private String fileId;
	private String shortFileId;
	private String content;

	public File() {
		// do nothing; default constructor necessary for JSON (de-)serialization
	}

	public File(String fileId, String shortFileId, String content) {
		this.fileId = fileId;
		this.shortFileId = shortFileId;
		this.content = content;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getShortFileId() {
		return shortFileId;
	}

	public void setShortFileId(String shortFileId) {
		this.shortFileId = shortFileId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
