package de.persosim.simulator.perso.export;

import org.globaltester.logging.BasicLogger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Nullable;

public class ProfileBase
{
	@Nullable
	public String serialize(boolean doPrettyPrint)
	{
		String jsonSerialized = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			if (doPrettyPrint) {
				jsonSerialized = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
				// String jsonSerialized = objectMapper.writer(new ProfilePrettyPrinter()).writeValueAsString(profile);
			}
			else {
				jsonSerialized = objectMapper.writeValueAsString(this);
			}
			// BasicLogger.log(jsonSerialized, LogLevel.TRACE);
		}
		catch (JsonProcessingException e) {
			BasicLogger.logException(this.getClass(), e);
		}
		return jsonSerialized;
	}

	@Nullable
	public static ProfileBase deserialize(String jsonSerialized, Class<? extends ProfileBase> clazz)
	{
		ProfileBase profileDeserialized = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			profileDeserialized = objectMapper.readValue(jsonSerialized, clazz);
		}
		catch (JsonProcessingException e) {
			BasicLogger.logException(ProfileBase.class, e);
		}
		return profileDeserialized;
	}
}
