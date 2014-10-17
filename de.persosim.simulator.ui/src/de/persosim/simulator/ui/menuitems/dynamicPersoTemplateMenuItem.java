package de.persosim.simulator.ui.menuitems;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
//import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.framework.Bundle;

/**
 * This class implements a dynamically created menu item for loading personalizations from a default set.
 * 
 * @author slutters
 *
 */
public class dynamicPersoTemplateMenuItem {
	
	public static final String PERSO_PATH = "personalization/profiles/";
	
//	@Inject protected ECommandService eCommandService;
	@Inject protected MApplication app;
	@Inject protected EModelService eModelService;
	
	@AboutToShow
	public void aboutToShow(List<MMenuElement> menuItems) {
	    MMenu dynamicMenuItem = MMenuFactory.INSTANCE.createMenu();
	    
	    dynamicMenuItem.setLabel("Dynamic Menu Item (" + new Date() + ")"); 
	    
	    menuItems.add(dynamicMenuItem);
	    
	    populateMenu(dynamicMenuItem);
	    
	}
	
	/**
	 * This method constructs the dynamic menu hierarchy for the loading of default personalizations.
	 * The menu starting at the level of the provided {@link MMenu} object lists all files and folders to be found starting from the base directory specified by {@link #PERSO_PATH}.
	 * @param parentMenu the menu base for adding sub menus to
	 */
	public void populateMenu(MMenu parentMenu) {
		try {
			Bundle plugin = Platform.getBundle("de.persosim.simulator");
			URL url = plugin.getEntry (PERSO_PATH);
			System.out.println("plugin URL for selected path is: " + url);
			URL resolvedUrl = FileLocator.resolve(url);
			System.out.println("resolved absolute URL for selected path is: " + resolvedUrl);
			URI resolvedUri = resolvedUrl.toURI();
			System.out.println("resolved absolute URI for selected path is: " + resolvedUri);
			
			File folder = new File(resolvedUri);
			
			if (folder.isDirectory()) {
	            populateMenu(parentMenu, folder);
	        }
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method returns the first object of type {@link MCommand} among all commands registered with this application to match the provided element id String.
	 * @param elementId the element id to match for
	 * @return the matching {@link MCommand} object or null if none
	 */
	public MCommand getCommand(String elementId) {
		List<MCommand> commands = app.getCommands();
		
		for(MCommand command : commands) {
			if(command.getElementId().equals(elementId)) {
				return command;
			}
		}
		
		return null;
	}
	
	public void populateMenu(MMenu parentMenu, File folder) {
		if (!folder.isDirectory()) {throw new IllegalArgumentException("provided File object must be a folder");}
		
//		MCommand command = (MCommand) eModelService.find("de.persosim.simulator.ui.command.selectPersoFromTemplateCommand", app);
		MCommand command = getCommand("de.persosim.simulator.ui.command.selectPersoFromTemplateCommand");
		
		List<MMenuElement> parentMenuItems = parentMenu.getChildren();
		
		File[] files = folder.listFiles();
		Arrays.sort(files);
		
		for (File fileEntry : files) {
	        if (fileEntry.isDirectory()) {
	        	MMenu dynamicFolderMenuItem = MMenuFactory.INSTANCE.createMenu();
	    	    dynamicFolderMenuItem.setLabel(fileEntry.getName());
	    	    parentMenuItems.add(dynamicFolderMenuItem);
	            populateMenu(dynamicFolderMenuItem, fileEntry);
	        } else {
	        	MHandledMenuItem dynamicFileMenuItem = MMenuFactory.INSTANCE.createHandledMenuItem();
	        	dynamicFileMenuItem.setLabel(fileEntry.getName());
	    	    parentMenuItems.add(dynamicFileMenuItem);
	        	
	    	    String absolutePath = fileEntry.getAbsolutePath();
	    	    System.out.println("absolute path: " + absolutePath);
	    	    
	    	    dynamicFileMenuItem.setCommand(command);
	    	    
	    	    
	    	    MParameter parameter = MCommandsFactory.INSTANCE.createParameter();
				parameter.setName("de.persosim.simulator.ui.commandparameter.persoSet");
				parameter.setValue(absolutePath);
				dynamicFileMenuItem.getParameters().add(parameter);
	    	    
	        }
	        
	    }
		
	}
	
}
