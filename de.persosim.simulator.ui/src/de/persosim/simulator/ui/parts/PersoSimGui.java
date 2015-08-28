package de.persosim.simulator.ui.parts;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;

import de.persosim.simulator.PersoSim;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.perso.PersonalizationFactory;
import de.persosim.simulator.ui.Activator;

/**
 * PersoSim view implementation used within Persosim RCP, handles some
 * configuration issues that are currently not handled at the appropriate
 * places.
 * 
 * @see PersoSimGuiMain
 * @author jgoeke
 *
 */
public class PersoSimGui extends PersoSimGuiMain{
	
	@Override
	@PostConstruct
	public void createComposite(Composite parentComposite) {
		super.createComposite(parentComposite);;
		getSimAndConnectToNativeDriver();
	}
	
	/**
	 * This method handles the connection to the simulator. Its primary task is
	 * to ensure the simulator is up and running when a connection is
	 * initialized. If the simulator is not found to be running a default FIXME JGE the implementation is NOT conditional!
	 * personalization is loaded.
	 */
	//IMPL loading the default personalisation shall be moved to the product defining plug-in
	private void getSimAndConnectToNativeDriver() {
		    de.persosim.simulator.Activator persoSimActivator = de.persosim.simulator.Activator.getDefault();
		    PersoSim sim = persoSimActivator.getSim();
		    try {
		    	sim.startSimulator();
				sim.loadPersonalization(getDefaultPersonalization());
			} catch (IOException e) {
				e.printStackTrace();
				MessageDialog.openError(parent.getShell(), "Error", "Failed to automatically load default personalization");
				return;
			}
		    Activator.connectToNativeDriver();
	}
	
	/**
	 * This method returns a personalization which can be used as default.
	 * @return a default personalization
	 * @throws IOException
	 */
	private Personalization getDefaultPersonalization() throws IOException {
		Bundle plugin = Platform.getBundle(DE_PERSOSIM_SIMULATOR_BUNDLE);
		URL url = plugin.getEntry (PERSO_PATH);
		URL resolvedUrl;
		
		resolvedUrl = FileLocator.resolve(url);
		
		File folder = new File(resolvedUrl.getFile());
		String pathString = folder.getAbsolutePath() + File.separator + PERSO_FILE;
		
		System.out.println("Loading default personalization from: " + pathString);
		
		Personalization personalization = (Personalization) PersonalizationFactory.unmarshal(pathString);
		
		return personalization;
	}
	
	//IMPL this code just hides an InterrupotedException when closing the RCP and thus should be removed (when the Exception is handled appropriately)
	@PreDestroy
	public void cleanUp() {
		System.exit(0);
	}


}
