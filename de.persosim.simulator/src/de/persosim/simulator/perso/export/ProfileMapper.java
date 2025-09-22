package de.persosim.simulator.perso.export;

import static org.globaltester.logging.BasicLogger.logException;

import java.util.Collection;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;

import de.persosim.simulator.cardobjects.AuthObjectIdentifier;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.DedicatedFileIdentifier;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.KeyPairObject;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.cardobjects.PasswordAuthObject;
import de.persosim.simulator.cardobjects.PasswordAuthObjectWithRetryCounter;
import de.persosim.simulator.cardobjects.TrustPointCardObject;
import de.persosim.simulator.cardobjects.TrustPointIdentifier;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.log.PersoSimLogTags;
import de.persosim.simulator.perso.DefaultPersonalization;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.platform.CommandProcessor;
import de.persosim.simulator.platform.PersonalizationHelper;
import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.protocols.ca.Ca;
import de.persosim.simulator.protocols.ri.Ri;
import de.persosim.simulator.protocols.ri.RiOid;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.utils.HexString;
import jakarta.annotation.Nullable;

public class ProfileMapper
{
	public Profile mapPersoToExportProfile(Personalization perso)
	{
		OrderedFileList orderedFileList = new OrderedFileList();

		orderedFileList.setContentByFileId(OrderedFileList.FID_EF_DIR, encodeEF(perso, OrderedFileList.FID_EF_DIR, null));
		orderedFileList.setContentByFileId(OrderedFileList.FID_EF_CARD_ACCESS, encodeEF(perso, OrderedFileList.FID_EF_CARD_ACCESS, null));
		orderedFileList.setContentByFileId(OrderedFileList.FID_EF_CARD_SECURITY, encodeEF(perso, OrderedFileList.FID_EF_CARD_SECURITY, null));
		orderedFileList.setContentByFileId(OrderedFileList.FID_EF_CHIP_SECURITY, encodeEF(perso, OrderedFileList.FID_EF_CHIP_SECURITY, null));

		DedicatedFileIdentifier dfApplEID = new DedicatedFileIdentifier(HexString.toByteArray(DefaultPersonalization.AID_EID));
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG1, encodeEF(perso, OrderedFileList.FID_DG1, dfApplEID));
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG2, encodeEF(perso, OrderedFileList.FID_DG2, dfApplEID));
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG3, encodeEF(perso, OrderedFileList.FID_DG3, dfApplEID));
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG4, encodeEF(perso, OrderedFileList.FID_DG4, dfApplEID));
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG5, encodeEF(perso, OrderedFileList.FID_DG5, dfApplEID));
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG6, encodeEF(perso, OrderedFileList.FID_DG6, dfApplEID));
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG7, encodeEF(perso, OrderedFileList.FID_DG7, dfApplEID));
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG8, encodeEF(perso, OrderedFileList.FID_DG8, dfApplEID));
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG9, encodeEF(perso, OrderedFileList.FID_DG9, dfApplEID));
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG10, encodeEF(perso, OrderedFileList.FID_DG10, dfApplEID));
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG11, encodeEF(perso, OrderedFileList.FID_DG11, dfApplEID));
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG12, encodeEF(perso, OrderedFileList.FID_DG12, dfApplEID));
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG13, encodeEF(perso, OrderedFileList.FID_DG13, dfApplEID));
		// orderedFileList.setContentByFileId(OrderedFileList.FID_DG14,
		// encodeEF(perso, OrderedFileList.FID_DG14, dfApplEID));
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG15, encodeEF(perso, OrderedFileList.FID_DG15, dfApplEID));
		// orderedFileList.setContentByFileId(OrderedFileList.FID_DG16, encodeEF(perso, OrderedFileList.FID_DG16, dfApplEID));
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG17, encodeEF(perso, OrderedFileList.FID_DG17, dfApplEID));
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG18, encodeEF(perso, OrderedFileList.FID_DG18, dfApplEID));
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG19, encodeEF(perso, OrderedFileList.FID_DG19, dfApplEID));
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG20, encodeEF(perso, OrderedFileList.FID_DG20, dfApplEID));
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG21, encodeEF(perso, OrderedFileList.FID_DG21, dfApplEID));
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG22, encodeEF(perso, OrderedFileList.FID_DG22, dfApplEID));

		MasterFile masterFile = PersonalizationHelper.getUniqueCompatibleLayer(perso.getLayerList(), CommandProcessor.class).getMasterFile();

		OrderedKeyList orderedKeyList = new OrderedKeyList(false);
		encodeRIKeys(masterFile, orderedKeyList);
		encodeCAKey41(masterFile, orderedKeyList);
		encodeCAKey45(masterFile, orderedKeyList);

		String trustpoint = encodeTrustPoint(masterFile);
		String pin = encodePassword(masterFile, Tr03110.ID_PIN);
		String can = encodePassword(masterFile, Tr03110.ID_CAN);
		String puk = encodePassword(masterFile, Tr03110.ID_PUK);
		boolean pinEnabled = true;
		if (perso instanceof DefaultPersonalization defaultPerso)
			pinEnabled = defaultPerso.isPinEnabled();
		Integer pinRetryCounter = encodePinRetryCounter(masterFile, perso);

		return new Profile(orderedFileList.getOrderedFiles(), orderedKeyList.getOrderedKeys(), trustpoint, pin, can, puk, pinEnabled, pinRetryCounter);
	}


	public OverlayProfile mapPersoToOverlayProfile(Personalization perso)
	{
		MasterFile masterFile = PersonalizationHelper.getUniqueCompatibleLayer(perso.getLayerList(), CommandProcessor.class).getMasterFile();

		OrderedKeyList orderedKeyList = new OrderedKeyList(true);
		encodeRIKeys(masterFile, orderedKeyList);

		return new OverlayProfile(orderedKeyList.getOrderedKeys());
	}


	@Nullable
	private String encodeEF(Personalization perso, String fidAsString, CardObjectIdentifier dfParent)
	{
		String efContent = null;
		try {
			byte[] content = PersonalizationHelper.getFileFromPerso(perso, new FileIdentifier(HexString.toByteArray(fidAsString)).getFileIdentifier(), dfParent);
			if (content == null) {
				// BasicLogger.log(fidAsString + ": NO_CONTENT", LogLevel.TRACE);
				return null;
			}
			efContent = HexString.encode(content);
			// BasicLogger.log(fidAsString + ": " + efContent, LogLevel.TRACE);
		}
		catch (AccessDeniedException e) {
			logException(e.getMessage(), e, LogLevel.ERROR, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.PERSO_TAG_ID));
		}
		return efContent;
	}


	private void encodeRIKeys(MasterFile masterFile, OrderedKeyList orderedKeyList)
	{
		KeyPairObject foundKeyPairObject = ProfileHelper.findKeyPairObjectExt(masterFile, new OidIdentifier(new RiOid(Ri.id_RI_ECDH_SHA_256)), Boolean.FALSE,
				Integer.valueOf(OrderedKeyList.ID_RI_1_SPERRMERKMAL));
		if (foundKeyPairObject != null) {
			orderedKeyList.setContent((GenericOid) new OidIdentifier(new RiOid(Ri.id_RI_ECDH_SHA_256)).getOid(), Boolean.FALSE, foundKeyPairObject.getPrimaryIdentifier().getInteger(),
					HexString.encode(foundKeyPairObject.getKeyPair().getPrivate().getEncoded()));
			// BasicLogger.log(HexString.encode(foundKeyPairObject.getKeyPair().getPrivate().getEncoded()), LogLevel.TRACE);
		}

		foundKeyPairObject = ProfileHelper.findKeyPairObjectExt(masterFile, new OidIdentifier(new RiOid(Ri.id_RI_ECDH_SHA_256)), Boolean.TRUE, Integer.valueOf(OrderedKeyList.ID_RI_2_PSEUDONYM));
		if (foundKeyPairObject != null) {
			orderedKeyList.setContent((GenericOid) new OidIdentifier(new RiOid(Ri.id_RI_ECDH_SHA_256)).getOid(), Boolean.TRUE, foundKeyPairObject.getPrimaryIdentifier().getInteger(),
					HexString.encode(foundKeyPairObject.getKeyPair().getPrivate().getEncoded()));
			// BasicLogger.log(HexString.encode(foundKeyPairObject.getKeyPair().getPrivate().getEncoded()), LogLevel.TRACE);
		}
	}

	private void encodeCAKey41(MasterFile masterFile, OrderedKeyList orderedKeyList)
	{
		KeyPairObject foundKeyPairObject = ProfileHelper.findKeyPairObjectExt(masterFile, Ca.OID_IDENTIFIER_id_CA_ECDH_AES_CBC_CMAC_128, Boolean.FALSE, Integer.valueOf(OrderedKeyList.ID_CA_41));
		if (foundKeyPairObject != null) {
			orderedKeyList.setContent((GenericOid) Ca.OID_IDENTIFIER_id_CA_ECDH_AES_CBC_CMAC_128.getOid(), Boolean.FALSE, foundKeyPairObject.getPrimaryIdentifier().getInteger(),
					HexString.encode(foundKeyPairObject.getKeyPair().getPrivate().getEncoded()));
			// BasicLogger.log(HexString.encode(foundKeyPairObject.getKeyPair().getPrivate().getEncoded()), LogLevel.TRACE);
		}
	}

	private void encodeCAKey45(MasterFile masterFile, OrderedKeyList orderedKeyList)
	{
		KeyPairObject foundKeyPairObject = ProfileHelper.findKeyPairObjectExt(masterFile, Ca.OID_IDENTIFIER_id_CA_ECDH_AES_CBC_CMAC_128, Boolean.TRUE, Integer.valueOf(OrderedKeyList.ID_CA_45));
		if (foundKeyPairObject != null) {
			orderedKeyList.setContent((GenericOid) Ca.OID_IDENTIFIER_id_CA_ECDH_AES_CBC_CMAC_128.getOid(), Boolean.TRUE, foundKeyPairObject.getPrimaryIdentifier().getInteger(),
					HexString.encode(foundKeyPairObject.getKeyPair().getPrivate().getEncoded()));
			// BasicLogger.log(HexString.encode(foundKeyPairObject.getKeyPair().getPrivate().getEncoded()), LogLevel.TRACE);
		}
	}

	@Nullable
	private String encodeTrustPoint(MasterFile masterFile)
	{
		String trustpoint = null;// Profile.CVCA_ROOT_CERT;
		for (CardObject curCardObject : masterFile.findChildren(new TrustPointIdentifier(TerminalType.AT))) {
			if (curCardObject instanceof TrustPointCardObject trustPointAt) {
				trustpoint = HexString.encode(trustPointAt.getCurrentCertificate().getEncoded().toByteArray());
				break;
			}
		}
		return trustpoint;
	}

	@Nullable
	private String encodePassword(MasterFile masterFile, byte passwordID)
	{
		String password = null;
		AuthObjectIdentifier passwordOID = new AuthObjectIdentifier(passwordID);
		Collection<CardObject> cardObjects = masterFile.findChildren(passwordOID);
		for (CardObject cardObject : cardObjects) {
			if (cardObject instanceof PasswordAuthObject passwordObject) {
				password = new String(passwordObject.getPassword());
				break;
			}
		}
		return password;
	}

	@Nullable
	private Integer encodePinRetryCounter(MasterFile masterFile, Personalization perso)
	{
		Integer counter = null;
		AuthObjectIdentifier pinOID = new AuthObjectIdentifier(Tr03110.ID_PIN);

		Collection<CardObject> cardObjects = masterFile.findChildren(pinOID);
		for (CardObject cardObject : cardObjects) {
			if (cardObject instanceof PasswordAuthObjectWithRetryCounter pinObject) {
				counter = pinObject.getRetryCounterCurrentValue();
				break;
			}
		}
		return counter;
	}
}
