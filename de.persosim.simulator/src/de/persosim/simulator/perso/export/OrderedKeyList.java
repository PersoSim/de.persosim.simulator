package de.persosim.simulator.perso.export;

import java.util.ArrayList;
import java.util.List;

import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.ca.Ca;
import de.persosim.simulator.protocols.ri.Ri;
import de.persosim.simulator.protocols.ri.RiOid;
import jakarta.annotation.Nullable;

public class OrderedKeyList
{

	public static final int ID_RI_1_SPERRMERKMAL = 1;
	public static final int ID_RI_2_PSEUDONYM = 2;
	public static final int ID_CA_41 = 41;
	public static final int ID_CA_45 = 45;

	// Restricted Identification (RI) keys
	// Individual RI key - 1st sector public/private key pair (Sperrmerkmal)
	private Key keyRI1 = new Key((GenericOid) new OidIdentifier(new RiOid(Ri.id_RI_ECDH_SHA_256)).getOid(), Boolean.FALSE, Integer.valueOf(ID_RI_1_SPERRMERKMAL), null);
	// Individual RI key - 2nd sector public/private key pair (Pseudonym)
	private Key keyRI2 = new Key((GenericOid) new OidIdentifier(new RiOid(Ri.id_RI_ECDH_SHA_256)).getOid(), Boolean.TRUE, Integer.valueOf(ID_RI_2_PSEUDONYM), null);

	// Chip Authentication Key with ID 41
	private Key keyCA41 = new Key((GenericOid) Ca.OID_IDENTIFIER_id_CA_ECDH_AES_CBC_CMAC_128.getOid(), Boolean.FALSE, Integer.valueOf(ID_CA_41), null);

	// Chip Authentication Key with ID 45
	private Key keyCA45 = new Key((GenericOid) Ca.OID_IDENTIFIER_id_CA_ECDH_AES_CBC_CMAC_128.getOid(), Boolean.TRUE, Integer.valueOf(ID_CA_45), null);

	private List<Key> orderedKeys = new ArrayList<>();

	public OrderedKeyList()
	{
		orderedKeys.add(keyRI1);
		orderedKeys.add(keyRI2);
	}

	public OrderedKeyList(boolean riKeysOnly)
	{
		this();
		if (!riKeysOnly) {
			orderedKeys.add(keyCA41);
			orderedKeys.add(keyCA45);
		}
	}

	public List<Key> getOrderedKeys()
	{
		return orderedKeys;
	}

	public void setContent(GenericOid oid, Boolean privilegedOnly, Integer id, String content)
	{
		Key found = getKey(oid, privilegedOnly, id);
		if (found != null) {
			found.setContent(content);
		}
		else {
			found = getKey(oid, privilegedOnly);
			if (found != null) {
				found.setId(id);
				found.setContent(content);
			}
		}
	}

	@Nullable
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

	@Nullable
	public Key getKey(GenericOid oid, Boolean privilegedOnly)
	{
		Key found = null;
		for (Key current : orderedKeys) {
			if (current.getOidInternal().equals(oid) && current.getPrivilegedOnly().equals(privilegedOnly)) {
				found = current;
				break;
			}
		}
		return found;
	}

}
