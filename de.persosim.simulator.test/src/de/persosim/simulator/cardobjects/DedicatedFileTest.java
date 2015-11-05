package de.persosim.simulator.cardobjects;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.seccondition.SecCondition;
import de.persosim.simulator.secstatus.SecStatus;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvTag;
import mockit.Mocked;

public class DedicatedFileTest extends PersoSimTestCase {

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
		DedicatedFile df = new DedicatedFile(fileIdentifier, dfIdentifier);
		df.setSecStatus(mockedSecurityStatus);
		df.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		assertTrue(df.getChildren().size() == 0);
		df.addChild(new NullCardObject());
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testAddChildAccessRightsValid() throws Exception{
		DedicatedFile df = new DedicatedFile(fileIdentifier, dfIdentifier, SecCondition.DENIED);
		df.setSecStatus(mockedSecurityStatus);
		df.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		assertTrue(df.getChildren().size() == 0);
		df.addChild(new NullCardObject());
	}
}
