package de.persosim.simulator.perso;

import java.util.Arrays;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import de.persosim.simulator.tlv.Asn1;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.utils.HexString;

/**
 * Default implementation of {@link SecInfoCmsBuilder}.
 * <p/>
 * This builds a simple valid SignedData structure around the provided eContent.
 * All required data is propagated with default values but can be overridden by
 * subclasses.
 * <p/>
 * The default values are choosen to provide a very simple
 * interface/implementation. For example it does not account for CRLs or multiple
 * signers/disgestAlgorithms etc. If this kind of flexibility is needed the
 * interface {@link SecInfoCmsBuilder} needs to be implemented independently.
 * 
 * @author amay
 * 
 */
@XmlRootElement
public class DefaultSecInfoCmsBuilder implements TlvConstants, SecInfoCmsBuilder {

	/* (non-Javadoc)
	 * @see de.persosim.simulator.perso.CmsBuilderInterface#buildSignedData(de.persosim.simulator.tlv.ConstructedTlvDataObject)
	 */
	@Override
	public ConstructedTlvDataObject buildSignedData(
			ConstructedTlvDataObject secInfos) {
		//version defaults to 3 in this implementation
		TlvDataObject version = new PrimitiveTlvDataObject(new TlvTag(Asn1.INTEGER), new byte[]{0x03});
		
		//digestAlgorithms
		ConstructedTlvDataObject digestAlgorithms = new ConstructedTlvDataObject(TAG_SET);
		digestAlgorithms.addAll(getDigestAlgorithms());
		
		//encapContentInfo
		TlvDataObject contentType = new PrimitiveTlvDataObject(HexString.toByteArray("06 08 04 00 7F 00 07 03 02 01"));
		ConstructedTlvDataObject eContent = new ConstructedTlvDataObject(TAG_A0);
		eContent.addTlvDataObject(new PrimitiveTlvDataObject(TAG_OCTET_STRING, secInfos.toByteArray()));
		ConstructedTlvDataObject encapContentInfo = new ConstructedTlvDataObject(TAG_SEQUENCE);
		encapContentInfo.addTlvDataObject(contentType);
		encapContentInfo.addTlvDataObject(eContent);
		
		//certificates
		ConstructedTlvDataObject certificates = new ConstructedTlvDataObject(TAG_A0);
		certificates.addAll(getCertificates());
		
		//signerInfos
		ConstructedTlvDataObject signerInfos = new ConstructedTlvDataObject(TAG_SET);
		certificates.addAll(getSignerInfos());
		
		//signedData
		ConstructedTlvDataObject signedData = new ConstructedTlvDataObject(TAG_SEQUENCE);
		signedData.addTlvDataObject(version);
		signedData.addTlvDataObject(digestAlgorithms);
		signedData.addTlvDataObject(encapContentInfo);
		signedData.addTlvDataObject(certificates);
		signedData.addTlvDataObject(signerInfos);
		
		return signedData;
	}

	/**
	 * Return all used digestAlgorithms
	 * @return
	 */
	protected Collection<? extends TlvDataObject> getDigestAlgorithms() {
		return Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("30 0D 06 09 60 86 48 01 65 03 04 02 04 05 00")));
	}

	/**
	 * Return all used certificates, defaults to a Collection of the single certificate returned by {@link #getCertificate()}
	 * @return
	 */
	protected Collection<? extends TlvDataObject> getCertificates() {
		return Arrays.asList(getCertificate());
	}

	/**
	 * Return the single used certificates
	 * <p/>
	 * If more than one certificate is used override {@link #getCertificates()} and ignore this method
	 * @return
	 */
	protected TlvDataObject getCertificate() {
		return new ConstructedTlvDataObject(HexString.toByteArray("30 82 03 EA 30 82 03 71 A0 03 02 01 02 02 01 19 30 0A 06 08 2A 86 48 CE 3D 04 03 03 30 55 31 0B 30 09 06 03 55 04 06 13 02 44 45 31 0D 30 0B 06 03 55 04 0A 0C 04 62 75 6E 64 31 0C 30 0A 06 03 55 04 0B 0C 03 62 73 69 31 0D 30 0B 06 03 55 04 05 13 04 30 30 30 33 31 1A 30 18 06 03 55 04 03 0C 11 54 45 53 54 20 63 73 63 61 2D 67 65 72 6D 61 6E 79 30 1E 17 0D 31 33 30 39 32 35 31 30 33 37 30 35 5A 17 0D 32 34 30 34 32 35 32 33 35 39 35 39 5A 30 5C 31 0B 30 09 06 03 55 04 06 13 02 44 45 31 0C 30 0A 06 03 55 04 0A 0C 03 42 53 49 31 0D 30 0B 06 03 55 04 05 13 04 30 30 33 37 31 30 30 2E 06 03 55 04 03 0C 27 54 45 53 54 20 44 6F 63 75 6D 65 6E 74 20 53 69 67 6E 65 72 20 49 64 65 6E 74 69 74 79 20 44 6F 63 75 6D 65 6E 74 73 30 82 01 13 30 81 D4 06 07 2A 86 48 CE 3D 02 01 30 81 C8 02 01 01 30 28 06 07 2A 86 48 CE 3D 01 01 02 1D 00 D7 C1 34 AA 26 43 66 86 2A 18 30 25 75 D1 D7 87 B0 9F 07 57 97 DA 89 F5 7E C8 C0 FF 30 3C 04 1C 68 A5 E6 2C A9 CE 6C 1C 29 98 03 A6 C1 53 0B 51 4E 18 2A D8 B0 04 2A 59 CA D2 9F 43 04 1C 25 80 F6 3C CF E4 41 38 87 07 13 B1 A9 23 69 E3 3E 21 35 D2 66 DB B3 72 38 6C 40 0B 04 39 04 0D 90 29 AD 2C 7E 5C F4 34 08 23 B2 A8 7D C6 8C 9E 4C E3 17 4C 1E 6E FD EE 12 C0 7D 58 AA 56 F7 72 C0 72 6F 24 C6 B8 9E 4E CD AC 24 35 4B 9E 99 CA A3 F6 D3 76 14 02 CD 02 1D 00 D7 C1 34 AA 26 43 66 86 2A 18 30 25 75 D0 FB 98 D1 16 BC 4B 6D DE BC A3 A5 A7 93 9F 02 01 01 03 3A 00 04 66 57 46 35 C1 76 5D 1D 49 59 81 E0 8F DF 8E 6C C7 A8 E1 66 04 36 1D 43 4C F6 FB 59 B7 BF 3D 13 55 58 7C 9C 14 2C 39 34 6A 93 AE C4 FE 08 F8 D1 7C 5C D5 82 7E 4C CD 07 A3 82 01 6D 30 82 01 69 30 1F 06 03 55 1D 23 04 18 30 16 80 14 A3 8D B7 C0 DB EC F5 A9 1F CA 6B 3D 5E B2 F3 28 B5 A5 DC 17 30 1D 06 03 55 1D 0E 04 16 04 14 A2 FC 23 A1 ED A6 32 1C 85 9A 77 B3 B8 1D 7B 13 4C BE D4 DC 30 0E 06 03 55 1D 0F 01 01 FF 04 04 03 02 07 80 30 2B 06 03 55 1D 10 04 24 30 22 80 0F 32 30 31 33 30 39 32 35 31 30 33 37 30 35 5A 81 0F 32 30 31 34 30 34 32 35 32 33 35 39 35 39 5A 30 16 06 03 55 1D 20 04 0F 30 0D 30 0B 06 09 04 00 7F 00 07 03 01 01 01 30 2D 06 03 55 1D 11 04 26 30 24 82 12 62 75 6E 64 65 73 64 72 75 63 6B 65 72 65 69 2E 64 65 A4 0E 30 0C 31 0A 30 08 06 03 55 04 07 0C 01 44 30 51 06 03 55 1D 12 04 4A 30 48 81 18 63 73 63 61 2D 67 65 72 6D 61 6E 79 40 62 73 69 2E 62 75 6E 64 2E 64 65 86 1C 68 74 74 70 73 3A 2F 2F 77 77 77 2E 62 73 69 2E 62 75 6E 64 2E 64 65 2F 63 73 63 61 A4 0E 30 0C 31 0A 30 08 06 03 55 04 07 0C 01 44 30 19 06 07 67 81 08 01 01 06 02 04 0E 30 0C 02 01 00 31 07 13 01 41 13 02 49 44 30 35 06 03 55 1D 1F 04 2E 30 2C 30 2A A0 28 A0 26 86 24 68 74 74 70 3A 2F 2F 77 77 77 2E 62 73 69 2E 62 75 6E 64 2E 64 65 2F 74 65 73 74 5F 63 73 63 61 5F 63 72 6C 30 0A 06 08 2A 86 48 CE 3D 04 03 03 03 67 00 30 64 02 30 50 8F B8 DC 3E 8E 6D 91 76 05 2B 03 2E 1D 23 7F 57 6A 92 A9 63 52 98 FC 13 06 75 AC 95 C7 85 8B F4 37 AC BC 45 56 16 DC A0 60 AB 5E 6C B1 2A 66 02 30 6D C4 ED 6A DC 90 D8 AA AF B3 B1 32 ED 4E 88 B5 EC EE 07 E4 1C 90 13 CC 31 92 7E 82 9D A4 A0 1A 84 A9 AA 44 E8 7B CB 5E D7 6C 23 5F 66 20 EE 28"));
	}
	
	/**
	 * Return all used signerInfos, defaults to a Collection of the single signerInfo returned by {@link #getSignerInfo()}
	 * @return
	 */
	protected Collection<? extends TlvDataObject> getSignerInfos() {
		return Arrays.asList(new ConstructedTlvDataObject(HexString.toByteArray("30 82 01 22 02 01 01 30 5A 30 55 31 0B 30 09 06 03 55 04 06 13 02 44 45 31 0D 30 0B 06 03 55 04 0A 0C 04 62 75 6E 64 31 0C 30 0A 06 03 55 04 0B 0C 03 62 73 69 31 0D 30 0B 06 03 55 04 05 13 04 30 30 30 33 31 1A 30 18 06 03 55 04 03 0C 11 54 45 53 54 20 63 73 63 61 2D 67 65 72 6D 61 6E 79 02 01 19 30 0D 06 09 60 86 48 01 65 03 04 02 04 05 00 A0 64 30 17 06 09 2A 86 48 86 F7 0D 01 09 03 31 0A 06 08 04 00 7F 00 07 03 02 01 30 1C 06 09 2A 86 48 86 F7 0D 01 09 05 31 0F 17 0D 31 33 31 30 31 34 30 39 32 32 33 38 5A 30 2B 06 09 2A 86 48 86 F7 0D 01 09 04 31 1E 04 1C B1 1F DC 64 72 5A 6C 13 98 CD B8 13 A8 2B D7 C8 54 22 85 36 FC E6 DA 48 5D 1C CA E5 30 0A 06 08 2A 86 48 CE 3D 04 03 01 04 40 30 3E 02 1D 00 B8 D9 89 5D F2 F7 02 39 0E 81 E9 03 6F F6 15 39 80 FB E8 53 09 D0 B0 ED 89 F2 67 66 02 1D 00 B4 68 A9 7C 15 27 0E 0D 06 E1 FE F0 10 97 0A D0 54 CD 61 28 BF 33 F1 9A 6C 0B 9C CA")));
	}
	
	/**
	 * Return the single used SignerInfo
	 * <p/>
	 * If more than one SignerInfo is used override {@link #getSignerInfos()} and ignore this method
	 * @return
	 */
	protected TlvDataObject getSignerInfo() {
		return new ConstructedTlvDataObject(HexString.toByteArray("30 82 01 22 02 01 01 30 5A 30 55 31 0B 30 09 06 03 55 04 06 13 02 44 45 31 0D 30 0B 06 03 55 04 0A 0C 04 62 75 6E 64 31 0C 30 0A 06 03 55 04 0B 0C 03 62 73 69 31 0D 30 0B 06 03 55 04 05 13 04 30 30 30 33 31 1A 30 18 06 03 55 04 03 0C 11 54 45 53 54 20 63 73 63 61 2D 67 65 72 6D 61 6E 79 02 01 19 30 0D 06 09 60 86 48 01 65 03 04 02 04 05 00 A0 64 30 17 06 09 2A 86 48 86 F7 0D 01 09 03 31 0A 06 08 04 00 7F 00 07 03 02 01 30 1C 06 09 2A 86 48 86 F7 0D 01 09 05 31 0F 17 0D 31 33 31 30 31 34 30 39 32 32 33 38 5A 30 2B 06 09 2A 86 48 86 F7 0D 01 09 04 31 1E 04 1C B1 1F DC 64 72 5A 6C 13 98 CD B8 13 A8 2B D7 C8 54 22 85 36 FC E6 DA 48 5D 1C CA E5 30 0A 06 08 2A 86 48 CE 3D 04 03 01 04 40 30 3E 02 1D 00 B8 D9 89 5D F2 F7 02 39 0E 81 E9 03 6F F6 15 39 80 FB E8 53 09 D0 B0 ED 89 F2 67 66 02 1D 00 B4 68 A9 7C 15 27 0E 0D 06 E1 FE F0 10 97 0A D0 54 CD 61 28 BF 33 F1 9A 6C 0B 9C CA"));
	}
	
}
