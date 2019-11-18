package de.persosim.simulator.cardobjects;

import org.junit.Test;

import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.exception.LifeCycleChangeException;
import de.persosim.simulator.protocols.ta.CertificateRole;
import de.persosim.simulator.protocols.ta.RelativeAuthorization;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.seccondition.OrSecCondition;
import de.persosim.simulator.seccondition.PaceWithPasswordSecurityCondition;
import de.persosim.simulator.seccondition.SecCondition;
import de.persosim.simulator.seccondition.TaSecurityCondition;
import de.persosim.simulator.utils.BitField;
import de.persosim.simulator.utils.HexString;

public class ChangeablePasswordAuthObjectTest {
	AuthObjectIdentifier mockedAuthObjectIdentifier = new AuthObjectIdentifier(42);
	
	/**
	 * Positive test case: create new {@link ChangeablePasswordAuthObject} object using only non-default constructor.
	 */
	@Test
	public void testCreateChangeablePasswordAuthObject(){
		new ChangeablePasswordAuthObject(
				mockedAuthObjectIdentifier, HexString.toByteArray("001122"), "XXX", 0, 3, SecCondition.DENIED, SecCondition.DENIED);
	}
	
	/**
	 * Negative test case: create new {@link ChangeablePasswordAuthObject} object using password longer than allowed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateChangeablePasswordAuthObject_PwdTooLong(){
		new ChangeablePasswordAuthObject(
				mockedAuthObjectIdentifier, HexString.toByteArray("001122"), "XXX", 0, 0, SecCondition.DENIED, SecCondition.DENIED);
	}
	
	/**
	 * Negative test case: create new {@link ChangeablePasswordAuthObject} object using password shorter than allowed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateChangeablePasswordAuthObject_PwdTooShort(){
		new ChangeablePasswordAuthObject(
				mockedAuthObjectIdentifier, HexString.toByteArray("00"), "XXX", 3, 3, SecCondition.DENIED, SecCondition.DENIED);
	}
	
	/**
	 * Negative test case: set new password, password deactivated.
	 * @throws LifeCycleChangeException 
	 * @throws AccessDeniedException 
	 */
	@Test(expected = IllegalStateException.class)
	public void testGetFileControlParameterObject2() throws LifeCycleChangeException, AccessDeniedException{
		TaSecurityCondition pinManagementCondition = new TaSecurityCondition(TerminalType.AT,
				new RelativeAuthorization(CertificateRole.TERMINAL, new BitField(38).flipBit(5)));
		ChangeablePasswordAuthObject pwd = new ChangeablePasswordAuthObject(
				mockedAuthObjectIdentifier, HexString.toByteArray("001122"), "XXX", 3, 3, pinManagementCondition,
				new OrSecCondition(new PaceWithPasswordSecurityCondition("PIN"), new PaceWithPasswordSecurityCondition("PUK")));
		pwd.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_ACTIVATED);
		pwd.updateLifeCycleState(Iso7816LifeCycleState.OPERATIONAL_DEACTIVATED);
		
		pwd.setPassword(HexString.toByteArray("AABBCC"));
	}
	
}
