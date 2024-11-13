package de.persosim.simulator.perso.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.persosim.simulator.cardobjects.KeyPairObject;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.perso.DefaultPersonalization;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.platform.CommandProcessor;
import de.persosim.simulator.platform.PersonalizationHelper;
import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.ri.Ri;
import de.persosim.simulator.protocols.ri.RiOid;
import de.persosim.simulator.test.PersoSimTestCase;
import de.persosim.simulator.utils.HexString;

public class PersoExportTest extends PersoSimTestCase
{
	// Current dir/$PWD: ".../repos/de.persosim.simulator/de.persosim.simulator.test/"
	// *.perso files: $PWD + "../de.persosim.simulator/personalization/profiles" + subdirs
	private static Path pathProfiles = Path.of("../de.persosim.simulator/personalization/profiles");
	private static String rootPathProfiles = pathProfiles.toAbsolutePath().toString();
	private static String rootPathOverlays = rootPathProfiles.substring(0, rootPathProfiles.lastIndexOf(ProfileHelper.PERSO_FILES_PARENT_DIR)) + ProfileHelper.OVERLAY_PROFILES_FILES_PARENT_DIR;


	@BeforeClass
	public static void beforeTests() throws IOException
	{
		ProfileHelper.setRootPathPersoFiles(pathProfiles);
		ProfileHelper.deleteDirectory(rootPathOverlays);
	}

	@AfterClass
	public static void afterTests() throws IOException
	{
		ProfileHelper.deleteDirectory(rootPathOverlays);
	}

	@Test
	public void testSimpleSerialization()
	{
		OrderedFileList orderedFileList = new OrderedFileList();
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG1, "610413024944");
		orderedFileList.setContentByFileId(OrderedFileList.FID_DG6, "66020c00");
		orderedFileList.setContentByShortFileId(OrderedFileList.SFI_DG2, "6203130144");
		orderedFileList.setContentByShortFileId(OrderedFileList.SFI_DG10, "DG10content");

		OrderedKeyList orderedKeyList = new OrderedKeyList();
		orderedKeyList.setContent((GenericOid) new OidIdentifier(new RiOid(Ri.id_RI_ECDH_SHA_256)).getOid(), Boolean.FALSE, Integer.valueOf(OrderedKeyList.ID_RI_1_SPERRMERKMAL),
				"RI 1 content - Sperrmerkmal");
		orderedKeyList.setContent((GenericOid) new OidIdentifier(new RiOid(Ri.id_RI_ECDH_SHA_256)).getOid(), Boolean.TRUE, Integer.valueOf(OrderedKeyList.ID_RI_2_PSEUDONYM),
				"RI 1 content - Pseudonym");

		String trustpoint = "7F21...";
		String pin = "123456";
		String can = "500540";
		String puk = "9876543210";
		boolean pinEnabled = true;
		int pinRetryCounter = 3;
		int pinResetCounter = -1;

		Profile profile = new Profile(orderedFileList.getOrderedFiles(), orderedKeyList.getOrderedKeys(), trustpoint, pin, can, puk, pinEnabled, pinRetryCounter, pinResetCounter);

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonSerialized = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(profile);
			// String jsonSerialized = objectMapper.writer(new
			// ProfilePrettyPrinter()).writeValueAsString(profile);
			BasicLogger.log(this, jsonSerialized, LogLevel.TRACE);

			Profile profileDeserialized = objectMapper.readValue(jsonSerialized, Profile.class);
			assertEquals(profile.getPin(), profileDeserialized.getPin());
		}
		catch (JsonProcessingException e) {
			BasicLogger.logException(getClass(), e, LogLevel.ERROR);
		}
	}


	@Test
	public void testPersoExportSerialization()
	{
		try {
			doTestCreateNonExistentOverlayProfileFiles(pathProfiles);
		}
		catch (IOException e) {
			BasicLogger.logException(getClass(), e, LogLevel.ERROR);
			fail(e.getMessage());
		}
		for (int i = 0; i <= 19; i++) {
			DefaultPersonalization perso = (DefaultPersonalization) de.persosim.editor.ui.launcher.Persos.getPerso(i);
			Profile profile = new ProfileMapper().mapPersoToExportProfile(perso);
			String jsonSerialized = profile.serialize(true);
			BasicLogger.log(this, "Serialized " + perso.getClass().getSimpleName() + ":", LogLevel.TRACE);
			BasicLogger.log(this, jsonSerialized, LogLevel.TRACE);

			Profile profileDeserialized = (Profile) ProfileBase.deserialize(jsonSerialized, Profile.class);
			assertNotNull(profileDeserialized);
			assertEquals(profile.getPin(), profileDeserialized.getPin());

			try {
				String path = "tmp/perso_export/";
				Files.createDirectories(Path.of(path));
				Files.write(Path.of(path + perso.getClass().getSimpleName() + ProfileHelper.OVERLAY_PROFILE_FILE_SUFFIX), jsonSerialized.getBytes(StandardCharsets.UTF_8));
			}
			catch (IOException e) {
				BasicLogger.logException(getClass(), e, LogLevel.ERROR);
			}
		}
		try {
			ProfileHelper.deleteDirectory(rootPathOverlays);
		}
		catch (IOException e) {
			BasicLogger.logException(getClass(), e, LogLevel.ERROR);
			fail(e.getMessage());
		}
	}


	@Test
	public void testOverlayProfileSerialization()
	{
		try {
			doTestCreateNonExistentOverlayProfileFiles(pathProfiles);
		}
		catch (IOException e) {
			BasicLogger.logException(getClass(), e, LogLevel.ERROR);
			fail(e.getMessage());
		}
		for (int i = 0; i <= 19; i++) {
			DefaultPersonalization perso = (DefaultPersonalization) de.persosim.editor.ui.launcher.Persos.getPerso(i);
			OverlayProfile profile = new ProfileMapper().mapPersoToOverlayProfile(perso);
			String jsonSerialized = profile.serialize(true);
			BasicLogger.log(this, "Serialized " + perso.getClass().getSimpleName() + ":", LogLevel.TRACE);
			BasicLogger.log(this, jsonSerialized, LogLevel.TRACE);

			OverlayProfile profileDeserialized = (OverlayProfile) ProfileBase.deserialize(jsonSerialized, OverlayProfile.class);
			assertNotNull(profileDeserialized);
			assertEquals(profile.getKeys().get(0).getContent(), profileDeserialized.getKeys().get(0).getContent());

			try {
				String path = "tmp/perso_overlay/";
				Files.createDirectories(Path.of(path));
				Files.write(Path.of(path + perso.getClass().getSimpleName() + ProfileHelper.OVERLAY_PROFILE_FILE_SUFFIX), jsonSerialized.getBytes(StandardCharsets.UTF_8));
			}
			catch (IOException e) {
				BasicLogger.logException(getClass(), e, LogLevel.ERROR);
			}
		}
		try {
			ProfileHelper.deleteDirectory(rootPathOverlays);
		}
		catch (IOException e) {
			BasicLogger.logException(getClass(), e, LogLevel.ERROR);
			fail(e.getMessage());
		}
	}


	@Test
	public void testCreateNonExistentOverlayProfileFiles()
	{
		try {
			doTestCreateNonExistentOverlayProfileFiles(pathProfiles);
			doTestCreateNonExistentOverlayProfileFiles(pathProfiles);
		}
		catch (IOException e) {
			BasicLogger.logException(getClass(), e, LogLevel.ERROR);
		}
	}


	private void doTestCreateNonExistentOverlayProfileFiles(Path rootPathProfiles) throws IOException
	{
		List<Path> allProfileFilePaths = new ArrayList<>();
		try {
			ProfileHelper.getAllPathsRecursively(rootPathProfiles, allProfileFilePaths);
		}
		catch (IOException e) {
			BasicLogger.logException(getClass(), e, LogLevel.ERROR);
		}
		for (Path path : allProfileFilePaths) {
			BasicLogger.log(this, "Found *.perso file: '" + path.toAbsolutePath().toString() + "'.", LogLevel.TRACE);
		}
		for (Path currentPersoPath : allProfileFilePaths) {
			// if (!currentPersoPath.endsWith("Profile01.perso"))
			// continue;
			String currentAbsFilePathPerso = currentPersoPath.toAbsolutePath().toString();
			String currentAbsFilePathOverlay = ProfileHelper.getOverlayFilePath(currentPersoPath);
			if (Files.exists(Path.of(currentAbsFilePathOverlay))) {
				BasicLogger.log(this, "Overlay Profile file '" + currentAbsFilePathOverlay + "' already exists -> overlay.", LogLevel.TRACE);
				doTestOverlayProfile(currentAbsFilePathPerso, currentAbsFilePathOverlay);
			}
			else {
				ProfileHelper.createMissingOverlayProfileFile(currentAbsFilePathPerso, currentAbsFilePathOverlay);
			}
		}
	}

	private void doTestOverlayProfile(String absPersoFilePath, String absOverlayFilePath) throws IOException
	{
		Personalization perso = ProfileHelper.readPersoFromFile(absPersoFilePath);
		String overlaySerialized = ProfileHelper.readOverlayProfileFromFile(absOverlayFilePath);

		String overlayProfileFilePathForPerso = ProfileHelper.getOverlayProfileFilePathForPerso(perso);
		assertEquals(absOverlayFilePath, overlayProfileFilePathForPerso);

		OverlayProfile overlayProfile = (OverlayProfile) ProfileBase.deserialize(overlaySerialized, OverlayProfile.class);

		MasterFile masterFile = PersonalizationHelper.getUniqueCompatibleLayer(perso.getLayerList(), CommandProcessor.class).getMasterFile();

		KeyPairObject keyPairObjectRIOld1 = ProfileHelper.findKeyPairObjectExt(masterFile, new OidIdentifier(new RiOid(Ri.id_RI_ECDH_SHA_256)), Boolean.FALSE,
				Integer.valueOf(OrderedKeyList.ID_RI_1_SPERRMERKMAL));
		String privateKeyHexOld1 = HexString.encode(keyPairObjectRIOld1.getKeyPair().getPrivate().getEncoded());
		String publicKeyHexOld1 = HexString.encode(keyPairObjectRIOld1.getKeyPair().getPublic().getEncoded());

		KeyPairObject keyPairObjectRIOld2 = ProfileHelper.findKeyPairObjectExt(masterFile, new OidIdentifier(new RiOid(Ri.id_RI_ECDH_SHA_256)), Boolean.TRUE,
				Integer.valueOf(OrderedKeyList.ID_RI_2_PSEUDONYM));
		String privateKeyHexOld2 = HexString.encode(keyPairObjectRIOld2.getKeyPair().getPrivate().getEncoded());
		String publicKeyHexOld2 = HexString.encode(keyPairObjectRIOld2.getKeyPair().getPublic().getEncoded());

		String dumpPublicKeyRIOld1 = ProfileHelper.getDumpPublicKey(keyPairObjectRIOld1);
		String dumpPrivateKeyRIOld1 = ProfileHelper.getDumpPrivateKey(keyPairObjectRIOld1);
		String dumpPublicKeyRIOld2 = ProfileHelper.getDumpPublicKey(keyPairObjectRIOld2);
		String dumpPrivateKeyRIOld2 = ProfileHelper.getDumpPrivateKey(keyPairObjectRIOld2);
		BasicLogger.log("Public Key Old 1:" + ProfileHelper.getDumpPublicKey(keyPairObjectRIOld1), LogLevel.TRACE);
		BasicLogger.log("Private Key Old 1:" + ProfileHelper.getDumpPrivateKey(keyPairObjectRIOld1), LogLevel.TRACE);

		ProfileHelper.updateKeyPairObjectsFromOverlayProfile(masterFile, overlayProfile.getKeys());

		KeyPairObject keyPairObjectRI1 = ProfileHelper.findKeyPairObjectExt(masterFile, new OidIdentifier(new RiOid(Ri.id_RI_ECDH_SHA_256)), Boolean.FALSE,
				Integer.valueOf(OrderedKeyList.ID_RI_1_SPERRMERKMAL));
		String privateKeyHex1 = HexString.encode(keyPairObjectRI1.getKeyPair().getPrivate().getEncoded());
		String publicKeyHex1 = HexString.encode(keyPairObjectRI1.getKeyPair().getPublic().getEncoded());

		KeyPairObject keyPairObjectRI2 = ProfileHelper.findKeyPairObjectExt(masterFile, new OidIdentifier(new RiOid(Ri.id_RI_ECDH_SHA_256)), Boolean.TRUE,
				Integer.valueOf(OrderedKeyList.ID_RI_2_PSEUDONYM));
		String privateKeyHex2 = HexString.encode(keyPairObjectRI2.getKeyPair().getPrivate().getEncoded());
		String publicKeyHex2 = HexString.encode(keyPairObjectRI2.getKeyPair().getPublic().getEncoded());

		String dumpPublicKeyRI1 = ProfileHelper.getDumpPublicKey(keyPairObjectRI1);
		String dumpPrivateKeyRI1 = ProfileHelper.getDumpPrivateKey(keyPairObjectRI1);
		String dumpPublicKeyRI2 = ProfileHelper.getDumpPublicKey(keyPairObjectRI2);
		String dumpPrivateKeyRI2 = ProfileHelper.getDumpPrivateKey(keyPairObjectRI2);
		// BasicLogger.log("Public Key Temp1:" + ProfileHelper.getDumpPublicKey(keyPairObjectRI1), LogLevel.TRACE);
		// BasicLogger.log("Private Key Temp1:" + ProfileHelper.getDumpPrivateKey(keyPairObjectRI1), LogLevel.TRACE);

		ProfileHelper.dumpKeyPairObjectPersoSimEditor(keyPairObjectRI1, "RI Key New 1");
		ProfileHelper.dumpKeyPairObjectPersoSimEditor(keyPairObjectRI2, "RI Key New 2");

		assertNotEquals(dumpPublicKeyRIOld1, dumpPublicKeyRI1);
		assertNotEquals(dumpPrivateKeyRIOld1, dumpPrivateKeyRI1);
		assertNotEquals(dumpPublicKeyRIOld2, dumpPublicKeyRI2);
		assertNotEquals(dumpPrivateKeyRIOld2, dumpPrivateKeyRI2);

		assertNotEquals(privateKeyHexOld1, privateKeyHex1);
		assertNotEquals(publicKeyHexOld1, publicKeyHex1);
		assertNotEquals(privateKeyHexOld2, privateKeyHex2);
		assertNotEquals(publicKeyHexOld2, publicKeyHex2);
	}

}
