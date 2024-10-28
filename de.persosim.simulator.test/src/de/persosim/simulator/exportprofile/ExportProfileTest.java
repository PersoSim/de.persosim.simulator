package de.persosim.simulator.exportprofile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.persosim.simulator.exportprofile.OrderedFileList;
import de.persosim.simulator.exportprofile.OrderedKeyList;
import de.persosim.simulator.exportprofile.Profile;
import de.persosim.simulator.exportprofile.ProfileMapper;
import de.persosim.simulator.perso.DefaultPersonalization;
import de.persosim.simulator.test.PersoSimTestCase;

public class ExportProfileTest extends PersoSimTestCase
{

	@Test
	@Ignore
	public void testSimpleSerialization()
	{
		OrderedFileList orderedFileList = new OrderedFileList();
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG1, "610413024944");
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG6, "66020c00");
		orderedFileList.setContentByShortFileId(OrderedFileList.SFI_DG2, "6203130144");
		orderedFileList.setContentByShortFileId(OrderedFileList.SFI_DG10, "DG10content");

		OrderedKeyList orderedKeyList = new OrderedKeyList();
		orderedKeyList.setContentById(OrderedKeyList.ID_RI_1_SPERRMERKMAL, "RI 1 content - Sperrmerkmal");
		orderedKeyList.setContentById(OrderedKeyList.ID_RI_2_PSEUDONYM, "RI 1 content - Pseudonym");

		String trustpoint = "7F21...";
		String pin = "123456";
		String can = "500540";
		String puk = "9876543210";
		boolean pinEnabled = true;
		int pinRetryCounter = 3;
		int pinResetCounter = -1;

		Profile profile = new Profile(orderedFileList.getOrderedFiles(), orderedKeyList.getOrderedKeys(), trustpoint, pin, can, puk, pinEnabled, pinRetryCounter, pinResetCounter);

		try
		{
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonSerialized = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(profile);
			// String jsonSerialized = objectMapper.writer(new
			// ProfilePrettyPrinter()).writeValueAsString(profile);
			System.out.println(jsonSerialized);

			Profile profileDeserialized = objectMapper.readValue(jsonSerialized, Profile.class);
			assertEquals(profile.getPin(), profileDeserialized.getPin());
		}
		catch (JsonProcessingException e)
		{
			e.printStackTrace();
		}
	}


	@Test
	public void testPersoSerialization()
	{
		// AbstractProfile perso = new Profile05();
		for (int i = 0; i <= 19; i++)
		{
			DefaultPersonalization perso = (DefaultPersonalization) de.persosim.editor.ui.launcher.Persos.getPerso(i);
			Profile profile = new ProfileMapper().mapPersoToExportProfile(perso);
			String jsonSerialized = profile.serialize();
			System.out.println(jsonSerialized);

			Profile profileDeserialized = Profile.deserialize(jsonSerialized);
			assertNotNull(profileDeserialized);
			assertEquals(profile.getPin(), profileDeserialized.getPin());

			try
			{
				String path = "tmp/profile_export/";
				Files.createDirectories(Path.of(path));
				Files.write(Path.of(path + perso.getClass().getSimpleName() + ".json"), jsonSerialized.getBytes(StandardCharsets.UTF_8));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

}
