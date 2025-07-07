package de.persosim.simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PersoSimLogTags
{
	public static final String SYSTEM_TAG_ID = "System";
	public static final String APDU_TAG_ID = "APDU";
	public static final String REMOTE_IFD_ID = "Remote IFD";
	public static final String VSMARTCARD_ID = "VSmartCard";

	private static final List<String> TAGS;

	static {
		List<String> tags = new ArrayList<>();
		tags.add(SYSTEM_TAG_ID);
		tags.add(APDU_TAG_ID);
		tags.add(REMOTE_IFD_ID);
		tags.add(VSMARTCARD_ID);
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
