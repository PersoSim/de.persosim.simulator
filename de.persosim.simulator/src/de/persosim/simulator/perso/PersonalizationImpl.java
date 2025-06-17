package de.persosim.simulator.perso;

import java.util.ArrayList;
import java.util.List;

import de.persosim.simulator.cardobjects.DedicatedFileIdentifier;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.platform.CommandProcessor;
import de.persosim.simulator.platform.IoManager;
import de.persosim.simulator.platform.Layer;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.file.FileProtocol;
import de.persosim.simulator.securemessaging.SecureMessaging;
import de.persosim.simulator.utils.HexString;

public abstract class PersonalizationImpl implements Personalization {

	public static final String AID_MF = "A0000002471003";

	protected List<Layer> layers = null;

	public PersonalizationImpl() {
		buildLayerList();
	}

	@Override
	public List<Layer> getLayerList() {
		return layers;
	}

	/**
	 * (Re)Build the layer list (in {@link #protocols})
	 * <p/>
	 * This method is called from {@link #reset()} and should be implemented at
	 * least in all Subclasses that are used within tests that need to reset the
	 * personalization.
	 */
	protected void buildLayerList() {
		layers = new ArrayList<>();
	}
	
	@Override
	public void initialize() {
		for(Layer layer:layers) {
			layer.initializeForUse();
		}
	}

	/**
	 * Create a default PersonalisationImpl that contains basic features (i.e.
	 * MF and FileManagement)
	 * 
	 * @return new instance
	 */
	public static Personalization createDefault() {
		PersonalizationImpl newPerso = new PersonalizationImpl() {};

		// load IO and SM layers
		newPerso.layers.add(new IoManager());
		newPerso.layers.add(new SecureMessaging());

		// load command processor layer
		CommandProcessor commandProcessor;
		try {
			commandProcessor = new CommandProcessor(getDefaultProtocols(), buildDefaultMf());
		} catch (AccessDeniedException e) {
			throw new IllegalStateException("The creation of the CommandProcessor layer failed.", e);
		}
		commandProcessor.init();
		newPerso.layers.add(commandProcessor);

		return newPerso;
	}

	private static MasterFile buildDefaultMf() {
		return new MasterFile(new FileIdentifier(0x3F00), new DedicatedFileIdentifier(HexString.toByteArray(AID_MF)));
	}

	private static List<Protocol> getDefaultProtocols() {
		List<Protocol>protocols = new ArrayList<>();

		/* load FM protocol */
		FileProtocol fileManagementProtocol = new FileProtocol();
		fileManagementProtocol.init();
		protocols.add(fileManagementProtocol);
		
		return protocols;
	}

}
