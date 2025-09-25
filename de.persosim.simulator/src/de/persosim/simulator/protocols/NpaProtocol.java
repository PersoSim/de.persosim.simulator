package de.persosim.simulator.protocols;

import static org.globaltester.logging.BasicLogger.logException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashSet;

import org.globaltester.cryptoprovider.Crypto;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.InfoSource;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.cardobjects.DedicatedFileIdentifier;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.exception.ProcessingException;
import de.persosim.simulator.log.PersoSimLogTags;
import de.persosim.simulator.perso.DefaultPersoGt;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

public class NpaProtocol implements Protocol, Iso7816, InfoSource, TlvConstants {
	@Override
	public String getProtocolName() {
		return "nPA";
	}

	@Override
	public void setCardStateAccessor(CardStateAccessor cardState) {
		//card state not needed by this protocol
	}

	@Override
	public Collection<TlvDataObject> getSecInfos(SecInfoPublicity publicity, MasterFile mf) {
		HashSet<TlvDataObject> secInfos = new HashSet<>();

		//add CardInfoLocator
		ConstructedTlvDataObject cardInfoLocator = new ConstructedTlvDataObject(TAG_SEQUENCE);
		cardInfoLocator.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, Tr03110.id_CI.toByteArray()));
		cardInfoLocator.addTlvDataObject(new PrimitiveTlvDataObject(TAG_IA5_STRING,
		HexString.toByteArray("68 74 74 70 3A 2F 2F 62 73 69 2E 62 75 6E 64 2E 64 65 2F 63 69 66 2F 6E 70 61 2E 78 6D 6C")));
		secInfos.add(cardInfoLocator);

		//add eidSecurityInfos
		if (publicity == SecInfoPublicity.PRIVILEGED) {
			secInfos.add(getEidSecurityInfo(mf));
		}

		return secInfos;
	}

	private TlvDataObject getEidSecurityInfo(MasterFile mf) {

		ConstructedTlvDataObject eidSecInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
		eidSecInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, Tr03110.id_eIDSecurity.toByteArray()));

		//add eIDSecurityObject
		ConstructedTlvDataObject eidSecurityObject = new ConstructedTlvDataObject(TAG_SEQUENCE);
		eidSecInfo.addTlvDataObject(eidSecurityObject);

		//eIDVersionInfo is optional, so skip it for simplicity

		//add hashAlgorithm to eIDSecurityObject
		eidSecurityObject.addTlvDataObject(new ConstructedTlvDataObject(TAG_SEQUENCE,
				new PrimitiveTlvDataObject(TAG_OID, HexString
						.toByteArray("60 86 48 01 65 03 04 02 04"))));
		String hashAlg = "SHA224";

		//add the sequence of hash values
		ConstructedTlvDataObject dgHashes = new ConstructedTlvDataObject(TAG_SEQUENCE);
		eidSecurityObject.addTlvDataObject(dgHashes);

		//add the concrete hashes

		DedicatedFileIdentifier eidAppIdentifier = new DedicatedFileIdentifier(
				HexString.toByteArray(DefaultPersoGt.AID_EID));
		Collection<CardObject> apps = mf.findChildren(eidAppIdentifier);
		DedicatedFile eidApp = (DedicatedFile) apps.iterator().next();

		MessageDigest md;
		try {
			md = MessageDigest.getInstance(hashAlg, Crypto.getCryptoProvider());
			createDgHashes(md, dgHashes, eidApp, null);
		} catch (NoSuchAlgorithmException e) {
			logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
		}

		return eidSecInfo;
	}

	public static void createDgHashes(MessageDigest md, ConstructedTlvDataObject dgHashes, DedicatedFile dedicatedFile, Collection<Integer> dgs) {
		try {


			Collection<CardObject> eidObjects = dedicatedFile.getChildren();
			for (CardObject curObject : eidObjects) {
				if (!(curObject instanceof ElementaryFile)) continue;
				ElementaryFile curFile = (ElementaryFile) curObject;

				//get DG number
				Integer dgNumber = null;
				for (CardObjectIdentifier curIdentifier : curFile.getAllIdentifiers()) {
					if (curIdentifier instanceof FileIdentifier) {
						int fidInteger = ((FileIdentifier) curIdentifier).getFileIdentifier();
						String fidHex = HexString.encode(Utils.removeLeadingZeroBytes(Utils.toUnsignedByteArray(fidInteger)));
						dgNumber = Integer.parseInt(fidHex.substring(fidHex.length()-2), 16);
						break;
					} else if (curIdentifier instanceof ShortFileIdentifier) {
						dgNumber = ((ShortFileIdentifier) curIdentifier).getShortFileIdentifier();
						break;
					}
				}
				if (dgNumber == null || (dgs != null && !dgs.contains(dgNumber))) continue;

				//read fileContent (bypassing access control enforcement)
				byte[] fileContent = null;
				try {
					fileContent = curFile.getContent();
				} catch (AccessDeniedException e1) {
					throw new ProcessingException(SW_6982_SECURITY_STATUS_NOT_SATISFIED, "access denied to "+curFile);
				}
				if (fileContent == null) continue;

				//calculate hash
				md.reset();
				byte[] digest = md.digest(fileContent);

				ConstructedTlvDataObject currentDgHash = new ConstructedTlvDataObject(TAG_SEQUENCE);
				currentDgHash.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, Utils.removeLeadingZeroBytes(Utils.toUnsignedByteArray(dgNumber))));
				currentDgHash.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OCTET_STRING, digest));
				dgHashes.addTlvDataObject(currentDgHash);
			}
		} catch (NullPointerException e) {
			//abort adding DG hashes if not possible
			logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.COMMAND_PROCESSOR_TAG_ID));
		}
	}

	@Override
	public void process(ProcessingData processingData) {
		//nothing to process
	}


	@Override
	public void reset() {
		//nothing to reset
	}

	@Override
	public String getIDString() {
		return "nPA protocol";
	}

	@Override
	public boolean isMoveToStackRequested() {
		return false;
	}

}
