package de.persosim.simulator.ui.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UISynchronize;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

import de.persosim.simulator.ui.Activator;
import de.persosim.simulator.ui.utils.LinkedListLogListener;

/**
 * @author slutters
 *
 */
public class PersoSimGuiMain {
	
	public static final int LOG_LIMIT = 1000;
	
	// get UISynchronize injected as field
	@Inject UISynchronize sync;
	
	private Text txtInput, txtOutput;
	
	
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
		
		//configure console field		
		txtOutput = new Text(parent, SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL | SWT.MULTI);
		
		final LinkedListLogListener listener = Activator.getListLogListener();
		if (listener != null){
			listener.addFilter("de.persosim.simulator");	
		} else {
			txtOutput.setText("The OSGi logging service can not be used.\nPlease check the availability and OSGi configuration" + System.lineSeparator());
		}
		
		txtOutput.setEditable(false);
		txtOutput.setCursor(null);
		txtOutput.setLayoutData(new GridData(GridData.FILL_BOTH));
		txtOutput.setSelection(txtOutput.getText().length());
		txtOutput.setTopIndex(txtOutput.getLineCount() - 1);
		
		//configure the slider
		slider = new Slider(parent, SWT.V_SCROLL);
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
		
		txtOutput.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseScrolled(MouseEvent e) {
				int count = e.count;
				slider.setSelection(slider.getSelection()-count);
				
				buildNewConsoleContent();					
			}
		});
		
		parent.setLayout(new GridLayout(2, false));		
		
		txtInput = new Text(parent, SWT.BORDER);
		txtInput.setMessage("Enter command here");
		
		txtInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if((e.character == SWT.CR) || (e.character == SWT.LF)) {
					String line = txtInput.getText();
					
					Activator.getSim().executeUserCommands(line);
					
					txtInput.setText("");
				}
			}
		});

		txtInput.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
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
		updateThread.setDaemon(true);
	    updateThread.start();
		
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
		
	@PreDestroy
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
	
}
