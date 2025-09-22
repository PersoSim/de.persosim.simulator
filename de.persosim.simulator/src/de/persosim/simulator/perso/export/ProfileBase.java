package de.persosim.simulator.perso.export;

import static org.globaltester.logging.BasicLogger.logException;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.persosim.simulator.log.PersoSimLogTags;
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
			logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
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
			logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		}
		return profileDeserialized;
	}
}
