package de.persosim.simulator.ui.parts;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

import de.persosim.driver.connector.service.NativeDriverConnectorInterface;
import de.persosim.simulator.Simulator;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.perso.PersonalizationFactory;
import de.persosim.simulator.ui.Activator;
import de.persosim.simulator.ui.handlers.SelectPersoFromFileHandler;
import de.persosim.simulator.ui.utils.LinkedListLogListener;

/**
 * @author slutters
 *
 */
public class PersoSimGuiMain {
	
	public static final String DE_PERSOSIM_SIMULATOR_BUNDLE = "de.persosim.simulator";
	public static final String PERSO_PATH = "personalization/profiles/";
	public static final String PERSO_FILE = "Profile01.xml";
	
	public static final int LOG_LIMIT = 1000;
	
	// get UISynchronize injected as field
	@Inject UISynchronize sync;
	
	private Text txtOutput;
	
	
	//maximum amount of strings saved in the buffer
	public static final int MAXIMUM_CACHED_CONSOLE_LINES = 2000;
	
	//maximum of lines the text field can show
	int maxLineCount=0;
	
	
	Composite parent;
	private Button lockScroller;
	Boolean locked = false;
	Slider slider;
	
	@PostConstruct
	public void createComposite(Composite parentComposite) {
		parent = parentComposite;
		parent.setLayout(new GridLayout(2, false));
		
		//add console out
		txtOutput = createConsoleOut(parent);
		addConsoleOutMenu(txtOutput);
		
		//configure the slider
		slider = createSlider(parent);
		
		txtOutput.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseScrolled(MouseEvent e) {
				int count = e.count;
				slider.setSelection(slider.getSelection()-count);
				
				buildNewConsoleContent();					
			}
		});
		
		parent.setLayout(new GridLayout(2, false));		
		
		createConsoleIn(parent);
		
		lockScroller = new Button(parent, SWT.TOGGLE);
		lockScroller.setText(" lock ");
		lockScroller.addListener(SWT.Selection, new Listener() {	
			@Override
			public void handleEvent (Event e) {
				if(locked){
					lockScroller.setText(" lock ");
					locked=false;
				}else{
					lockScroller.setText("unlock");
					locked=true;
				}
			}
		});

		Thread updateThread = createUpdateThread();
		updateThread.setDaemon(true);
	    updateThread.start();
	    
		connectToSimulator();
	}
	
	private Thread createUpdateThread() {
		final LinkedListLogListener listener = Activator.getListLogListener();
		if (listener == null){
			txtOutput.setText("The OSGi logging service can not be used.\nPlease check the availability and OSGi configuration" + System.lineSeparator());
		}
		
		final Thread uiThread = Display.getCurrent().getThread();
		
		Thread updateThread = new Thread() {
			public void run() {				
				
				while (uiThread.isAlive()) {					
					sync.syncExec(new Runnable() {

						@Override
						public void run() {
							
							 if(listener.isRefreshNeeded()) {
								 listener.resetRefreshState();
								 buildNewConsoleContent();
								 showNewOutput();
							 }
						}
					});
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// sleep interrupted, doesn't matter
					}
				}
			}
		};
		
		return updateThread;
	}
	
	/**
	 * This method handles the connection to the simulator. Its primary task is
	 * to ensure the simulator is up and running when a connection is
	 * initialized. If the simulator is not found to be running a default
	 * personalization is loaded.
	 */
	private void connectToSimulator() {
		Simulator sim = Activator.getSim();
		
		if(sim != null) {
			//ensure at least a default personalization is loaded before connecting
			if(!sim.isRunning()) {
				try {
					Personalization defaultPersonalization = getDefaultPersonalization(); 
					sim.loadPersonalization(defaultPersonalization);
				} catch (IOException e) {
					e.printStackTrace();
					
					MessageDialog.openError(parent.getShell(), "Error", "Failed to automatically load default personalization");
					return;
				}
			}
		}
		
		NativeDriverConnectorInterface connector = Activator.getConnector();
		connectReader(connector);
	}
	
	/**
	 * This method creates the slider to be used in connection with the console output.
	 * @param parentComposite a composite control which will be the parent of the new instance (cannot be null)
	 * @return the slider to be used in connection with the console output
	 */
	private Slider createSlider(Composite parentComposite) {
		Slider slider = new Slider(parentComposite, SWT.V_SCROLL);
		slider.setIncrement(1);
		slider.setPageIncrement(10);
		if (Activator.getListLogListener() != null){
			slider.setMaximum(Activator.getListLogListener().getNumberOfCachedLines()+slider.getThumb());	
		}
		slider.setMinimum(0);
		slider.setLayoutData(new GridData(GridData.FILL_VERTICAL));		
		
		SelectionListener sliderListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				buildNewConsoleContent();

			}
		};

		slider.addSelectionListener(sliderListener);
		
		return slider;
	}
	
	private void addConsoleOutMenu(Text console) {
		Menu consoleMenu = createConsoleMenu(console);
		console.setMenu(consoleMenu);
	}
	
	/**
	 * This method creates the console input.
	 * @param parent a composite control which will be the parent of the new instance (cannot be null)
	 * @return the console input
	 */
	private Text createConsoleIn(Composite parent) {
		final Text txtIn = new Text(parent, SWT.BORDER);
		txtIn.setMessage("Enter command here");
		
		txtIn.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if((e.character == SWT.CR) || (e.character == SWT.LF)) {
					String line = txtIn.getText();
					
					Activator.executeUserCommands(line);
					
					txtIn.setText("");
				}
			}
		});

		txtIn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		return txtIn;
	}
	
	/**
	 * This method creates the console output.
	 * @param compositeParent a composite control which will be the parent of the new instance (cannot be null)
	 * @return the console output
	 */
	private Text createConsoleOut(Composite compositeParent) {
		Text txtOut = new Text(compositeParent, SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL | SWT.MULTI);		
		txtOut.setEditable(false);
		txtOut.setCursor(null);
		txtOut.setLayoutData(new GridData(GridData.FILL_BOTH));
		txtOut.setSelection(txtOut.getText().length());
		txtOut.setTopIndex(txtOut.getLineCount() - 1);
		
		return txtOut;
	}
	
	/**
	 * This method creates the console output menu.
	 * @param controlParent a composite control which will be the parent of the new instance (cannot be null)
	 * @return the console output menu
	 */
	private Menu createConsoleMenu(Control controlParent) {
		final Control controlParentFinal = controlParent;
		
		Menu consoleMenu = new Menu(controlParentFinal);
		
		//configure log level menu
		MenuItem changeLogLevelItem = new MenuItem(consoleMenu, SWT.CASCADE);
		changeLogLevelItem.setText("Configure logLevel");
		
		changeLogLevelItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) { 
				
				LogLevelDialog ld = new LogLevelDialog(null);
				ld.open();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		
		
		//configure load personalization menu
		MenuItem selectPersonalization = new MenuItem(consoleMenu, SWT.CASCADE);
		selectPersonalization.setText("Load Personalization");
		
		selectPersonalization.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) { 
				
				SelectPersoFromFileHandler fileHandler = new SelectPersoFromFileHandler();
				fileHandler.execute(controlParentFinal.getShell());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		
		
		// configure save log menu
		MenuItem saveLogItem = new MenuItem(consoleMenu, SWT.CASCADE);
		saveLogItem.setText("Save log to file");
		saveLogItem.addSelectionListener(new SelectionListener() {
			
			String logFileName;
			File file;
			PrintWriter writer;
			
			@Override
			public void widgetSelected(SelectionEvent e) { 
				
				try{
					logFileName = "PersoSim_" + new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime()) + ".log";
					file = new File(logFileName);
					writer = new PrintWriter(file);
					for (int i = 0; i < Activator.getListLogListener()
							.getNumberOfCachedLines(); i++) {
						writer.write(Activator.getListLogListener().getLine(i)+"\n");
					}
					
					MessageDialog.openInformation(txtOutput.getShell(), "Info", "Logfile written to " + file.getAbsolutePath());
				}catch(IOException ioe){
		            ioe.printStackTrace(); 
		        } finally { 
		            if (writer != null){ 
		                writer.flush(); 
		                writer.close(); 
		            } 
		        } 				

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		return consoleMenu;
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
	
	/**
	 * changes the maximum of the slider and selects the 
	 * new Maximum to display the latest log messages
	 */
	private void rebuildSlider(){
		sync.syncExec(new Runnable() {
			
			@Override
			public void run() {
				if (Activator.getListLogListener() != null) {
					slider.setMaximum(Activator.getListLogListener()
							.getNumberOfCachedLines()
							+ slider.getThumb()
							- maxLineCount + 1);
				}
				slider.setSelection(slider.getMaximum());	
			}
		});
	}
	
	
	/**
	 * takes the selected value from the Slider and prints the fitting messages
	 * from the LinkedList until the Text field is full
	 */
	private void buildNewConsoleContent() {
		if (Activator.getListLogListener() == null){
			// if there is no log listener there is no content to be printed.
			return;
		}

		// clean text field before filling it with the requested data
		final StringBuilder strConsoleStrings = new StringBuilder();

		// calculates how many lines can be shown without cutting
		maxLineCount = ( txtOutput.getBounds().height - txtOutput.getHorizontalBar().getThumbBounds().height ) / txtOutput.getLineHeight();
		
		//synchronized is used to avoid IndexOutOfBoundsExceptions
		synchronized (Activator.getListLogListener()) {
			int listSize = Activator.getListLogListener().getNumberOfCachedLines();

			// value is needed to stop writing in the console when the end in
			// the list is reached
			int linesToShow = maxLineCount;
			linesToShow = listSize - slider.getMaximum() + slider.getThumb();

			// Fill text field with selected data
			for (int i = 0; i < linesToShow; i++) {

				strConsoleStrings.append(Activator.getListLogListener().getLine(slider
						.getSelection() + i));
				strConsoleStrings.append("\n");

			}
		}
		// send the StringBuilder data to the console field
		sync.syncExec(new Runnable() {

			@Override
			public void run() {

				txtOutput.setText(strConsoleStrings.toString());

			}
		});

	}
		
	/**
	 * controls slider selection (auto scrolling)
	 */
	public void showNewOutput() {
		
		sync.syncExec(new Runnable() {
			@Override
			public void run() {
				rebuildSlider();
				slider.setSelection(slider.getMaximum());							
			}
		});

	}
	
	public void cleanUp() {
		System.exit(0);
	}
	
	@Focus
	public void setFocus() {
		txtOutput.setFocus();
	}
	
	@Override
	public String toString() {
		return "OutputHandler here!";
	}
	
	/**
	 * Attach reader to simulator, i.e. connect connector
	 */
	public void connectReader(NativeDriverConnectorInterface connector) {
		try {
			connector.connect("localhost", 5678);
		} catch (IOException e) {
			MessageDialog.openError(parent.getShell(), "Error",
					"Failed to connect to virtual card reader driver!\nTry to restart driver, then re-connect by selecting\ndesired reader type from menu \"Reader Type\".");
		}
	}
	
	/**
	 * Separate reader from simulator, i.e. disconnect connector
	 */
	public void disconnectReader(NativeDriverConnectorInterface connector) {
		if ((connector != null) && (connector.isRunning())) {
			try {
				connector.disconnect();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
				MessageDialog.openError(parent.getShell(), "Error", "Failed to disconnect reader");
			}
		}
	}
	
}
