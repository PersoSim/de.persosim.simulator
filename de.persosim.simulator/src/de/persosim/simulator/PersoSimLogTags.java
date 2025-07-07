package de.persosim.simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PersoSimLogTags
{
	public static final String SYSTEM_TAG_ID = "SYSTEM";
	public static final String APDU_TAG_ID = "APDU";

	private static final List<String> TAGS;

	static {
		List<String> tags = new ArrayList<>();
		tags.add(SYSTEM_TAG_ID);
		tags.add(APDU_TAG_ID);
		TAGS = Collections.unmodifiableList(tags);
	}

	private PersoSimLogTags()
	{
		// hide implicit public constructor
	}

	public static List<String> getAllTags()
	{
		return TAGS;
	}

	public static boolean isKnownTag(String tag)
	{
		return TAGS.contains(tag);
	}
}
