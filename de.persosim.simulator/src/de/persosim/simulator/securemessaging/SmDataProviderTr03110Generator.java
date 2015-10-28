package de.persosim.simulator.securemessaging;

import javax.crypto.spec.SecretKeySpec;

import de.persosim.simulator.crypto.SendSequenceCounter;
import de.persosim.simulator.processing.UpdatePropagation;
import de.persosim.simulator.utils.Serialized;
import de.persosim.simulator.utils.Serializer;

/**
 * This class represents an immutable representation of an {@link SmDataProviderTr03110}.
 * 
 * @author slutters
 *
 */
public class SmDataProviderTr03110Generator implements SmDataProviderGenerator {
	
	private Serialized<SecretKeySpec> serializedKeyEnc;
	private Serialized<SecretKeySpec> serializedKeyMac;
	private Serialized<SendSequenceCounter> serializedSsc;
	private boolean pendingCommandApdu;
	
	
	
	public SmDataProviderTr03110Generator(SecretKeySpec keyEnc, SecretKeySpec keyMac, SendSequenceCounter ssc, boolean pendingCommandApdu) {
		serializedKeyEnc = Serializer.serialize(keyEnc);
		serializedKeyMac = Serializer.serialize(keyMac);
		serializedSsc    = Serializer.serialize(ssc);
		
		this.pendingCommandApdu = pendingCommandApdu;
	}
	
	public SmDataProviderTr03110Generator(SmDataProviderTr03110 smDataProviderTr03110) {
		this(smDataProviderTr03110.getKeyEnc(),
				smDataProviderTr03110.getKeyMac(),
				smDataProviderTr03110.getSsc(),
				smDataProviderTr03110.isPendingCommandApdu());
	}
	
	@Override
	public SmDataProviderTr03110 generateSmDataProvider() {
		SecretKeySpec keyEncNew = Serializer.deserialize(serializedKeyEnc);
		SecretKeySpec keyMacNew = Serializer.deserialize(serializedKeyMac);
		SendSequenceCounter sscNew = Serializer.deserialize(serializedSsc);
		
		if(pendingCommandApdu) {
			sscNew.increment();
		}
		
		return new SmDataProviderTr03110(keyEncNew, keyMacNew, sscNew);
	}
	
	@Override
	public Class<? extends UpdatePropagation> getKey() {
		return SmDataProviderGenerator.class;
	}

}
