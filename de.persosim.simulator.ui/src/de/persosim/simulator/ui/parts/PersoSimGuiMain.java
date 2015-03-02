package de.persosim.simulator.ui.parts;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;

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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

import de.persosim.simulator.PersoSim;

/**
 * @author slutters
 *
 */
public class PersoSimGuiMain {
	
	public static final int LOG_LIMIT = 1000;
	
	// get UISynchronize injected as field
	@Inject UISynchronize sync;
	
	private Text txtInput, txtOutput;
	
	
	private final InputStream originalSystemIn = System.in;
	private final PrintStream originalSystemOut = System.out;
	
	//Buffer for old console outputs
	private LinkedList<String> consoleStrings = new LinkedList<String>();
	
	//maximum amount of strings saved in the buffer
	private int maxLines = 2000;
	
	//maximum of lines the text field can show
	int maxLineCount=0;
	
	private PrintStream newSystemOut;
	private final PipedInputStream inPipe = new PipedInputStream();
	
	private PrintWriter inWriter;
	
	Composite parent;
	private Button lockScroller;
	Boolean locked = false;
	Slider slider;
	
	@PostConstruct
	public void createComposite(Composite parentComposite) {
		parent = parentComposite;
		grabSysOut();
		grabSysIn();
		
		parent.setLayout(new GridLayout(2, false));
		
		//configure console field
		txtOutput = new Text(parent, SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL | SWT.MULTI);		
		txtOutput.setText("PersoSim GUI" + System.lineSeparator());
		txtOutput.setEditable(false);
		txtOutput.setCursor(null);
		txtOutput.setLayoutData(new GridData(GridData.FILL_BOTH));
		txtOutput.setSelection(txtOutput.getText().length());
		txtOutput.setTopIndex(txtOutput.getLineCount() - 1);
		
		//configure the slider
		slider = new Slider(parent, SWT.V_SCROLL);
		slider.setIncrement(1);
		slider.setPageIncrement(10);
		slider.setMaximum(consoleStrings.size()+slider.getThumb());
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
					
					txtOutput.append(line + System.lineSeparator());
					inWriter.println(line);
					inWriter.flush();
					
					txtInput.setText("");
				}
			}
		});

		txtInput.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		lockScroller = new Button(parent, SWT.TOGGLE);
		lockScroller.setText("unlocked");
		lockScroller.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent (Event e) {
				
				if(locked){
					lockScroller.setText("unlocked");
					locked=false;
				}else{
					lockScroller.setText("locked");
					locked=true;
				}
			}
		});
		
		
		PersoSim sim = new PersoSim();
		sim.loadPersonalization("1"); //load default perso with valid TestPKI EF.CardSec etc. (Profile01)
		Thread simThread = new Thread(sim);
		simThread.start();
		
	}
	
	/**
	 * changes the maximum of the slider
	 */
	private void rebuildSlider(){
		sync.asyncExec(new Runnable() {
			
			@Override
			public void run() {
				slider.setMaximum(consoleStrings.size()+slider.getThumb()-maxLineCount+1);
			}
		});
	}
	
	
	/**
	 * takes the selected value from the Slider and prints the fitting messages
	 * from the LinkedList until the Text field is full
	 */
	private void buildNewConsoleContent() {

		// clean text field before filling it with the requested data
		final StringBuilder strConsoleStrings = new StringBuilder();

		// calculates how many lines can be shown without cutting
		maxLineCount = ( txtOutput.getBounds().height - txtOutput.getHorizontalBar().getThumbBounds().height ) / txtOutput.getLineHeight();
		
		//synchronized is used to avoid IndexOutOfBoundsExceptions 
		synchronized (consoleStrings) {
			int listSize = consoleStrings.size();

			// value is needed to stop writing in the console when the end in
			// the list is reached
			int linesToShow = maxLineCount;
			linesToShow = listSize - slider.getMaximum() + slider.getThumb();

			// Fill text field with selected data
			for (int i = 0; i < linesToShow; i++) {

				strConsoleStrings.append(consoleStrings.get(slider
						.getSelection() + i));

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
	 * This method activates redirection of System.out.
	 */
	private void grabSysOut() {
	    OutputStream out = new OutputStream() {

			char [] buffer = new char [200];
			int currentPosition = 0;
			boolean checkNextForNewline = false;
			
			@Override
			public void write(int b) throws IOException {
				final char value = (char) b;
				
				if (checkNextForNewline){
					checkNextForNewline = false;
					if (value == '\n'){
						return;
					}
				}

				if (currentPosition < buffer.length - 1 && !(value == '\n' || value == '\r')){
					buffer [currentPosition++] = value;
				} else {
					if (value == '\n' || value == '\r'){
						if (value == '\r'){
							checkNextForNewline = true;
						}
						buffer [currentPosition++] = '\n';
					} else {
						buffer [currentPosition++] = value;
					}

					final String toPrint = new String(Arrays.copyOf(buffer, currentPosition));
					originalSystemOut.print(toPrint);
					saveConsoleStrings(toPrint);
					
					
					currentPosition = 0;
				}

			}
		};

		newSystemOut = new PrintStream(out, true);
		
		System.setOut(newSystemOut);
		
		if(newSystemOut != null) {
			originalSystemOut.println("activated redirection of System.out");
		}
	    
	}

	//XXX ensure that this method is called often enough, so that the last updates are correctly reflected
	/**
	 * saves and manages Strings grabbed by {@link #grabSysOut()} in a
	 * LinkedList. This is necessary because they are not saved in the text
	 * field all the time. Writing all of them directly in the text field would
	 * slow down the whole application. Taking the Strings from the List and
	 * adding them to the text field (if needed) is done by
	 * {@link #buildNewConsoleContent()} which is called by {@link #showNewOutput()}.
	 * 
	 * @param s is the String that should be saved in the List
	 */
	protected void saveConsoleStrings(final String s) {

		String[] splitResult = s.split("(?=/n|/r)");

		for (int i = 0; i < splitResult.length; i++) {

			if (consoleStrings.size() > maxLines) {
				
				//synchronized is used to avoid IndexOutOfBoundsExceptions 
				synchronized (consoleStrings) {
					consoleStrings.removeFirst();
					consoleStrings.add(splitResult[i]);	
				}

			}else{
			consoleStrings.add(splitResult[i]);
			}
			if (!locked) {
				showNewOutput();
			}
		}
	}
	
	/**
	 * Shows new incoming Strings at the end of the console when scrolling is enabled
	 * 
	 * @param message is the new String
	 */
	public void showNewOutput() {
		
		// TODO JKH changing this to sync removes flickering but suddenly there would be pauses between the test cases...
		sync.asyncExec(new Runnable() {
			@Override
			public void run() {
//				slider.setMaximum(slider.getMaximum() + slider.getThumb() + 1);
				slider.setSelection(slider.getMaximum());
				buildNewConsoleContent();
				rebuildSlider();
			}
		});

	}
	
	public void write(String line) {
		inWriter.println(line);
		inWriter.flush();
	}
	
	/**
	 * This method deactivates redirection of System.out.
	 */
	private void releaseSysOut() {
		if(originalSystemOut != null) {
			System.setOut(originalSystemOut);
			System.out.println("deactivated redirection of System.out");
		}
	}
	
	/**
	 * This method activates redirection of System.in.
	 */
	private void grabSysIn() {
		// XXX check if redirecting the system in is actually necessary
		try {
	    	inWriter = new PrintWriter(new PipedOutputStream(inPipe), true);
	    	System.setIn(inPipe);
	    	originalSystemOut.println("activated redirection of System.in");
	    }
	    catch(IOException e) {
	    	System.out.println("Error: " + e);
	    	return;
	    }
	}
	
	/**
	 * This method deactivates redirection of System.in.
	 */
	private void releaseSysIn() {
		if(originalSystemIn != null) {
			System.setIn(originalSystemIn);
			originalSystemOut.println("deactivated redirection of System.in");
		}
	}
	
	@PreDestroy
	public void cleanUp() {
		releaseSysOut();
		releaseSysIn();
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