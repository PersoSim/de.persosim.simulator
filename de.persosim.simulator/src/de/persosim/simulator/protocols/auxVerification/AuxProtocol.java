package de.persosim.simulator.protocols.auxVerification;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import org.globaltester.logging.BasicLogger;

import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.cardobjects.AuxDataObject;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.exception.VerificationException;
import de.persosim.simulator.platform.Iso7816;
import de.persosim.simulator.platform.PlatformUtil;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.ca.ChipAuthenticationMechanism;
import de.persosim.simulator.protocols.ta.AuthenticatedAuxiliaryData;
import de.persosim.simulator.protocols.ta.TerminalAuthenticationMechanism;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObjectContainer;

public class AuxProtocol extends AbstractProtocol implements Iso7816, TlvConstants {
	@Override
	public void process(ProcessingData processingData) {
		if (((processingData.getCommandApdu().getCla() == (byte) 0x80) && (processingData.getCommandApdu().getIns() == INS_20_VERIFY)) || (processingData.getCommandApdu().getIns() == INS_33_COMPARE)){
			//check for ca
			HashSet<Class<? extends SecMechanism>> previousMechanisms = new HashSet<>();
			previousMechanisms.add(ChipAuthenticationMechanism.class);
			Collection<SecMechanism> currentMechanisms = cardState.getCurrentMechanisms(SecContext.APPLICATION, previousMechanisms);
			if (currentMechanisms.isEmpty()){
				ResponseApdu resp = new ResponseApdu(PlatformUtil.SW_4982_SECURITY_STATUS_NOT_SATISFIED);
				processingData.updateResponseAPDU(this, "The AUX protocol can not be executed without a previous CA", resp);
				/* there is nothing more to be done here */
				return;
			}
			
			ResponseApdu resp;
			String msg;
			TlvDataObjectContainer commandData = processingData.getCommandApdu().getCommandDataObjectContainer();
			if (commandData.containsTlvDataObject(TlvConstants.TAG_06)){
				try{
					Oid oid = new GenericOid(commandData.getTlvDataObject(TlvConstants.TAG_06).getValueField());
					processOid(oid);

					msg = "Auxiliary data verification successful";
					resp = new ResponseApdu(Iso7816.SW_9000_NO_ERROR);
				} catch (IllegalArgumentException e){
					msg = "The given OID is not valid";
					resp = new ResponseApdu(PlatformUtil.SW_4A80_WRONG_DATA);
					BasicLogger.logException(this, msg, e);
				} catch (FileNotFoundException e) {
					msg = "The referenced data could not be found";
					resp = new ResponseApdu(PlatformUtil.SW_4A88_REFERENCE_DATA_NOT_FOUND);
					BasicLogger.logException(this, msg, e);
				} catch (VerificationException e) {
					msg = "Auxiliary data verification failed";
					resp = new ResponseApdu(SW_6FFF_IMPLEMENTATION_ERROR);
					if (processingData.getCommandApdu().getIns() == INS_20_VERIFY) {
						resp = new ResponseApdu(SW_6300_AUTHENTICATION_FAILED);
					} else if (processingData.getCommandApdu().getIns() == INS_33_COMPARE) {
						resp = new ResponseApdu(SW_6340_COMPARISON_FAILED);
					}
					BasicLogger.logException(this, msg, e);
				} catch (AccessDeniedException e) {
					msg = "Auxiliary data verification failed - Access to data not allowed";
					resp = new ResponseApdu(SW_6982_SECURITY_STATUS_NOT_SATISFIED);
					BasicLogger.logException(this, msg, e);
				}
			} else {
				msg = "Missing an OID";
				resp = new ResponseApdu(PlatformUtil.SW_4A80_WRONG_DATA);
			}
			processingData.updateResponseAPDU(this, msg, resp);
		}
	}

	private void processOid(Oid oid) throws VerificationException, FileNotFoundException, AccessDeniedException {
		
		AuthenticatedAuxiliaryData expectedAuxData = getExpectedAuxDataFromTa(oid);
		AuxDataObject auxDataObject = getAuxDataObjectForOid(oid);

		if (!auxDataObject.verify(expectedAuxData)){
			throw new VerificationException("Verification of auxiliary data failed!");				
		}
		
	}

	private AuthenticatedAuxiliaryData getExpectedAuxDataFromTa(Oid oid) throws FileNotFoundException {
		Collection<Class<? extends SecMechanism>> previousMechanisms = new HashSet<>();
		previousMechanisms.add(TerminalAuthenticationMechanism.class);
		Collection<SecMechanism> currentMechanisms = cardState.getCurrentMechanisms(SecContext.APPLICATION, previousMechanisms);
		if (currentMechanisms.isEmpty()) throw new FileNotFoundException("No TA mechanism available");
		
		TerminalAuthenticationMechanism taMechanism = (TerminalAuthenticationMechanism) currentMechanisms.toArray()[0];
		List<AuthenticatedAuxiliaryData> auxDataFromTa = taMechanism.getAuxiliaryData();
		if ((auxDataFromTa == null)||(auxDataFromTa.isEmpty())) throw new FileNotFoundException("No auxiliary data was stored during TA");
		
		ListIterator<AuthenticatedAuxiliaryData> auxDataIterator = auxDataFromTa.listIterator(auxDataFromTa.size());
		
		while(auxDataIterator.hasPrevious()) {
			AuthenticatedAuxiliaryData curAuthAuxData = auxDataIterator.previous();
			if(oid.equals(curAuthAuxData.getObjectIdentifier())) {
				return curAuthAuxData;
			}			
		}
		
		throw new FileNotFoundException("No auxiliary data was stored during TA matching the provided OID");
	}

	private AuxDataObject getAuxDataObjectForOid(Oid oid) throws FileNotFoundException {
		Collection<CardObject> candidates = cardState.getMasterFile().findChildren(new OidIdentifier(oid));
		
		for (CardObject auxDataCandidate : candidates) {
			if (auxDataCandidate instanceof AuxDataObject){
				return (AuxDataObject) auxDataCandidate;
			}
		}
		
		throw new FileNotFoundException("The card object using the OID " + oid.toString() + " is not a AUX data object");
	}

}
