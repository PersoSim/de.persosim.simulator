package de.persosim.simulator.protocols.pace;

import static org.junit.Assert.assertEquals;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collection;

import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.ietf.jgss.GSSException;
import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.apdu.CommandApduFactory;
import de.persosim.simulator.cardobjects.AbstractCardObject;
import de.persosim.simulator.cardobjects.AuthObjectIdentifier;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.DomainParameterSetCardObject;
import de.persosim.simulator.cardobjects.DomainParameterSetIdentifier;
import de.persosim.simulator.cardobjects.MasterFileIdentifier;
import de.persosim.simulator.cardobjects.NullCardObject;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.cardobjects.PasswordAuthObject;
import de.persosim.simulator.cardobjects.Scope;
import de.persosim.simulator.cardobjects.TrustPointCardObject;
import de.persosim.simulator.cardobjects.TrustPointIdentifier;
import de.persosim.simulator.crypto.DomainParameterSet;
import de.persosim.simulator.crypto.StandardizedDomainParameters;
import de.persosim.simulator.crypto.certificates.CardVerifiableCertificate;
import de.persosim.simulator.exception.CarParameterInvalidException;
import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.TR03110Utils;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.utils.HexString;

public class AbstractPaceProtocolTest extends PersoSimTestCase {
	private DefaultPaceProtocol paceProtocol;
	@Mocked
	AbstractCardObject mockedMf;
	@Mocked
	CardStateAccessor mockedCardStateAccessor;
	PasswordAuthObject passwordAuthObject;
	ConstructedTlvDataObject cvcaIsTlv;
	TlvDataObject cvcaIsCarTlv;
	DomainParameterSet domainParameterSet13;
	Collection<CardObject> domainParameterSet13Collection;
	DomainParameterSetCardObject domainParameters13;
	OidIdentifier oidIdentifier;
	
	/**
	 * Create the test environment.
	 * 
	 * @throws ReflectiveOperationException
	 */
	@Before
	public void setUp() throws ReflectiveOperationException {
		passwordAuthObject = new PasswordAuthObject(new AuthObjectIdentifier(1), new byte [] {1,2,3,4});
		
		byte [] cvcaIsData = HexString.toByteArray("7F218201B07F4E8201685F290100420D444549534356434130303030317F4982011D060A04007F000702020202038120A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E537782207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9832026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B68441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F0469978520A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A78641045889BF5306189ABB7FA3AD0E922443F9C60162E8215053B72812663E5D798EE05097C4DFAC7470701A5B644AAEAFE1E50BA1D0ED5769151EC476C154BB4A56848701015F200D444549534356434130303030317F4C0E060904007F0007030102015301E35F25060104000500055F24060105000500055F37400A589134205376E20EFF49E108560F1CB47C7D221E96E51FF3C6F4EAF1F6CCC000A5E34ED8E3F6E05253DA09B0D68FF5DFB5BD586782B987453C655FBEE8EC59");
		
		cvcaIsTlv = (ConstructedTlvDataObject) ((ConstructedTlvDataObject)new TlvDataObjectContainer(cvcaIsData).getTlvDataObject(TR03110Utils.TAG_7F21)).getTlvDataObject(TR03110Utils.TAG_7F4E);
		cvcaIsCarTlv = ((ConstructedTlvDataObject)((ConstructedTlvDataObject)new TlvDataObjectContainer(cvcaIsData).getTlvDataObject(TR03110Utils.TAG_7F21)).getTlvDataObject(TR03110Utils.TAG_7F4E)).getTlvDataObject(TR03110Utils.TAG_42);
		
		// create and init the object under test
		paceProtocol = new DefaultPaceProtocol();
		paceProtocol.setCardStateAccessor(mockedCardStateAccessor);
		paceProtocol.init();
		
		oidIdentifier = new OidIdentifier(Pace.OID_id_PACE_ECDH_GM_AES_CBC_CMAC_192);
		
		domainParameterSet13 = StandardizedDomainParameters.getDomainParameterSetById(13);
		domainParameters13 = new DomainParameterSetCardObject(domainParameterSet13, new DomainParameterSetIdentifier(13));
		domainParameters13.addOidIdentifier(oidIdentifier);
		domainParameterSet13Collection = new ArrayList<CardObject>();
		domainParameterSet13Collection.add(domainParameters13);
	}
	
	@Test
	public void testSetAtNoChat(){
		// prepare the mock
		new Expectations() {
			{
				mockedCardStateAccessor.getObject(withInstanceOf(AuthObjectIdentifier.class),null);
				result = passwordAuthObject;

				mockedCardStateAccessor.getObject(
						withInstanceOf(MasterFileIdentifier.class),
						withInstanceOf(Scope.class));
				result = mockedMf;
			}
		};
		
		new Expectations() {
			{
				mockedMf.findChildren(
						withInstanceOf(DomainParameterSetIdentifier.class),
						withInstanceOf(OidIdentifier.class));
				result = domainParameters13;
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 C1 A4 0F 80 0A 04 00 7F 00 07 02 02 04 02 03 83 01 02");
		processingData.updateCommandApdu(this, "setAT APDU",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		paceProtocol.process(processingData);

		// check results
		assertEquals("Statusword is not 9000", Iso7816.SW_9000_NO_ERROR, processingData.getResponseApdu()
				.getStatusWord());
	}
	
	@Test
	public void testSetAtMissingTrustPoint(){
		// prepare the mock
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getObject(withInstanceOf(AuthObjectIdentifier.class),null);
				result = passwordAuthObject;
				
				mockedCardStateAccessor.getObject(
						withInstanceOf(MasterFileIdentifier.class),
						withInstanceOf(Scope.class));
				result = mockedMf;
				
				mockedMf.findChildren(
						withInstanceOf(OidIdentifier.class),
						withInstanceOf(DomainParameterSetIdentifier.class));
				result = domainParameters13;
				
				mockedCardStateAccessor.getObject(
						withInstanceOf(DomainParameterSetIdentifier.class),
						withInstanceOf(Scope.class));
				result = domainParameters13;
				
				mockedCardStateAccessor.getObject(withInstanceOf(TrustPointIdentifier.class),null);
				result = new NullCardObject();
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 C1 A4 23 80 0A 04 00 7F 00 07 02 02 04 02 03 83 01 02 7F 4C 0E 06 09 04 00 7F 00 07 03 01 02 01 53 01 23 84 01 0D");
		processingData.updateCommandApdu(this, "setAT APDU with chat",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		paceProtocol.process(processingData);

		// check results
		assertEquals("Statusword is not 6A88", Iso7816.SW_6A88_REFERENCE_DATA_NOT_FOUND, processingData.getResponseApdu()
				.getStatusWord());
	}
	
	@Test
	public void testSetAtWithChat() throws CarParameterInvalidException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, GSSException, CertificateNotParseableException{
		// prepare the mock
		final TrustPointCardObject trustpoint = new TrustPointCardObject(new TrustPointIdentifier(TerminalType.IS), new CardVerifiableCertificate(cvcaIsTlv)); 
		
		new NonStrictExpectations() {
			{
				mockedCardStateAccessor.getObject(withInstanceOf(AuthObjectIdentifier.class),null);
				result = passwordAuthObject;
				
				mockedCardStateAccessor.getObject(
						withInstanceOf(MasterFileIdentifier.class),
						withInstanceOf(Scope.class));
				result = mockedMf;
				
				mockedMf.findChildren(
						withInstanceOf(DomainParameterSetIdentifier.class),
						withInstanceOf(OidIdentifier.class));
				result = domainParameters13;
				
				mockedCardStateAccessor.getObject(
						withInstanceOf(DomainParameterSetIdentifier.class),
						withInstanceOf(Scope.class));
				result = domainParameters13;

				mockedCardStateAccessor.getObject(withInstanceOf(TrustPointIdentifier.class),null);
				result = trustpoint;
			}
		};
		
		// select Apdu
		ProcessingData processingData = new ProcessingData();
		byte[] apduBytes = HexString.toByteArray("00 22 C1 A4 23 80 0A 04 00 7F 00 07 02 02 04 02 03 83 01 02 7F 4C 0E 06 09 04 00 7F 00 07 03 01 02 01 53 01 23 84 01 0D");
		processingData.updateCommandApdu(this, "setAT APDU with chat",
				CommandApduFactory.createCommandApdu(apduBytes));

		// call mut
		paceProtocol.process(processingData);

		// check results
		assertEquals("Statusword is not 9000", Iso7816.SW_9000_NO_ERROR, processingData.getResponseApdu()
				.getStatusWord());
	}
}
