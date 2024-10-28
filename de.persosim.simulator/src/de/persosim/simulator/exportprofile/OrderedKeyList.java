package de.persosim.simulator.exportprofile;

import java.util.ArrayList;
import java.util.List;

public class OrderedKeyList {

	public static final int ID_RI_1_SPERRMERKMAL = 1;
	public static final int ID_RI_2_PSEUDONYM = 2;
	public static final int ID_41 = 41;

	// Restricted Identification (RI) keys
	// Individual RI key - 1st sector public/private key pair (Sperrmerkmal)
	private Key keyRI1 = new Key(ID_RI_1_SPERRMERKMAL, null);
	// Individual RI key - 2nd sector public/private key pair (Pseudonym)
	private Key keyRI2 = new Key(ID_RI_2_PSEUDONYM, null);

	private Key key41 = new Key(ID_41, null);

	private List<Key> orderedKeys = new ArrayList<>();

	public OrderedKeyList() {
		orderedKeys.add(keyRI1);
		orderedKeys.add(keyRI2);
		orderedKeys.add(key41);
	}

	public List<Key> getOrderedKeys() {
		return orderedKeys;
	}

	public void setContentById(int id, String content) {
		for (Key current : orderedKeys) {
			if (current.getId() == id) {
				current.setContent(content);
				break;
			}
		}
	}

	public Key getKeyById(int id) {
		Key found = null;
		for (Key current : orderedKeys) {
			if (current.getId() == id) {
				found = current;
				break;
			}
		}
		return found;
	}

}
