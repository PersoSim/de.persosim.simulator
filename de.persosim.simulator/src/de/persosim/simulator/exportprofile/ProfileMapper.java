package de.persosim.simulator.exportprofile;

import java.util.Collection;

import org.globaltester.logging.BasicLogger;

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
import de.persosim.simulator.perso.DefaultPersonalization;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.platform.CommandProcessor;
import de.persosim.simulator.platform.PersonalizationHelper;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.protocols.ri.Ri;
import de.persosim.simulator.protocols.ri.RiOid;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.utils.HexString;

public class ProfileMapper
{
	public Profile mapPersoToExportProfile(Personalization perso)
	{
		OrderedFileList orderedFileList = new OrderedFileList();

		orderedFileList.setContentByFileId(OrderedFileList.FID_EF_DIR, encodeEF(perso, OrderedFileList.FID_EF_DIR, null));
		orderedFileList.setContentByFileId(OrderedFileList.FID_EF_CARD_ACCESS, encodeEF(perso, OrderedFileList.FID_EF_CARD_ACCESS, null));
		orderedFileList.setContentByFileId(OrderedFileList.FID_EF_CARD_SECURITY, encodeEF(perso, OrderedFileList.FID_EF_CARD_SECURITY, null));

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

		OrderedKeyList orderedKeyList = new OrderedKeyList();

		MasterFile masterFile = PersonalizationHelper.getUniqueCompatibleLayer(perso.getLayerList(), CommandProcessor.class).getMasterFile();

		encodeRIKeys(masterFile, orderedKeyList);

		String trustpoint = encodeTrustPoint(masterFile);
		String pin = encodePassword(masterFile, Tr03110.ID_PIN);
		String can = encodePassword(masterFile, Tr03110.ID_CAN);
		String puk = encodePassword(masterFile, Tr03110.ID_PUK);
		boolean pinEnabled = true;
		if (perso instanceof DefaultPersonalization)
			pinEnabled = ((DefaultPersonalization) perso).isPinEnabled();
		Integer pinRetryCounter = encodePinRetryCounter(masterFile, perso);
		Integer pinResetCounter = null;

		return new Profile(orderedFileList.getOrderedFiles(), orderedKeyList.getOrderedKeys(), trustpoint, pin, can, puk, pinEnabled, pinRetryCounter, pinResetCounter);
	}


	private String encodeEF(Personalization perso, String fidAsString, CardObjectIdentifier dfParent)
	{
		String efContent = null;
		try
		{
			byte[] content = PersonalizationHelper.getFileFromPerso(perso, new FileIdentifier(HexString.toByteArray(fidAsString)).getFileIdentifier(), dfParent);
			if (content == null)
			{
				// System.out.println(fidAsString + ": NO_CONTENT");
				return null;
			}
			efContent = HexString.encode(content);
			// System.out.println(fidAsString + ": " + efContent);
		}
		catch (AccessDeniedException e)
		{
			BasicLogger.logException(this.getClass(), e);
		}
		return efContent;
	}

	private void encodeRIKeys(MasterFile masterFile, OrderedKeyList orderedKeyList)
	{
		for (CardObject curCardObject : masterFile.findChildren(new OidIdentifier(new RiOid(Ri.id_RI_ECDH_SHA_256))))
		{
			if (curCardObject instanceof KeyPairObject)
			{
				KeyPairObject keyPairObject = ((KeyPairObject) curCardObject);
				int primaryID = keyPairObject.getPrimaryIdentifier().getInteger();
				if (OrderedKeyList.ID_RI_1_SPERRMERKMAL == primaryID)
				{
					// System.out.println(HexString.encode(keyPairObject.getKeyPair().getPrivate().getEncoded()));
					orderedKeyList.setContentById(OrderedKeyList.ID_RI_1_SPERRMERKMAL, HexString.encode(keyPairObject.getKeyPair().getPrivate().getEncoded()));
				}
				else if (OrderedKeyList.ID_RI_2_PSEUDONYM == primaryID)
				{
					orderedKeyList.setContentById(OrderedKeyList.ID_RI_2_PSEUDONYM, HexString.encode(keyPairObject.getKeyPair().getPrivate().getEncoded()));
				}
			}
		}
	}

	private String encodeTrustPoint(MasterFile masterFile)
	{
		String trustpoint = null;// Profile.CVCA_ROOT_CERT;
		for (CardObject curCardObject : masterFile.findChildren(new TrustPointIdentifier(TerminalType.AT)))
		{
			if (curCardObject instanceof TrustPointCardObject)
			{
				TrustPointCardObject trustPointAt = (TrustPointCardObject) curCardObject;
				trustpoint = HexString.encode(trustPointAt.getCurrentCertificate().getEncoded().toByteArray());
			}
		}
		return trustpoint;
	}

	private String encodePassword(MasterFile masterFile, byte passwordID)
	{
		String password = null;
		AuthObjectIdentifier passwordOID = new AuthObjectIdentifier(passwordID);
		Collection<CardObject> cardObjects = masterFile.findChildren(passwordOID);
		for (CardObject cardObject : cardObjects)
		{
			if (cardObject instanceof PasswordAuthObject)
			{
				PasswordAuthObject passwordObject = (PasswordAuthObject) cardObject;
				password = new String(passwordObject.getPassword());
				break;
			}
		}
		return password;
	}

	private Integer encodePinRetryCounter(MasterFile masterFile, Personalization perso)
	{
		Integer counter = null;
		AuthObjectIdentifier pinOID = new AuthObjectIdentifier(Tr03110.ID_PIN);

		Collection<CardObject> cardObjects = masterFile.findChildren(pinOID);
		for (CardObject cardObject : cardObjects)
		{
			if (cardObject instanceof PasswordAuthObjectWithRetryCounter)
			{
				PasswordAuthObjectWithRetryCounter pinObject = (PasswordAuthObjectWithRetryCounter) cardObject;
				counter = pinObject.getRetryCounterCurrentValue();
				break;
			}
		}
		return counter;
	}
}
