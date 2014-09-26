package de.persosim.simulator.perso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXB;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.persosim.simulator.jaxb.PersoSimJaxbContextProvider;
import de.persosim.simulator.jaxb.TlvMapAdapter;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SignedDataFileCache {

	@XmlJavaTypeAdapter(TlvMapAdapter.class)
	private Map<TlvDataObject, TlvDataObject> map = new HashMap<>();

	@XmlTransient
	private File file;

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
		
		//ensure that the cache file is updated
		if (file != null) {
			JAXB.marshal(this, file);
		}

	}

	public static SignedDataFileCache getInstance(String fileName) {
		SignedDataFileCache retVal = null;
		File file = new File(fileName);

		if (file.exists()) {
			try {
				Unmarshaller um = PersoSimJaxbContextProvider.getContext().createUnmarshaller();
				retVal = (SignedDataFileCache) um.unmarshal(file);
			} catch (Exception e) {
				// unmarshalling the provided file failed, will create an empty cache in following lines
				e.printStackTrace();
			}
		} 
		
		if (retVal == null) {
			retVal = new SignedDataFileCache();
		}
		retVal.file = file;

		return retVal;
	}
}