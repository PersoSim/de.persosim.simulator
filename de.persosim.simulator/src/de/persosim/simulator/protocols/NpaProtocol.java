package de.persosim.simulator.protocols;

import static de.persosim.simulator.utils.PersoSimLogger.logException;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashSet;

import org.globaltester.cryptoprovider.Crypto;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.cardobjects.DedicatedFileIdentifier;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.perso.DefaultPersoGt;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.InfoSource;
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
		HashSet<TlvDataObject> secInfos = new HashSet<TlvDataObject>();
		
		//add CardInfoLocator
		ConstructedTlvDataObject cardInfoLocator = new ConstructedTlvDataObject(TAG_SEQUENCE);
		cardInfoLocator.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, Tr03110.id_CI));
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
		eidSecInfo.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OID, Tr03110.id_eIDSecurity));
		
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
		try {
			MessageDigest md = MessageDigest.getInstance(hashAlg, Crypto.getCryptoProvider());
			
			DedicatedFileIdentifier eidAppIdentifier = new DedicatedFileIdentifier(
					HexString.toByteArray(DefaultPersoGt.AID_EID));
			Collection<CardObject> apps = mf.findChildren(eidAppIdentifier);
			DedicatedFile eidApp = (DedicatedFile) apps.iterator().next();
			
			Collection<CardObject> eidObjects = eidApp.getChildren();
			for (CardObject curObject : eidObjects) {
				if (!(curObject instanceof ElementaryFile)) continue;
				ElementaryFile curFile = (ElementaryFile) curObject;	
				
				//get DG number
				byte[] dgNumber = null;
				for (CardObjectIdentifier curIdentifier : curFile.getAllIdentifiers()) {
					if (curIdentifier instanceof ShortFileIdentifier) {
						int sfid = ((ShortFileIdentifier) curIdentifier).getShortFileIdentifier();
						dgNumber = Utils.removeLeadingZeroBytes(Utils.toUnsignedByteArray(sfid));
						break;
					}
				}
				if (dgNumber == null) continue;
				
				//read fileContent (bypassing access control enforcement)
				//XXX do not bypass access control enforcement but use the card life cycle for accessing this data during personalization
				byte[] fileContent = null;
				try {
					Class<ElementaryFile>  aClass = ElementaryFile.class;
					Field field = aClass.getDeclaredField("content");
					field.setAccessible(true);
					fileContent = (byte[]) field.get(curFile);
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
					logException(getClass(), e);
				}
				if (fileContent == null) continue;
				
				//calculate hash
				md.reset();
				byte[] digest = md.digest(fileContent);
				
				ConstructedTlvDataObject currentDgHash = new ConstructedTlvDataObject(TAG_SEQUENCE);
				currentDgHash.addTlvDataObject(new PrimitiveTlvDataObject(TAG_INTEGER, dgNumber));
				currentDgHash.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OCTET_STRING, digest));
				dgHashes.addTlvDataObject(currentDgHash);
			}
		} catch (NoSuchAlgorithmException | NullPointerException e) {
			//abort adding DG hashes if not possible
			logException(getClass(), e);
		}
		
		return eidSecInfo;
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
