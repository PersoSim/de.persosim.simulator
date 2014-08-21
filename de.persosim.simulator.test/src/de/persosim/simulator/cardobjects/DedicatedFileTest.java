package de.persosim.simulator.cardobjects;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import mockit.Mocked;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvTag;

public class DedicatedFileTest extends PersoSimTestCase {

	DedicatedFile df;
	byte [] dfName;
	DedicatedFileIdentifier dfIdentifier;
	FileIdentifier fileIdentifier;
	@Mocked
	SecStatus mockedSecurityStatus;
	
	@Before
	public void setUp(){
		dfName = new byte []{1,2,3,4,5};
		dfIdentifier = new DedicatedFileIdentifier(dfName);
		fileIdentifier = new FileIdentifier(1);
		df = new DedicatedFile(fileIdentifier, dfIdentifier);
	}
	
	@Test
	public void testGetFileControlParameterObject(){
		ConstructedTlvDataObject fcp = df.getFileControlParameterDataObject();
		TlvTag dfNameTag = new TlvTag((byte)0x84);
		assertTrue(fcp.containsTlvDataObject(dfNameTag));
		assertArrayEquals(dfName, fcp.getTlvDataObject(dfNameTag).getValueField());
	}
}
