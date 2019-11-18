package de.persosim.simulator.cardobjects;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.seccondition.SecCondition;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvTag;

public class DedicatedFileTest extends PersoSimTestCase {

	byte [] dfName;
	DedicatedFileIdentifier dfIdentifier;
	FileIdentifier fileIdentifier;
	
	@Before
	public void setUp(){
		dfName = new byte []{1,2,3,4,5};
		dfIdentifier = new DedicatedFileIdentifier(dfName);
		fileIdentifier = new FileIdentifier(1);
	}
	
	@Test
	public void testGetFileControlParameterObject(){
		DedicatedFile df = new DedicatedFile(fileIdentifier, dfIdentifier);
		ConstructedTlvDataObject fcp = df.getFileControlParameterDataObject();
		TlvTag dfNameTag = new TlvTag((byte)0x84);
		assertTrue(fcp.containsTlvDataObject(dfNameTag));
		assertArrayEquals(dfName, fcp.getTlvDataObject(dfNameTag).getValueField());
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testAddChildNoAccessRights() throws Exception{
		DedicatedFile df = new DedicatedFile(fileIdentifier, dfIdentifier, SecCondition.DENIED);
		df.setSecStatus(new SecStatus());
		df.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		assertEquals(0, df.getChildren().size());
		df.addChild(new NullCardObject());
	}
	
	@Test
	public void testAddChildAccessRightsValid() throws Exception{
		DedicatedFile df = new DedicatedFile(fileIdentifier, dfIdentifier);
		df.setSecStatus(new SecStatus());
		df.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		assertEquals(0, df.getChildren().size());
		df.addChild(new NullCardObject());
		assertEquals(1, df.getChildren().size());
	}
}
