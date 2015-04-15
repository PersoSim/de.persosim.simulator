package de.persosim.simulator.perso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;

public class SignedDataFileCache {

	private Map<TlvDataObject, TlvDataObject> map = new HashMap<>();

	private SignedDataFileCache() {

	}

	public boolean containsKey(ConstructedTlvDataObject key) {
		return map.containsKey(key);
	}

	public TlvDataObject get(ConstructedTlvDataObject key) {
		return map.get(key);
	}

	public void put(ConstructedTlvDataObject secInfos,
			ConstructedTlvDataObject signedDataFile) {
		// store new value in actual map
		map.put(secInfos, signedDataFile);

	}

	public static SignedDataFileCache getInstance(String fileName) {
		SignedDataFileCache retVal = null;
		File file = new File(fileName);
		
		if (retVal == null) {
			retVal = new SignedDataFileCache();
		}

		return retVal;
	}
}