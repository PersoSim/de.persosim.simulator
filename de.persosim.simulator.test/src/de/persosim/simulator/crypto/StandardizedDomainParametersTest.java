package de.persosim.simulator.crypto;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;


public class StandardizedDomainParametersTest extends PersoSimTestCase {

	/**
	 * Positive test case: test for all implemented standardized domain
	 * parameters that their corresponding plain AlgorithmIdentifer is correctly
	 * matched back to standardized domain parameters
	 */
	@Test
	public void testSimplifyAlgorithmIdentifier() {
		for(byte i = 0; i < StandardizedDomainParameters.NO_OF_STANDARDIZED_DOMAIN_PARAMETERS; i++) {
			DomainParameterSet domParams = StandardizedDomainParameters.getDomainParameterSetById(i);
			if (domParams == null) continue;
			
			ConstructedTlvDataObject expectedAlgIdentifier = new ConstructedTlvDataObject(TlvConstants.TAG_SEQUENCE);
			expectedAlgIdentifier.addTlvDataObject(new PrimitiveTlvDataObject(TlvConstants.TAG_OID, StandardizedDomainParameters.OID));
			expectedAlgIdentifier.addTlvDataObject(new PrimitiveTlvDataObject(TlvConstants.TAG_INTEGER, new byte[]{i}));
			
			assertEquals(expectedAlgIdentifier, StandardizedDomainParameters.simplifyAlgorithmIdentifier(domParams.getAlgorithmIdentifier()));
		}
	}
	
}
