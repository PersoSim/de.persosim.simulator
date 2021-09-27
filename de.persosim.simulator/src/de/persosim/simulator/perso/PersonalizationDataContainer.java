package de.persosim.simulator.perso;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Collection;

import de.persosim.simulator.cardobjects.CardObject;

/**
 * @author slutters
 *
 */
public class PersonalizationDataContainer {
	
	protected String dg1PlainData, dg2PlainData, dg3PlainData, dg4PlainData, dg5PlainData,
						dg6PlainData, dg7PlainData, dg8PlainData, dg9PlainData, dg10PlainData,
						dg11PlainData, dg12PlainData, dg13PlainData, dg14PlainData, dg15PlainData,
						dg16PlainData, dg17StreetPlainData, dg17CityPlainData, dg17StatePlainData,
						dg17CountryPlainData, dg17ZipPlainData, dg18PlainData, dg19PlainData, dg20PlainData,
						dg21PlainData, dg22PlainData, efCardAccess, efChipSecurity, efCardSecurity, epassDg1PlainData, mrz;
	
	ArrayList<KeyPair> caKeys, riKeys;
	ArrayList<Integer> caKeyIds, riKeyIds;
	ArrayList<Boolean> caKeyPrivileges, riKeyAuthorizedOnly;
	
	boolean pinEnabled = true;

	private Collection<CardObject> additionalObjects;
	
	public PersonalizationDataContainer() {
		this.mrz                  = null;
		
		this.dg1PlainData         = null;
		this.dg2PlainData         = null;
		this.dg3PlainData         = null;
		this.dg4PlainData         = null;
		this.dg5PlainData         = null;
		this.dg6PlainData         = null;
		this.dg7PlainData         = null;
		this.dg8PlainData         = null;
		this.dg9PlainData         = null;
		this.dg10PlainData        = null;
		this.dg11PlainData        = null;
		this.dg12PlainData        = null;
		this.dg13PlainData        = null;
		this.dg14PlainData        = null;
		this.dg15PlainData        = null;
		this.dg16PlainData        = null;
		this.dg17StreetPlainData  = null;
		this.dg17CityPlainData    = null;
		this.dg17StatePlainData   = null;
		this.dg17CountryPlainData = null;
		this.dg17ZipPlainData     = null;
		this.dg18PlainData        = null;
		this.dg19PlainData        = null;
		this.dg20PlainData        = null;
		this.dg21PlainData        = null;
		this.dg22PlainData        = null;
		
		this.efCardAccess		  = null;
		
		this.epassDg1PlainData    = null;
		
		this.caKeys               = new ArrayList<>();
		this.caKeyIds             = new ArrayList<>();
		this.caKeyPrivileges      = new ArrayList<>();
		
		this.riKeys               = new ArrayList<>();
		this.riKeyIds             = new ArrayList<>();
		this.riKeyAuthorizedOnly  = new ArrayList<>();
		
		this.additionalObjects = new ArrayList<>();
	}
	
	public void addCaKeyPair(KeyPair keyPair, int keyId, boolean privileged) {
		this.caKeys.add(keyPair);
		this.caKeyIds.add(keyId);
		this.caKeyPrivileges.add(privileged);
	}
	
	public void addRiKeyPair(KeyPair keyPair, int keyId, boolean authorizedOnly) {
		this.riKeys.add(keyPair);
		this.riKeyIds.add(keyId);
		this.riKeyAuthorizedOnly.add(authorizedOnly);
	}
	
	public String getMrz() {
		return mrz;
	}

	public void setMrz(String mrz) {
		this.mrz = mrz;
	}

	/**
	 * This method returns the default String "ID" for eID application data group 1 (Document Type) of the new German identity card.
	 * @return the document type
	 */
	public String getDg1PlainData() {
		return dg1PlainData;
	}

	public void setDg1PlainData(String dg1PlainData) {
		this.dg1PlainData = dg1PlainData;
	}
	
	/**
	 * This method returns the default String "D" for eID application data group 2 (Issuing State) of the new German identity card.
	 * @return the issuing state
	 */
	public String getDg2PlainData() {
		return dg2PlainData;
	}

	public void setDg2PlainData(String dg2PlainData) {
		this.dg2PlainData = dg2PlainData;
	}
	
	/**
	 * This method returns the default String "20201031" for eID application data group 3 (Date of Expiry) of the new German identity card.
	 * @return the date of expiry
	 */
	public String getDg3PlainData() {
		return dg3PlainData;
	}

	public void setDg3PlainData(String dg3PlainData) {
		this.dg3PlainData = dg3PlainData;
	}

	public String getDg4PlainData() {
		return dg4PlainData;
	}

	public void setDg4PlainData(String dg4PlainData) {
		this.dg4PlainData = dg4PlainData;
	}

	public String getDg5PlainData() {
		return dg5PlainData;
	}

	public void setDg5PlainData(String dg5PlainData) {
		this.dg5PlainData = dg5PlainData;
	}
	
	/**
	 * This method returns the default String "" for eID application data group 6 (Religious/Artistic Name) of the new German identity card.
	 * @return the religious/artistic name
	 */
	public String getDg6PlainData() {
		return dg6PlainData;
	}

	public void setDg6PlainData(String dg6PlainData) {
		this.dg6PlainData = dg6PlainData;
	}
	
	/**
	 * This method returns the default String "" for eID application data group 7 (Academic Title) of the new German identity card.
	 * @return the academic title
	 */
	public String getDg7PlainData() {
		return dg7PlainData;
	}

	public void setDg7PlainData(String dg7PlainData) {
		this.dg7PlainData = dg7PlainData;
	}

	public String getDg8PlainData() {
		return dg8PlainData;
	}

	public void setDg8PlainData(String dg8PlainData) {
		this.dg8PlainData = dg8PlainData;
	}

	public String getDg9PlainData() {
		return dg9PlainData;
	}

	public void setDg9PlainData(String dg9PlainData) {
		this.dg9PlainData = dg9PlainData;
	}
	
	public String getEfChipSecurity() {
		
		return efChipSecurity;
	}

	public void setEfChipSecurity(String efChipSecurity) {
		this.efChipSecurity = efChipSecurity;
	}
	
	public String getEfCardSecurity() {
		
		return efCardSecurity;
	}

	public void setEfCardSecurity(String efCardSecurity) {
		this.efCardSecurity = efCardSecurity;
	}
	
	public String getEfCardAccess() {
		
		return efCardAccess;
	}

	public void setEfCardAccess(String efCardAccess) {
		this.efCardAccess = efCardAccess;
	}
	
	
	/**
	 * This method returns the default String "D" for eID application data group 10 (Nationality) of the new German identity card.
	 * @return the nationality
	 */
	public String getDg10PlainData() {
		return dg10PlainData;
	}

	public void setDg10PlainData(String dg10PlainData) {
		this.dg10PlainData = dg10PlainData;
	}

	public String getDg11PlainData() {
		return dg11PlainData;
	}

	public void setDg11PlainData(String dg11PlainData) {
		this.dg11PlainData = dg11PlainData;
	}

	public String getDg12PlainData() {
		return dg12PlainData;
	}

	public void setDg12PlainData(String dg12PlainData) {
		this.dg12PlainData = dg12PlainData;
	}
	
	/**
	 * This method returns the default String "" for eID application data group 13 (Birth Name) of the new German identity card.
	 * @return the birth name
	 */
	public String getDg13PlainData() {
		return dg13PlainData;
	}

	public void setDg13PlainData(String dg13PlainData) {
		this.dg13PlainData = dg13PlainData;
	}

	public String getDg14PlainData() {
		return dg14PlainData;
	}

	public void setDg14PlainData(String dg14PlainData) {
		this.dg14PlainData = dg14PlainData;
	}

	public String getDg15PlainData() {
		return dg15PlainData;
	}

	public void setDg15PlainData(String dg15PlainData) {
		this.dg15PlainData = dg15PlainData;
	}

	public String getDg16PlainData() {
		return dg16PlainData;
	}

	public void setDg16PlainData(String dg16PlainData) {
		this.dg16PlainData = dg16PlainData;
	}

	public String getDg17StreetPlainData() {
		return dg17StreetPlainData;
	}

	public void setDg17StreetPlainData(String dg17StreetPlainData) {
		this.dg17StreetPlainData = dg17StreetPlainData;
	}

	public String getDg17CityPlainData() {
		return dg17CityPlainData;
	}

	public void setDg17CityPlainData(String dg17CityPlainData) {
		this.dg17CityPlainData = dg17CityPlainData;
	}

	public String getDg17StatePlainData() {
		return dg17StatePlainData;
	}

	public void setDg17StatePlainData(String dg17StatePlainData) {
		this.dg17StatePlainData = dg17StatePlainData;
	}

	public String getDg17CountryPlainData() {
		return dg17CountryPlainData;
	}

	public void setDg17CountryPlainData(String dg17CountryPlainData) {
		this.dg17CountryPlainData = dg17CountryPlainData;
	}

	public String getDg17ZipPlainData() {
		return dg17ZipPlainData;
	}

	public void setDg17ZipPlainData(String dg17ZipPlainData) {
		this.dg17ZipPlainData = dg17ZipPlainData;
	}

	public void setDg17PlainData(String dg17PlainData) {
		this.dg17StreetPlainData = dg17PlainData;
	}

	public String getDg18PlainData() {
		return dg18PlainData;
	}

	public void setDg18PlainData(String dg18PlainData) {
		this.dg18PlainData = dg18PlainData;
	}

	public String getDg19PlainData() {
		return dg19PlainData;
	}

	public void setDg19PlainData(String dg19PlainData) {
		this.dg19PlainData = dg19PlainData;
	}

	public String getDg20PlainData() {
		return dg20PlainData;
	}

	public void setDg20PlainData(String dg20PlainData) {
		this.dg20PlainData = dg20PlainData;
	}

	public String getDg21PlainData() {
		return dg21PlainData;
	}

	public void setDg21PlainData(String dg21PlainData) {
		this.dg21PlainData = dg21PlainData;
	}

	public String getDg22PlainData() {
		return dg22PlainData;
	}

	public void setDg22PlainData(String dg22PlainData) {
		this.dg22PlainData = dg22PlainData;
	}

	public String getEpassDg1PlainData() {
		return epassDg1PlainData;
	}

	public void setEpassDg1PlainData(String epassDg1PlainData) {
		this.epassDg1PlainData = epassDg1PlainData;
	}

	public ArrayList<KeyPair> getCaKeys() {
		return caKeys;
	}

	public ArrayList<KeyPair> getRiKeys() {
		return riKeys;
	}
	
	public ArrayList<Boolean> getRiKeyAuthorizedOnly() {
		return riKeyAuthorizedOnly;
	}

	public ArrayList<Integer> getCaKeyIds() {
		return caKeyIds;
	}
	
	public ArrayList<Boolean> getCaKeyPrivileges() {
		return caKeyPrivileges;
	}

	public ArrayList<Integer> getRiKeyIds() {
		return riKeyIds;
	}
	
	/**
	 * This method creates the 3-line 3x30 = 90 characters MRZ based on the mandatory data group data specified within this object.
	 * The data groups used are eId DGs 1, 2, 3, 8, 10 as well as the provided document number, sex and third line of the MRZ. 
	 * @param documentNumber the document number to be used
	 * @param sex the sex to be used
	 * @param mrzLine3 the third line of the MRZ
	 * @return the 3-line 3x30 = 90 characters MRZ
	 */
	public String createMrzFromDgs(String documentNumber, String sex, String mrzLine3) {
		return AbstractProfile.getMrz(dg1PlainData,
				dg2PlainData,
				documentNumber,
				dg8PlainData,
				sex,
				dg3PlainData,
				dg10PlainData,
				mrzLine3);
	}
	
	/**
	 * This method returns a {@link PersonalizationDataContainer} object with some default settings.
	 * @return a {@link PersonalizationDataContainer} object with some default settings
	 */
	public static PersonalizationDataContainer getDefaultContainer() {
		PersonalizationDataContainer pdc = new PersonalizationDataContainer();
		
		pdc.setDg1PlainData("ID");
		pdc.setDg2PlainData("D");
		pdc.setDg3PlainData("20291031");
		pdc.setDg6PlainData("");
		pdc.setDg7PlainData("");
		pdc.setDg13PlainData("");
		pdc.setDg19PlainData("ResPermit1");
		pdc.setDg20PlainData("ResPermit2");
		
		return pdc;
	}

	public boolean isPinEnabled() {
		return pinEnabled;
	}

	public void setPinEnabled(boolean newValue) {
		pinEnabled = newValue;
	}

	public Collection<CardObject> getAdditionalObjects() {
		return additionalObjects;
	}

	public void addAdditionalObject(CardObject newObject) {
		additionalObjects.add(newObject);
	}
	
}
