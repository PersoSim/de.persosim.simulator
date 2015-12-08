package de.persosim.simulator.test.globaltester;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class GtConstants {

	public static final String PROJECT_EPP_BAC_SAC_ICAO = "GT Scripts EPP BAC SAC ICAO";
	public static final String PROJECT_EPA_EAC2_BSI = "GT Scripts EPA EAC2 BSI";
	
	//commonly used suite descriptors
	public static final GtSuiteDescriptor SUITE_EAC2_BSI_LAYER_6 = new GtSuiteDescriptor(PROJECT_EPA_EAC2_BSI, "testsuite_complete_standard_layer6");
	public static final GtSuiteDescriptor SUITE_EAC2_BSI_LAYER_7 = new GtSuiteDescriptor(PROJECT_EPA_EAC2_BSI, "testsuite_complete_standard_layer7");
	
	public static final GtSuiteDescriptor SUITE_EAC2_ISO7816_H = new GtSuiteDescriptor(PROJECT_EPA_EAC2_BSI, "testsuite_ISO7816_H");
	public static final GtSuiteDescriptor SUITE_EAC2_ISO7816_I = new GtSuiteDescriptor(PROJECT_EPA_EAC2_BSI, "testsuite_ISO7816_I");
	public static final GtSuiteDescriptor SUITE_EAC2_ISO7816_J = new GtSuiteDescriptor(PROJECT_EPA_EAC2_BSI, "testsuite_ISO7816_J");
	public static final GtSuiteDescriptor SUITE_EAC2_ISO7816_K = new GtSuiteDescriptor(PROJECT_EPA_EAC2_BSI, "testsuite_ISO7816_K");
	public static final GtSuiteDescriptor SUITE_EAC2_ISO7816_L = new GtSuiteDescriptor(PROJECT_EPA_EAC2_BSI, "testsuite_ISO7816_L");
	public static final GtSuiteDescriptor SUITE_EAC2_ISO7816_M = new GtSuiteDescriptor(PROJECT_EPA_EAC2_BSI, "testsuite_ISO7816_M");
	public static final GtSuiteDescriptor SUITE_EAC2_ISO7816_N = new GtSuiteDescriptor(PROJECT_EPA_EAC2_BSI, "testsuite_ISO7816_N");
	public static final GtSuiteDescriptor SUITE_EAC2_ISO7816_O = new GtSuiteDescriptor(PROJECT_EPA_EAC2_BSI, "testsuite_ISO7816_O");
	public static final GtSuiteDescriptor SUITE_EAC2_ISO7816_P = new GtSuiteDescriptor(PROJECT_EPA_EAC2_BSI, "testsuite_ISO7816_P");
	public static final GtSuiteDescriptor SUITE_EAC2_ISO7816_Q = new GtSuiteDescriptor(PROJECT_EPA_EAC2_BSI, "testsuite_ISO7816_Q");
	public static final GtSuiteDescriptor SUITE_EAC2_ISO7816_R = new GtSuiteDescriptor(PROJECT_EPA_EAC2_BSI, "testsuite_ISO7816_R");
	public static final GtSuiteDescriptor SUITE_EAC2_DATA_A = new GtSuiteDescriptor(PROJECT_EPA_EAC2_BSI, "testsuite_DATA_A");
	public static final GtSuiteDescriptor SUITE_EAC2_DATA_B = new GtSuiteDescriptor(PROJECT_EPA_EAC2_BSI, "testsuite_DATA_B");
	public static final GtSuiteDescriptor SUITE_EAC2_DATA_C = new GtSuiteDescriptor(PROJECT_EPA_EAC2_BSI, "testsuite_DATA_C");
	public static final GtSuiteDescriptor SUITE_EAC2_EIDDATA_B = new GtSuiteDescriptor(PROJECT_EPA_EAC2_BSI, "testsuite_EIDDATA_B");
	
	public static final GtSuiteDescriptor SUITE_SAC_ISO7816_P = new GtSuiteDescriptor(PROJECT_EPP_BAC_SAC_ICAO, "testsuite_ISO7816_P");
	
	//Relevant profile constants
	public static final String PROFILE_AA = "AA";
	public static final String PROFILE_AA_ECDSA = "AA-ECDSA";
	public static final String PROFILE_AA_RSA = "AA-RSA";
	public static final String PROFILE_AUX = "AUX";
	public static final String PROFILE_AIP = "AIP";
	public static final String PROFILE_BAC = "BAC";
	public static final String PROFILE_CA = "CA";
	public static final String PROFILE_CA_ATGA = "CA_ATGA";
	public static final String PROFILE_CA_KAT = "CA_KAT";
	public static final String PROFILE_CA2 = "CA2";
	public static final String PROFILE_CAN = "CAN";
	public static final String PROFILE_CNG_CAN_AR = "CNG_CAN_AR";
	public static final String PROFILE_CNG_PIN_AR = "CNG_PIN_AR";
	public static final String PROFILE_CNG_PIN_PUK = "CNG_PIN_PUK";
	public static final String PROFILE_CS = "CS";
	public static final String PROFILE_DATE = "DATE";
	public static final String PROFILE_CVCA = "CVCA";
	public static final String PROFILE_DG2 = "DG2";
	public static final String PROFILE_DG3 = "DG3";
	public static final String PROFILE_DG4 = "DG4";
	public static final String PROFILE_DG5 = "DG5";
	public static final String PROFILE_DG6 = "DG6";
	public static final String PROFILE_DG7 = "DG7";
	public static final String PROFILE_DG8 = "DG8";
	public static final String PROFILE_DG9 = "DG9";
	public static final String PROFILE_DG10 = "DG10";
	public static final String PROFILE_DG11 = "DG11";
	public static final String PROFILE_DG12 = "DG12";
	public static final String PROFILE_DG13 = "DG13";
	public static final String PROFILE_DG15 = "DG15";
	public static final String PROFILE_DG16 = "DG16";
	public static final String PROFILE_DH = "DH";
	public static final String PROFILE_EAC = "EAC";
	public static final String PROFILE_ECDH = "ECDH";
	public static final String PROFILE_ECDSA = "ECDSA";
	public static final String PROFILE_EID = "EID";
	public static final String PROFILE_eID = "eID";
	public static final String PROFILE_eID_DG1 = "eID-DG1";
	public static final String PROFILE_eID_DG2 = "eID-DG2";
	public static final String PROFILE_eID_DG3 = "eID-DG3";
	public static final String PROFILE_eID_DG4 = "eID-DG4";
	public static final String PROFILE_eID_DG5 = "eID-DG5";
	public static final String PROFILE_eID_DG6 = "eID-DG6";
	public static final String PROFILE_eID_DG7 = "eID-DG7";
	public static final String PROFILE_eID_DG8 = "eID-DG8";
	public static final String PROFILE_eID_DG9 = "eID-DG9";
	public static final String PROFILE_eID_DG10 = "eID-DG10";
	public static final String PROFILE_eID_DG11 = "eID-DG11";
	public static final String PROFILE_eID_DG12 = "eID-DG12";
	public static final String PROFILE_eID_DG13 = "eID-DG13";
	public static final String PROFILE_eID_DG14 = "eID-DG14";
	public static final String PROFILE_eID_DG15 = "eID-DG15";
	public static final String PROFILE_eID_DG16 = "eID-DG16";
	public static final String PROFILE_eID_DG17 = "eID-DG17";
	public static final String PROFILE_eID_DG18 = "eID-DG18";
	public static final String PROFILE_eID_DG19 = "eID-DG19";
	public static final String PROFILE_eID_DG20 = "eID-DG20";
	public static final String PROFILE_eID_DG21 = "eID-DG21";
	public static final String PROFILE_EPASSPORT = "ePassport";
	public static final String PROFILE_EPASSPORT_DG3 = "ePassport-DG3";
	public static final String PROFILE_EPASSPORT_DG4 = "ePassport-DG4";
	// public static final String PROFILE_Generate = "Generate";
	// public static final String PROFILE_Generator = "Generator";
	public static final String PROFILE_ICAO = "ICAO";
	public static final String PROFILE_KEYREF = "KeyRef";
	public static final String PROFILE_MIG = "MIG";
	public static final String PROFILE_NOT_CNG_CAN_AR = "NOT CNG_CAN_AR";
	public static final String PROFILE_NOT_CNG_PIN_AR = "NOT CNG_PIN_AR";
	public static final String PROFILE_NOT_CNG_PIN_PUK = "NOT CNG_PIN_PUK";
	public static final String PROFILE_OddIns = "OddIns";
	public static final String PROFILE_PACE = "PACE";
	public static final String PROFILE_PACE_CAN = "PACE-CAN";
	public static final String PROFILE_PACE_DH = "PACE-DH";
	public static final String PROFILE_PACE_ECDH = "PACE-ECDH";
	public static final String PROFILE_PACE_GM = "PACE-GM";
	public static final String PROFILE_PACE_IM = "PACE-IM";
	public static final String PROFILE_PACE_CAM = "PACE-CAM";
	public static final String PROFILE_Plain = "Plain";
	public static final String PROFILE_PLAINTEXT = "Plaintext";
	public static final String PROFILE_RI = "RI";
	public static final String PROFILE_RI_DP = "RI_DP";
	public static final String PROFILE_RSA = "RSA";
	public static final String PROFILE_SAC = "SAC";
	public static final String PROFILE_SIP = "SIP";
	public static final String PROFILE_TA = "TA";
	public static final String PROFILE_TA2 = "TA2";
	public static final String PROFILE_TS_CA = "TS_CA";
	public static final String PROFILE_TS_eID = "TS_eID";
	public static final String PROFILE_TS_PACE = "TS_PACE";
	public static final String PROFILE_TS_TA = "TS_TA";

	
	private static Set<String> allProfiles= new HashSet<>();
	static {
		//add all static fields with name PROFILE_* to allProfiles (and thus to ALL_PROFILES)
		Field[] fields = GtConstants.class.getDeclaredFields();
		for (Field f : fields) {
			f.setAccessible(true);

			if ((f.getName().startsWith("PROFILE_"))
					&& Modifier.isStatic(f.getModifiers())
					&& Modifier.isFinal(f.getModifiers())
					&& f.getType().equals(String.class)) {
				try {
					allProfiles.add((String) f.get(null));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// ignore the current field
				}
			}
		}

	}
	public static final Set<String> ALL_PROFILES = Collections.unmodifiableSet(allProfiles);	

}
