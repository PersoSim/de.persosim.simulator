package de.persosim.simulator.perso.export;

import java.util.ArrayList;
import java.util.List;

import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.ca.Ca;
import de.persosim.simulator.protocols.ri.Ri;
import de.persosim.simulator.protocols.ri.RiOid;

public class OrderedKeyList
{

	public static final int ID_RI_1_SPERRMERKMAL = 1;
	public static final int ID_RI_2_PSEUDONYM = 2;
	public static final int ID_CA_41 = 41;

	// Restricted Identification (RI) keys
	// Individual RI key - 1st sector public/private key pair (Sperrmerkmal)
	private Key keyRI1 = new Key((GenericOid) new OidIdentifier(new RiOid(Ri.id_RI_ECDH_SHA_256)).getOid(), Boolean.FALSE, Integer.valueOf(ID_RI_1_SPERRMERKMAL), null);
	// Individual RI key - 2nd sector public/private key pair (Pseudonym)
	private Key keyRI2 = new Key((GenericOid) new OidIdentifier(new RiOid(Ri.id_RI_ECDH_SHA_256)).getOid(), Boolean.TRUE, Integer.valueOf(ID_RI_2_PSEUDONYM), null);

	// Chip Authentication Key with ID 41
	private Key keyCA41 = new Key((GenericOid) Ca.OID_IDENTIFIER_id_CA_ECDH_AES_CBC_CMAC_128.getOid(), Boolean.FALSE, Integer.valueOf(ID_CA_41), null);

	private List<Key> orderedKeys = new ArrayList<>();

	public OrderedKeyList()
	{
		orderedKeys.add(keyRI1);
		orderedKeys.add(keyRI2);
	}

	public OrderedKeyList(boolean riKeysOnly)
	{
		this();
		if (!riKeysOnly)
			orderedKeys.add(keyCA41);
	}

	public List<Key> getOrderedKeys()
	{
		return orderedKeys;
	}

	public void setContent(GenericOid oid, Boolean privilegedOnly, Integer id, String content)
	{
		for (Key current : orderedKeys) {
			if (current.getOidInternal().equals(oid) && current.getPrivilegedOnly().equals(privilegedOnly) && current.getId().equals(id)) {
				current.setContent(content);
				break;
			}
		}
	}

	public Key getKey(GenericOid oid, Boolean privilegedOnly, Integer id)
	{
		Key found = null;
		for (Key current : orderedKeys) {
			if (current.getOidInternal().equals(oid) && current.getPrivilegedOnly().equals(privilegedOnly) && current.getId().equals(id)) {
				found = current;
				break;
			}
		}
		return found;
	}

}
