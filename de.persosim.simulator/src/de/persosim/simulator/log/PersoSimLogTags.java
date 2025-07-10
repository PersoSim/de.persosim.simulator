package de.persosim.simulator.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PersoSimLogTags
{
	public static final String SYSTEM_TAG_ID = "System";
	public static final String APDU_TAG_ID = "APDU";
	public static final String PERSO_TAG_ID = "Perso";
	public static final String VIRTUAL_DRIVER_TAG_ID = "Virtual Driver";
	public static final String VSMARTCARD_TAG_ID = "VSmartCard";
	public static final String REMOTE_IFD_TAG_ID = "Remote IFD";
	public static final String COMMAND_PROCESSOR_TAG_ID = "Cmd Processor";
	public static final String PROTOCOL_TAG_ID = "Protocol";

	private static final List<String> TAGS;

	static {
		List<String> tags = new ArrayList<>();
		tags.add(SYSTEM_TAG_ID);
		tags.add(APDU_TAG_ID);
		tags.add(PERSO_TAG_ID);
		tags.add(VIRTUAL_DRIVER_TAG_ID);
		tags.add(VSMARTCARD_TAG_ID);
		tags.add(REMOTE_IFD_TAG_ID);
		tags.add(COMMAND_PROCESSOR_TAG_ID);
		tags.add(PROTOCOL_TAG_ID);
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
