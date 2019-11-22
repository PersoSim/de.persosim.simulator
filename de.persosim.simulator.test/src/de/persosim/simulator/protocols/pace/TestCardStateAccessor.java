package de.persosim.simulator.protocols.pace;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.platform.CardStateAccessor;
import de.persosim.simulator.secstatus.SecMechanism;
import de.persosim.simulator.secstatus.SecStatus.SecContext;

public class TestCardStateAccessor implements CardStateAccessor {
	
	private MasterFile mf;
	private HashMap<Class<? extends SecMechanism>, SecMechanism> mechanisms = new HashMap<>();
	
	public TestCardStateAccessor(MasterFile mf) {
		this.mf = mf;
	}

	@Override
	public MasterFile getMasterFile() {
		return mf;
	}

	@Override
	public Collection<SecMechanism> getCurrentMechanisms(SecContext context,
			Collection<Class<? extends SecMechanism>> wantedMechanisms) {

		HashSet<SecMechanism> retVal = new HashSet<>();
		for (Class<? extends SecMechanism> clazz : wantedMechanisms) {
			if (mechanisms.containsKey(clazz)) {
				retVal.add(mechanisms.get(clazz));
			}
		}
		return retVal;
	}

	public void putSecMechanism(Class<? extends SecMechanism> clazz, SecMechanism newMechanism) {
		this.mechanisms.put(clazz, newMechanism);
	}

}
