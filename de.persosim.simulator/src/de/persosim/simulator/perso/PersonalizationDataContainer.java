package de.persosim.simulator.perso;

import java.security.KeyPair;
import java.util.ArrayList;

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
						dg21PlainData, documentNumber, mrzLine3Of3;
	
	ArrayList<KeyPair> caKeys, riKeys;
	ArrayList<Integer> caKeyIds, riKeyIds;
	
	public PersonalizationDataContainer() {
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
		
		this.documentNumber       = null;
		this.mrzLine3Of3          = null;
		
		this.caKeys               = new ArrayList<KeyPair>();
		this.caKeyIds             = new ArrayList<Integer>();
		
		this.riKeys               = new ArrayList<KeyPair>();
		this.riKeyIds             = new ArrayList<Integer>();
	}
	
	public void addCaKeyPair(KeyPair keyPair, int keyId) {
		this.caKeys.add(keyPair);
		this.caKeyIds.add(keyId);
	}
	
	public void addRiKeyPair(KeyPair keyPair, int keyId) {
		this.riKeys.add(keyPair);
		this.riKeyIds.add(keyId);
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

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getMrzLine3Of3() {
		return mrzLine3Of3;
	}

	public void setMrzLine3Of3(String mrzLine3Of3) {
		this.mrzLine3Of3 = mrzLine3Of3;
	}

	public ArrayList<KeyPair> getCaKeys() {
		return caKeys;
	}

	public void setCaKeys(ArrayList<KeyPair> caKeys) {
		this.caKeys = caKeys;
	}

	public ArrayList<KeyPair> getRiKeys() {
		return riKeys;
	}

	public void setRiKeys(ArrayList<KeyPair> riKeys) {
		this.riKeys = riKeys;
	}

	public ArrayList<Integer> getCaKeyIds() {
		return caKeyIds;
	}

	public void setCaKeyIds(ArrayList<Integer> caKeyIds) {
		this.caKeyIds = caKeyIds;
	}

	public ArrayList<Integer> getRiKeyIds() {
		return riKeyIds;
	}

	public void setRiKeyIds(ArrayList<Integer> riKeyIds) {
		this.riKeyIds = riKeyIds;
	}
	
}
