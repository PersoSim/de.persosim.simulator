package de.persosim.simulator.control.soap.service;

import java.net.URL;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;

import de.persosim.simulator.log.PersoSimLogTags;


public class PersoSimRemoteControlClient
{
	public static void main(String[] args)
	{
		new PersoSimRemoteControlClient();
		BasicLogger.log("PersoSimRemoteControl handler is reachable.", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));

		callReset(null);

		String filePathPerso = "e:\\projects\\globaltester\\dev\\env\\master\\repos\\de.persosim.simulator\\de.persosim.simulator\\personalization\\profiles\\Profile02.perso";
		callLoadPerso(null, filePathPerso);
		filePathPerso = "c:\\_downloads\\GlobalTester\\profiles\\ProfileOA04.perso";
		callLoadPerso(null, filePathPerso);
		filePathPerso = "c:/_downloads/GlobalTester/profiles/ProfileOA04.perso";
		callLoadPerso(null, filePathPerso);
		filePathPerso = "c:\\_downloads\\GlobalTester\\profiles\\NotExist.perso";
		callLoadPerso(null, filePathPerso);

		String apduAsHexString = "0CB000000D9701018E0803689686FA90971500";
		callSendApdu(null, apduAsHexString);
		apduAsHexString = "no_apdu";
		callSendApdu(null, apduAsHexString);
	}

	PersoSimRemoteControl port;

	public PersoSimRemoteControlClient()
	{
		PersoSimRemoteControlService service = new PersoSimRemoteControlService();
		port = service.getPersoSimRemoteControlPort();
	}

	public PersoSimRemoteControlClient(URL serviceUrl)
	{
		PersoSimRemoteControlService service = new PersoSimRemoteControlService(serviceUrl);
		port = service.getPersoSimRemoteControlPort();
	}

	public static String getPersoSimRemoteControlResult(PersoSimRemoteControlResult result)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(result.getResultCode()).append('\n').append(result.getResultMessage()).append('\n').append(result.getResultAsHex()).append('\n').append(result.getResultPrettyPrint()).append('\n');
		return sb.toString();
	}


	public static void callReset(URL serviceUrl)
	{
		BasicLogger.log("Try calling reset from PersoSimRemoteControl...", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		PersoSimRemoteControlService service = serviceUrl == null ? new PersoSimRemoteControlService() : new PersoSimRemoteControlService(serviceUrl);
		PersoSimRemoteControl port = service.getPersoSimRemoteControlPort();
		PersoSimRemoteControlResult result = port.reset();
		BasicLogger.log("Response: '" + getPersoSimRemoteControlResult(result) + "'", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
	}

	public static void callLoadPerso(URL serviceUrl, String filePathPerso)
	{
		BasicLogger.log("Try calling loadPerso from PersoSimRemoteControl...", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		PersoSimRemoteControlService service = serviceUrl == null ? new PersoSimRemoteControlService() : new PersoSimRemoteControlService(serviceUrl);
		PersoSimRemoteControl port = service.getPersoSimRemoteControlPort();
		PersoSimRemoteControlResult result = port.loadPerso(filePathPerso);
		BasicLogger.log("Response: '" + getPersoSimRemoteControlResult(result) + "'", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
	}

	public static void callSendApdu(URL serviceUrl, String apduAsHexString)
	{
		BasicLogger.log("Try calling sendApdu from PersoSimRemoteControl...", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		PersoSimRemoteControlService service = serviceUrl == null ? new PersoSimRemoteControlService() : new PersoSimRemoteControlService(serviceUrl);
		PersoSimRemoteControl port = service.getPersoSimRemoteControlPort();
		PersoSimRemoteControlResult result = port.sendApdu(apduAsHexString);
		BasicLogger.log("Response: '" + getPersoSimRemoteControlResult(result) + "'", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
	}

}
