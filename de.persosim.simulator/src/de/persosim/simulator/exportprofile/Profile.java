package de.persosim.simulator.exportprofile;

import java.util.ArrayList;
import java.util.List;

import org.globaltester.logging.BasicLogger;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonPropertyOrder({ //
		"files", //
		"keys", //
		"trustpoint", //
		"pin", //
		"can", //
		"puk", //
		"pinEnabled", //
		"pinRetryCounter", //
		"pinResetCounter" })
public class Profile
{

	// BSI Test-PKI CVCA root certificate
	public static final String CVCA_ROOT_CERT = "7F218201B67F4E82016E5F290100420E44455445535465494430303030357F4982011D060A04007F000702020202038120A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E537782207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9832026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B68441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F0469978520A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A78641049BFEBA8DC7FAAB6E3BDEB3FF794DBB800848FE4F6940A4CC7EECB5159C87DA5395505892026D420A22596CD014ED1FD872DADA597DB0F8D64441041198F62D448701015F200E44455445535465494430303030357F4C12060904007F0007030102025305FC0F13FFFF5F25060105000500045F24060108000500045F374058B4E65598EFB9CA2CAFC05C80F5A907E8B69C3897C704739320896DC53492E47766841A9C3D4EAC85CE653D166B53DB06A70E735AB93C88858811EF69D6B543";

	private List<File> files = new ArrayList<>();
	private List<Key> keys = new ArrayList<>();
	private String trustpoint = CVCA_ROOT_CERT;
	private String pin;
	private String can;
	private String puk;
	private boolean pinEnabled;
	private Integer pinRetryCounter = null;
	private Integer pinResetCounter = null;


	public Profile()
	{
		// do nothing; default constructor necessary for JSON (de-)serialization
	}

	public Profile(List<File> files, List<Key> keys, String trustpoint, String pin, String can, String puk, boolean pinEnabled, Integer pinRetryCounter, Integer pinResetCounter)
	{
		if (files != null)
			this.files = files;
		if (keys != null)
			this.keys = keys;
		this.trustpoint = trustpoint;
		this.pin = pin;
		this.can = can;
		this.puk = puk;
		this.pinEnabled = pinEnabled;
		this.pinRetryCounter = pinRetryCounter;
		this.pinResetCounter = pinResetCounter;
	}

	public List<File> getFiles()
	{
		return files;
	}

	public void setFiles(List<File> files)
	{
		this.files = files;
	}

	public List<Key> getKeys()
	{
		return keys;
	}

	public void setKeys(List<Key> keys)
	{
		this.keys = keys;
	}

	public String getTrustpoint()
	{
		return trustpoint;
	}

	public void setTrustpoint(String trustpoint)
	{
		this.trustpoint = trustpoint;
	}

	public String getPin()
	{
		return pin;
	}

	public void setPin(String pin)
	{
		this.pin = pin;
	}

	public String getCan()
	{
		return can;
	}

	public void setCan(String can)
	{
		this.can = can;
	}

	public String getPuk()
	{
		return puk;
	}

	public void setPuk(String puk)
	{
		this.puk = puk;
	}

	public boolean isPinEnabled()
	{
		return pinEnabled;
	}

	public void setPinEnabled(boolean pinEnabled)
	{
		this.pinEnabled = pinEnabled;
	}

	public Integer getPinRetryCounter()
	{
		return pinRetryCounter;
	}

	public void setPinRetryCounter(Integer pinRetryCounter)
	{
		this.pinRetryCounter = pinRetryCounter;
	}

	public Integer getPinResetCounter()
	{
		return pinResetCounter;
	}

	public void setPinResetCounter(Integer pinResetCounter)
	{
		this.pinResetCounter = pinResetCounter;
	}

	public String serialize()
	{
		String jsonSerialized = null;
		try
		{
			ObjectMapper objectMapper = new ObjectMapper();
			jsonSerialized = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
			// String jsonSerialized = objectMapper.writer(new ProfilePrettyPrinter()).writeValueAsString(profile);
			// System.out.println(jsonSerialized);
		}
		catch (JsonProcessingException e)
		{
			BasicLogger.logException(this.getClass(), e);
		}
		return jsonSerialized;
	}

	public static Profile deserialize(String jsonSerialized)
	{

		Profile profileDeserialized = null;
		try
		{
			ObjectMapper objectMapper = new ObjectMapper();
			profileDeserialized = objectMapper.readValue(jsonSerialized, Profile.class);
		}
		catch (JsonProcessingException e)
		{
			BasicLogger.logException(Profile.class, e);
		}
		return profileDeserialized;
	}
}
