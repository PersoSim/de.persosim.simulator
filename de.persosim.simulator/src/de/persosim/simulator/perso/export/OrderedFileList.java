package de.persosim.simulator.perso.export;

import java.util.ArrayList;
import java.util.List;

public class OrderedFileList
{

	public static final String FID_EF_DIR = "2f00";
	public static final String SFI_EF_DIR = "1e";
	// public static final String FID_EF_CHIP_SECURITY = "011b";
	// public static final String SFI_EF_CHIP_SECURITY = "1b";
	public static final String FID_EF_CARD_ACCESS = "011c";
	public static final String SFI_EF_CARD_ACCESS = "1c";
	public static final String FID_EF_CARD_SECURITY = "011d";
	public static final String SFI_EF_CARD_SECURITY = "1d";

	public static final String FID_DG1 = "0101";
	public static final String SFI_DG1 = "01";
	public static final String FID_DG2 = "0102";
	public static final String SFI_DG2 = "02";
	public static final String FID_DG3 = "0103";
	public static final String SFI_DG3 = "03";
	public static final String FID_DG4 = "0104";
	public static final String SFI_DG4 = "04";
	public static final String FID_DG5 = "0105";
	public static final String SFI_DG5 = "05";
	public static final String FID_DG6 = "0106";
	public static final String SFI_DG6 = "06";
	public static final String FID_DG7 = "0107";
	public static final String SFI_DG7 = "07";
	public static final String FID_DG8 = "0108";
	public static final String SFI_DG8 = "08";
	public static final String FID_DG9 = "0109";
	public static final String SFI_DG9 = "09";
	public static final String FID_DG10 = "010a";
	public static final String SFI_DG10 = "0a";
	public static final String FID_DG11 = "010b";
	public static final String SFI_DG11 = "0b";
	public static final String FID_DG12 = "010c";
	public static final String SFI_DG12 = "0c";
	public static final String FID_DG13 = "010d";
	public static final String SFI_DG13 = "0d";
	// public static final String FID_DG14 = "010e";
	// public static final String SFI_DG14 = "0e";
	public static final String FID_DG15 = "010f";
	public static final String SFI_DG15 = "0f";
	// public static final String FID_DG16 = "0110";
	// public static final String SFI_DG16 = "10";
	public static final String FID_DG17 = "0111";
	public static final String SFI_DG17 = "11";
	public static final String FID_DG18 = "0112";
	public static final String SFI_DG18 = "12";
	public static final String FID_DG19 = "0113";
	public static final String SFI_DG19 = "13";
	public static final String FID_DG20 = "0114";
	public static final String SFI_DG20 = "14";
	public static final String FID_DG21 = "0115";
	public static final String SFI_DG21 = "15";
	public static final String FID_DG22 = "0116";
	public static final String SFI_DG22 = "16";

	private File fileEFDir = new File(FID_EF_DIR, SFI_EF_DIR, null);
	// private File fileEFChipSecurity = new File(FID_EF_CHIP_SECURITY, SFI_EF_CHIP_SECURITY, null);
	private File fileEFCardAccess = new File(FID_EF_CARD_ACCESS, SFI_EF_CARD_ACCESS, null);
	private File fileEFCardSecurity = new File(FID_EF_CARD_SECURITY, SFI_EF_CARD_SECURITY, null);
	// BSI TR-03110 Part 4
	private File fileDG1 = new File(FID_DG1, SFI_DG1, null); // Document Type
	private File fileDG2 = new File(FID_DG2, SFI_DG2, null); // Issuing State, Region and Municipality
	private File fileDG3 = new File(FID_DG3, SFI_DG3, null); // Date of Expiry
	private File fileDG4 = new File(FID_DG4, SFI_DG4, null); // Given Names
	private File fileDG5 = new File(FID_DG5, SFI_DG5, null); // Family Names
	private File fileDG6 = new File(FID_DG6, SFI_DG6, null); // Nom de Plume
	private File fileDG7 = new File(FID_DG7, SFI_DG7, null); // Academic Title
	private File fileDG8 = new File(FID_DG8, SFI_DG8, null); // Date of Birth
	private File fileDG9 = new File(FID_DG9, SFI_DG9, null); // Place of Birth
	private File fileDG10 = new File(FID_DG10, SFI_DG10, null); // Nationality
	private File fileDG11 = new File(FID_DG11, SFI_DG11, null); // Sex
	private File fileDG12 = new File(FID_DG12, SFI_DG12, null); // Optional Data
	private File fileDG13 = new File(FID_DG13, SFI_DG13, null); // Birth Name
	// private File fileDG14 = new File(FID_DG14, SFI_DG14, null); // Written Signature
	private File fileDG15 = new File(FID_DG15, SFI_DG15, null); // Date of Issuance
	// private File fileDG16 = new File(FID_DG16, SFI_DG16, null); // Not used; RFU
	private File fileDG17 = new File(FID_DG17, SFI_DG17, null); // Normal Place of Residence (multiple)
	private File fileDG18 = new File(FID_DG18, SFI_DG18, null); // Municipality ID
	private File fileDG19 = new File(FID_DG19, SFI_DG19, null); // Residence Permit I
	private File fileDG20 = new File(FID_DG20, SFI_DG20, null); // Residence Permit II
	private File fileDG21 = new File(FID_DG21, SFI_DG21, null); // Phone Number
	private File fileDG22 = new File(FID_DG22, SFI_DG22, null); // Email Address

	private List<File> orderedFiles = new ArrayList<>();

	public OrderedFileList()
	{
		orderedFiles.add(fileEFDir);
		orderedFiles.add(fileEFCardAccess);
		orderedFiles.add(fileEFCardSecurity);
		orderedFiles.add(fileDG1);
		orderedFiles.add(fileDG2);
		orderedFiles.add(fileDG3);
		orderedFiles.add(fileDG4);
		orderedFiles.add(fileDG5);
		orderedFiles.add(fileDG6);
		orderedFiles.add(fileDG7);
		orderedFiles.add(fileDG8);
		orderedFiles.add(fileDG9);
		orderedFiles.add(fileDG10);
		orderedFiles.add(fileDG11);
		orderedFiles.add(fileDG12);
		orderedFiles.add(fileDG13);
		orderedFiles.add(fileDG15);
		orderedFiles.add(fileDG17);
		orderedFiles.add(fileDG18);
		orderedFiles.add(fileDG19);
		orderedFiles.add(fileDG20);
		orderedFiles.add(fileDG21);
		orderedFiles.add(fileDG22);
	}

	public List<File> getOrderedFiles()
	{
		return orderedFiles;
	}

	public void setContentByFileId(String fileId, String content)
	{
		for (File current : orderedFiles) {
			if (current.getFileId().equals(fileId)) {
				current.setContent(content);
				break;
			}
		}
	}

	public void setContentByShortFileId(String shortFileId, String content)
	{
		for (File current : orderedFiles) {
			if (current.getShortFileId().equals(shortFileId)) {
				current.setContent(content);
				break;
			}
		}
	}

	public File getFileByFileId(String fileId)
	{
		File found = null;
		for (File current : orderedFiles) {
			if (current.getFileId().equals(fileId)) {
				found = current;
				break;
			}
		}
		return found;
	}

	public File getFileByShortFileId(String shortFileId)
	{
		File found = null;
		for (File current : orderedFiles) {
			if (current.getShortFileId().equals(shortFileId)) {
				found = current;
				break;
			}
		}
		return found;
	}

}
