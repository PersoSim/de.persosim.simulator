package de.persosim.simulator.adapter.socket.ui.vsmartcard;

import java.util.List;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;

import de.persosim.driver.connector.IfdComm;
import de.persosim.driver.connector.pcsc.PcscListener;
import de.persosim.simulator.adapter.socket.ui.Activator;
import de.persosim.simulator.adapter.socket.ui.handlers.VSmartcardHandler;
import de.persosim.simulator.log.PersoSimLogTags;

public class VSmartcardComm implements IfdComm
{
	@Override
	public void stop()
	{
		BasicLogger.log("VSmartcard Comm stop", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));
		Activator.stopVsmartcard();
	}

	@Override
	public void start()
	{
		BasicLogger.log("VSmartcard Comm start", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));
		Activator.startVsmartcard();
	}

	@Override
	public void setListeners(List<PcscListener> listeners)
	{
		// Do nothing, we do not actually simulate a PCSC reader
	}

	@Override
	public void reset()
	{
		BasicLogger.log("VSmartcard Comm reset", LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));
		Activator.stopVsmartcard();
		Activator.startVsmartcard();
	}

	@Override
	public boolean isRunning()
	{
		boolean isRunning = Activator.isVSmartcardRunning();
		BasicLogger.log("VSmartcard Comm is running: " + isRunning, LogLevel.TRACE, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));
		return isRunning;
	}

	@Override
	public String getUserString()
	{
		return "VSmartcard Adapter";
	}

	@Override
	public String getName()
	{
		return VSmartcardHandler.NAME;
	}
}
