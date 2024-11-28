package de.persosim.simulator.perso.export;

import static org.globaltester.logging.BasicLogger.log;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECFieldFp;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.globaltester.cryptoprovider.Crypto;
import org.globaltester.cryptoprovider.bc.CryptoProviderUtil;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.simulator.CommandParser;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.KeyPairObject;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.crypto.StandardizedDomainParameters;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.perso.PersonalizationFactory;
import de.persosim.simulator.platform.CommandProcessor;
import de.persosim.simulator.platform.PersonalizationHelper;
import de.persosim.simulator.preferences.IniPreferenceStoreAccessor;
import de.persosim.simulator.protocols.ri.Ri;
import de.persosim.simulator.protocols.ri.RiOid;
import de.persosim.simulator.utils.HexString;
import jakarta.annotation.Nullable;

public abstract class ProfileHelper
{
	public static final String PERSO_FILES_PARENT_DIR = "profiles";
	public static final String PERSO_FILE_SUFFIX = ".perso";
	public static final String OVERLAY_PROFILES_FILES_ROOT_PATH = "profiles_overlays_root_path";
	public static final String OVERLAY_PROFILES_FILES_PARENT_DIR = "profiles_overlays";
	public static final String OVERLAY_PROFILE_FILE_SUFFIX = ".json";
	public static final String OVERLAY_PROFILES_PREFS_FILE = OVERLAY_PROFILES_FILES_PARENT_DIR + ".preferences";

	public static final String OVERLAY_PROFILES_PREF_CREATE_MISSING_PROFILES_OVERLAYS = "create_missing_profiles_overlays"; // default: true
	public static final String OVERLAY_PROFILES_PREF_OVERLAY_ALL = "overlay_all"; // default: true
	public static final String OVERLAY_PROFILES_PREF_PRETTY_PRINT = "pretty_print"; // default: false

	private static IniPreferenceStoreAccessor preferenceStoreAccessor = null;
	private static Path rootPathPersoFiles = null;
	private static Path rootPathProfileOverlays = null;


	private ProfileHelper()
	{
		// Do nothing
	}

	@Nullable
	public static synchronized IniPreferenceStoreAccessor getPreferenceStoreAccessorInstance()
	{
		if (getRootPathPersoFiles() == null) {
			BasicLogger.log(ProfileHelper.class, "Personalization root path not set", LogLevel.ERROR);
		}
		if (preferenceStoreAccessor == null) {
			preferenceStoreAccessor = new IniPreferenceStoreAccessor(Paths.get(getRootPathPersoFiles().getParent().toAbsolutePath().toString(), OVERLAY_PROFILES_PREFS_FILE));
		}
		return preferenceStoreAccessor;
	}

	public static synchronized void setRootPathPersoFiles(Path rootPathPersoFilesToSet)
	{
		rootPathPersoFiles = rootPathPersoFilesToSet;
		log(ProfileHelper.class, "Personalization root path is '" + rootPathPersoFiles.toAbsolutePath().toString() + "'.", LogLevel.INFO);
		String rootAbsPathPersoFilesAsString = rootPathPersoFiles.toAbsolutePath().toString();
		Path rootPathOverlays = null;
		String rootPathOverlaysCfg = getPreferenceStoreAccessorInstance().get(ProfileHelper.OVERLAY_PROFILES_FILES_ROOT_PATH);
		if (rootPathOverlaysCfg != null) {
			rootPathOverlaysCfg = rootPathOverlaysCfg.trim();
			if (rootPathOverlaysCfg.contains("$HOME")) {
				String userHome = System.getProperty("user.home");
				if (userHome == null)
					BasicLogger.log(ProfileHelper.class, "$HOME configured, but not set (user.home = null)", LogLevel.ERROR);
				rootPathOverlaysCfg = rootPathOverlaysCfg.replace("$HOME", userHome);
			}
			rootPathOverlays = Path.of(rootPathOverlaysCfg);
			try {
				Files.createDirectories(rootPathOverlays);
			}
			catch (IOException e) {
				BasicLogger.logException(ProfileHelper.class, e);
			}

			if (Files.exists(rootPathOverlays) && Files.isDirectory(rootPathOverlays)) {
				log(ProfileHelper.class, "Configured Overlays Profiles root path is '" + rootPathOverlays + "'.", LogLevel.INFO);
				rootPathProfileOverlays = rootPathOverlays;
				return;
			}
			else {
				log(ProfileHelper.class, "Configured Overlays Profiles root path '" + rootPathOverlays + "' does not exist or is not a directory. Default path will be used.", LogLevel.WARN);
			}
		}
		rootPathProfileOverlays = Path
				.of(rootAbsPathPersoFilesAsString.substring(0, rootAbsPathPersoFilesAsString.lastIndexOf(ProfileHelper.PERSO_FILES_PARENT_DIR)) + ProfileHelper.OVERLAY_PROFILES_FILES_PARENT_DIR);
		log(ProfileHelper.class, "Overlays Profiles root path is '" + rootPathProfileOverlays + "'.", LogLevel.INFO);
	}

	@Nullable
	public static synchronized Path getRootPathPersoFiles()
	{
		return rootPathPersoFiles;
	}

	@Nullable
	public static synchronized Path getRootPathProfileOverlays()
	{
		return rootPathProfileOverlays;
	}

	@Nullable
	private static KeyPairObject findKeyPairObject(MasterFile masterFile, OidIdentifier oid, Boolean privilegedOnly, Integer primaryId)
	{
		KeyPairObject keyPairObject = null;
		for (CardObject curCardObject : masterFile.findChildren(oid)) {
			if (curCardObject instanceof KeyPairObject curKeyPairObject) {
				boolean curKeyPairObjectPrivilegedOnly = curKeyPairObject.isPrivilegedOnly();
				int curKeyPairObjectPrimaryId = curKeyPairObject.getPrimaryIdentifier().getInteger();
				if ((privilegedOnly == null || (Boolean.TRUE.equals(privilegedOnly) && curKeyPairObjectPrivilegedOnly) || (Boolean.FALSE.equals(privilegedOnly) && !curKeyPairObjectPrivilegedOnly))
						&& (primaryId == null || primaryId.intValue() == curKeyPairObjectPrimaryId)) {
					keyPairObject = curKeyPairObject;
					break;
				}
			}
		}
		return keyPairObject;
	}

	@Nullable
	public static KeyPairObject findKeyPairObjectExt(MasterFile masterFile, OidIdentifier oid, Boolean privilegedOnly, Integer primaryId)
	{
		KeyPairObject keyPairObject = ProfileHelper.findKeyPairObject(masterFile, oid, privilegedOnly, primaryId);
		if (keyPairObject == null)
			keyPairObject = ProfileHelper.findKeyPairObject(masterFile, oid, privilegedOnly, null);
		if (keyPairObject == null) {
			BasicLogger.log(ProfileHelper.class, "KeyPairObject not found", LogLevel.WARN);
			return null;
		}
		return keyPairObject;
	}

	public static void getAllPathsRecursively(Path currentPath, List<Path> allFiles) throws IOException
	{
		if (!Files.exists(currentPath))
			return;
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentPath)) {
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					getAllPathsRecursively(entry, allFiles);
				}
				else {
					allFiles.add(entry);
				}
			}
		}
	}

	public static void deleteDirectory(String rootDirPathToDelete) throws IOException
	{
		if (!Files.exists(Path.of(rootDirPathToDelete)))
			return;
		try (var dirStream = Files.walk(Paths.get(rootDirPathToDelete))) {
			dirStream.map(Path::toFile).sorted(Comparator.reverseOrder()).forEach(java.io.File::delete);
		}
	}


	@Nullable
	private static KeyPairObject updateKeyPairObject(MasterFile masterFile, OidIdentifier oid, Boolean privilegedOnly, Integer primaryId)
	{
		KeyPairObject foundKeyPairObject = ProfileHelper.findKeyPairObjectExt(masterFile, oid, privilegedOnly, primaryId);
		if (foundKeyPairObject == null) {
			return null;
		}

		KeyPair newKeyPair = null;
		try {
			newKeyPair = generateNewECKeyPair();
		}
		catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
			BasicLogger.logException(ProfileHelper.class, e);
		}
		try {
			if (newKeyPair != null)
				foundKeyPairObject.setKeyPair(newKeyPair);
		}
		catch (AccessDeniedException e) {
			BasicLogger.logException(ProfileHelper.class, e);
		}

		return foundKeyPairObject;
	}

	public static void updateKeyPairObjectsFromOverlayProfile(MasterFile masterFile, List<Key> keysFromOverlayProfile)
	{
		for (Key currentKey : keysFromOverlayProfile) {
			KeyPairObject foundKeyPairObject = ProfileHelper.findKeyPairObjectExt(masterFile, new OidIdentifier(currentKey.getOidInternal()), currentKey.getPrivilegedOnly(), currentKey.getId());
			if (foundKeyPairObject == null) {
				return;
			}

			PrivateKey privateKey = null;
			try {
				String keyHex = currentKey.getContent();
				byte[] privateKeyRaw = HexString.toByteArray(keyHex);
				KeyFactory keyFactory = KeyFactory.getInstance("EC", Crypto.getCryptoProvider());
				PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyRaw);
				privateKey = keyFactory.generatePrivate(keySpec);
			}
			catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				BasicLogger.logException(ProfileHelper.class, "Cannot generate new private key.", e, LogLevel.ERROR);
				return;
			}

			PublicKey publicKey = null;
			try {
				publicKey = CryptoProviderUtil.convertECPublicKeyFromECPrivateKey(privateKey);
			}
			catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				BasicLogger.logException(ProfileHelper.class, "Cannot generate new public key.", e, LogLevel.ERROR);
				return;
			}
			KeyPair keyPair = new KeyPair(publicKey, privateKey);
			try {
				foundKeyPairObject.setKeyPair(keyPair);
			}
			catch (AccessDeniedException e) {
				BasicLogger.logException(ProfileHelper.class, "Cannot generate new key pair.", e, LogLevel.ERROR);
				return;
			}
		}
	}

	public static String getDumpPublicKey(KeyPairObject keyPairObject)
	{
		ECPublicKey pubKey = (ECPublicKey) keyPairObject.getKeyPair().getPublic();
		int referenceLength = CryptoUtil.getPublicPointReferenceLengthL(((ECFieldFp) pubKey.getParams().getCurve().getField()).getP());
		byte[] pubKeyBytes = CryptoUtil.encode(pubKey.getW(), referenceLength, CryptoUtil.ENCODING_UNCOMPRESSED);
		return HexString.dump(pubKeyBytes);
	}

	public static String getDumpPrivateKey(KeyPairObject keyPairObject)
	{
		ECPrivateKey privKey = (ECPrivateKey) keyPairObject.getKeyPair().getPrivate();
		byte[] privKeyBytes = privKey.getS().toByteArray();
		return HexString.dump(privKeyBytes);
	}

	public static void dumpKeyPairObjectPersoSimEditor(KeyPairObject keyPairObject, String keyPairSuffixToLog)
	{
		BasicLogger.log(ProfileHelper.class, "Public Key " + keyPairSuffixToLog + ":\n" + getDumpPublicKey(keyPairObject), LogLevel.DEBUG);
		BasicLogger.log(ProfileHelper.class, "Private Key " + keyPairSuffixToLog + ":\n" + getDumpPrivateKey(keyPairObject), LogLevel.DEBUG);
	}

	private static KeyPair generateNewECKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException
	{
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDH");
		AlgorithmParameterSpec params = StandardizedDomainParameters.getDomainParameterSetById(13).getAlgorithmParameterSpec();
		kpg.initialize(params);
		return kpg.generateKeyPair();
	}

	// private static Personalization createPersoFromSimpleClassName(String simpleClassName)
	// {
	// try {
	// Class<? extends Personalization> profileClass = Class.forName("de.persosim.simulator.perso." + simpleClassName).asSubclass(DefaultPersonalization.class);
	// Constructor<?> constructor = profileClass.getConstructors()[0];
	// Personalization perso2 = (Personalization) constructor.newInstance();
	// BasicLogger.log(profileClass.getName(), LogLevel.DEBUG);
	// }
	// catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
	// BasicLogger.logException(ProfileHelper.class, e);
	// }
	// }

	// Overlay profile with old existing RI keys
	private static String overlayProfileWithNewRIKeys(Personalization perso)
	{
		MasterFile masterFile = PersonalizationHelper.getUniqueCompatibleLayer(perso.getLayerList(), CommandProcessor.class).getMasterFile();

		// Create new RI keys and update overlay profile
		updateKeyPairObject(masterFile, new OidIdentifier(new RiOid(Ri.id_RI_ECDH_SHA_256)), Boolean.FALSE, Integer.valueOf(OrderedKeyList.ID_RI_1_SPERRMERKMAL));
		updateKeyPairObject(masterFile, new OidIdentifier(new RiOid(Ri.id_RI_ECDH_SHA_256)), Boolean.TRUE, Integer.valueOf(OrderedKeyList.ID_RI_2_PSEUDONYM));

		OverlayProfile overlayProfile = new ProfileMapper().mapPersoToOverlayProfile(perso);
		boolean prettyPrint = false;
		String prettyPrintCfg = ProfileHelper.getPreferenceStoreAccessorInstance().get(ProfileHelper.OVERLAY_PROFILES_PREF_PRETTY_PRINT);
		if (prettyPrintCfg != null && ("true".equalsIgnoreCase(prettyPrintCfg.trim()) || "yes".equalsIgnoreCase(prettyPrintCfg.trim())))
			prettyPrint = true;
		return overlayProfile.serialize(prettyPrint);
	}


	public static void createMissingOverlayProfileFile(String persoFileAbsFilePath, String overlayFileAbsFilePath)
	{
		Personalization perso = CommandParser.getPerso(persoFileAbsFilePath);
		String jsonSerialized = overlayProfileWithNewRIKeys(perso);
		try {
			Files.write(Path.of(overlayFileAbsFilePath), jsonSerialized.getBytes(StandardCharsets.UTF_8));
		}
		catch (IOException e) {
			BasicLogger.logException(ProfileHelper.class, e);
		}

	}

	private static String createMissingOverlayProfileDirectories(String pathPersoAbsFilePath, String pathPersoFileName)
	{
		String pathAfterRootPathProfiles = pathPersoAbsFilePath.substring(pathPersoAbsFilePath.lastIndexOf(PERSO_FILES_PARENT_DIR) + PERSO_FILES_PARENT_DIR.length() + java.io.File.separator.length());
		int pathAfterRootPathProfilesLength = pathAfterRootPathProfiles.length() - pathPersoFileName.length();

		// Create related overlays directories (if not available)
		String overlayPath = rootPathProfileOverlays.toAbsolutePath().toString();
		String pathAfterRootPathProfilesWithoutFileName = "";
		if (pathAfterRootPathProfilesLength > 0) {
			pathAfterRootPathProfilesWithoutFileName = pathAfterRootPathProfiles.substring(0, pathAfterRootPathProfilesLength - java.io.File.separator.length());
			overlayPath = overlayPath + java.io.File.separator;
		}
		try {
			Files.createDirectories(Path.of(overlayPath + pathAfterRootPathProfilesWithoutFileName));
		}
		catch (IOException e) {
			BasicLogger.logException(ProfileHelper.class, e);
		}
		return pathAfterRootPathProfilesWithoutFileName;
	}

	public static String getOverlayFilePath(Path pathPerso)
	{
		String pathPersoAbsFilePath = pathPerso.toAbsolutePath().toString();
		String pathPersoFileName = pathPerso.getFileName().toString();
		String pathAfterRootPathPersosWithoutFileName = ProfileHelper.createMissingOverlayProfileDirectories(pathPersoAbsFilePath, pathPersoFileName);

		String currentFileNameWithoutSuffix = pathPersoFileName.substring(0, pathPersoFileName.lastIndexOf(ProfileHelper.PERSO_FILE_SUFFIX));
		String currentFileNameOverlay = currentFileNameWithoutSuffix + ProfileHelper.OVERLAY_PROFILE_FILE_SUFFIX;
		if (pathAfterRootPathPersosWithoutFileName != null && !pathAfterRootPathPersosWithoutFileName.isEmpty())
			pathAfterRootPathPersosWithoutFileName = pathAfterRootPathPersosWithoutFileName + File.separator;
		return rootPathProfileOverlays.toAbsolutePath().toString() + File.separator + pathAfterRootPathPersosWithoutFileName + currentFileNameOverlay;
	}


	public static void createAllMissingOverlayProfileFiles(Path rootPathProfiles)
	{
		IniPreferenceStoreAccessor preferenceStoreAccessor = ProfileHelper.getPreferenceStoreAccessorInstance();
		String createMissing = preferenceStoreAccessor.get(OVERLAY_PROFILES_PREF_CREATE_MISSING_PROFILES_OVERLAYS);
		if (createMissing != null && ("false".equalsIgnoreCase(createMissing.trim()) || "no".equalsIgnoreCase(createMissing.trim()))) {
			BasicLogger.log(ProfileHelper.class, "Creating of Profiles Overlays is disabled. Missing Profiles Overlays files will NOT be created.", LogLevel.INFO);
			return;
		}

		log(ProfileHelper.class, "Personalization root path is '" + rootPathProfiles.toString() + "'.", LogLevel.DEBUG);
		List<Path> allProfileFilePaths = new ArrayList<>();
		try {
			ProfileHelper.getAllPathsRecursively(rootPathProfiles, allProfileFilePaths);
		}
		catch (IOException e) {
			BasicLogger.logException(ProfileHelper.class, e);
		}

		String rootPathOverlays = rootPathProfileOverlays.toAbsolutePath().toString();
		log(ProfileHelper.class, "Overlays Profiles root path is '" + rootPathOverlays + "'.", LogLevel.DEBUG);
		for (Path currentPath : allProfileFilePaths) {
			String currentAbsFilePath = currentPath.toAbsolutePath().toString();
			String currentAbsFilePathOverlay = ProfileHelper.getOverlayFilePath(currentPath);
			if (!Files.exists(Path.of(currentAbsFilePathOverlay))) {
				log(ProfileHelper.class, "Overlay Profile file '" + currentAbsFilePathOverlay + "' does not exist and will be created.", LogLevel.INFO);
				ProfileHelper.createMissingOverlayProfileFile(currentAbsFilePath, currentAbsFilePathOverlay);
				log(ProfileHelper.class, "Overlay Profile file '" + currentAbsFilePathOverlay + "' created successfully.", LogLevel.INFO);
			}
			else {
				log(ProfileHelper.class, "Overlay Profile file '" + currentAbsFilePathOverlay + "' already exists.", LogLevel.DEBUG);
			}
		}
	}


	@Nullable
	public static String getOverlayProfileFilePathForPerso(Personalization perso)
	{
		String overlayProfileFilePath = null;
		List<Path> allOverlayProfileFilePaths = new ArrayList<>();
		try {
			ProfileHelper.getAllPathsRecursively(rootPathProfileOverlays, allOverlayProfileFilePaths);
		}
		catch (IOException e) {
			BasicLogger.logException(ProfileHelper.class, e);
		}
		String persoFileName = perso.getClass().getSimpleName() + OVERLAY_PROFILE_FILE_SUFFIX;
		if ("DefaultPerso.json".equals(persoFileName))
			persoFileName = "DefaultPersoGt.json"; // special editor case
		for (Path current : allOverlayProfileFilePaths) {
			String currentAsString = current.toString();
			if (currentAsString.endsWith(persoFileName)) {
				overlayProfileFilePath = currentAsString;
				log(ProfileHelper.class, "Found Overlay Profile file for '" + perso.getClass().getSimpleName() + "' : '" + overlayProfileFilePath + "'.", LogLevel.DEBUG);
				break;
			}
		}
		if (overlayProfileFilePath == null)
			log(ProfileHelper.class, "Found no Overlay Profile file for '" + perso.getClass().getSimpleName() + " in root path: '" + getRootPathProfileOverlays().toString() + "'.", LogLevel.DEBUG);
		return overlayProfileFilePath;
	}

	private static void handleOverlayProfile(Path pathPerso)
	{
		if (!Files.exists(pathPerso)) {
			throw new IllegalArgumentException("Personalization file '" + pathPerso.toAbsolutePath().toString() + "' does not exist.");
		}
		if (!Files.exists(rootPathProfileOverlays)) {
			throw new IllegalArgumentException("Root path of Overlay Profile files '" + rootPathProfileOverlays.toAbsolutePath().toString() + "' does not exist.");
		}
		Personalization perso;
		try {
			perso = readPersoFromFile(pathPerso.toAbsolutePath().toString());
		}
		catch (IOException e) {
			return;
		}
		handleOverlayProfile(perso);
	}

	public static void handleOverlayProfile(Personalization perso)
	{
		IniPreferenceStoreAccessor preferenceStoreAccessor = ProfileHelper.getPreferenceStoreAccessorInstance();
		String createMissing = preferenceStoreAccessor.get(OVERLAY_PROFILES_PREF_CREATE_MISSING_PROFILES_OVERLAYS);
		if (createMissing != null && ("false".equalsIgnoreCase(createMissing.trim()) || "no".equalsIgnoreCase(createMissing.trim()))) {
			BasicLogger.log(ProfileHelper.class, "Creating of Profiles Overlays is disabled. Profile '" + perso.getClass().getSimpleName() + "' will NOT be overlaid.", LogLevel.INFO);
			return;
		}
		// preferenceStoreAccessor.set(OVERLAY_PROFILES_PREF_OVERLAY_ALL, "true");
		String overlayAll = preferenceStoreAccessor.get(OVERLAY_PROFILES_PREF_OVERLAY_ALL);
		if (overlayAll != null && ("false".equalsIgnoreCase(overlayAll.trim()) || "no".equalsIgnoreCase(overlayAll.trim()))) {
			BasicLogger.log(ProfileHelper.class, "Profiles Overlaying is disabled. Profile '" + perso.getClass().getSimpleName() + "' will NOT be overlaid.", LogLevel.INFO);
			return;
		}

		BasicLogger.log(ProfileHelper.class, "Profile '" + perso.getClass().getSimpleName() + "' will be overlaid.", LogLevel.DEBUG);

		OverlayProfile overlayProfile = getOverlayProfileForPerso(perso, false);
		if (overlayProfile == null)
			return;
		MasterFile masterFile = PersonalizationHelper.getUniqueCompatibleLayer(perso.getLayerList(), CommandProcessor.class).getMasterFile();
		ProfileHelper.updateKeyPairObjectsFromOverlayProfile(masterFile, overlayProfile.getKeys());
		BasicLogger.log(ProfileHelper.class, "Profile '" + perso.getClass().getSimpleName() + "' overlaid successfully.", LogLevel.DEBUG);
	}

	@Nullable
	public static OverlayProfile getOverlayProfileForPerso(Personalization perso, boolean hasToExist)
	{
		String overlayProfileFilePath = ProfileHelper.getOverlayProfileFilePathForPerso(perso);
		if (overlayProfileFilePath == null) {
			if (hasToExist)
				BasicLogger.log(ProfileHelper.class, "Cannot get Overlay Profile file path for personalization '" + perso.getClass().getSimpleName() + "'.", LogLevel.ERROR);
			return null;
		}
		return getOverlayProfile(overlayProfileFilePath);
	}

	@Nullable
	public static OverlayProfile getOverlayProfile(String overlayProfileFilePath)
	{
		String overlaySerialized;
		try {
			overlaySerialized = ProfileHelper.readOverlayProfileFromFile(overlayProfileFilePath);
		}
		catch (IOException e) {
			return null;
		}
		return (OverlayProfile) ProfileBase.deserialize(overlaySerialized, OverlayProfile.class);
	}

	public static Personalization readPersoFromFile(String persoFilePath) throws IOException
	{
		try (Reader reader = Files.newBufferedReader(Path.of(persoFilePath))) {
			return (Personalization) PersonalizationFactory.unmarshal(reader);
		}
		catch (IOException e) {
			BasicLogger.logException(ProfileHelper.class, "Reading the personalization file '" + persoFilePath + "' failed.", e, LogLevel.ERROR);
			throw e;
		}
	}

	public static String readOverlayProfileFromFile(String overlayProfileFilePath) throws IOException
	{
		try {
			return Files.readString(Path.of(overlayProfileFilePath));
		}
		catch (IOException e) {
			BasicLogger.logException(ProfileHelper.class, "Reading the Overlay Profile file '" + overlayProfileFilePath + "' failed.", e, LogLevel.ERROR);
			throw e;
		}
	}

}
