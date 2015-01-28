package de.persosim.simulator.ui.parts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.security.auth.callback.TextOutputCallback;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

import de.persosim.simulator.PersoSim;
import de.persosim.simulator.ui.utils.TextLengthLimiter;

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
	
	private PrintStream newSystemOut;
	private final PipedInputStream inPipe = new PipedInputStream();
	
	private PrintWriter inWriter;
	
	Composite parent;
	Slider slider;
	
	@PostConstruct
	public void createComposite(Composite parentComposite) {
		parent = parentComposite;
		grabSysOut();
		grabSysIn();
		
		parent.setLayout(new GridLayout(2, false));
		
		txtOutput = new Text(parent, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.WRAP);
		
		TextLengthLimiter tl = new TextLengthLimiter();
		txtOutput.addModifyListener(tl);
		
		txtOutput.setText("PersoSim GUI" + System.lineSeparator());
		txtOutput.setEditable(false);
		txtOutput.setCursor(null);
		txtOutput.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		//configure the slider
		slider = new Slider(parent, SWT.V_SCROLL);
		slider.setIncrement(1);
		slider.setPageIncrement(10);
		slider.setMaximum(consoleStrings.size());
		slider.setMinimum(0);
		slider.setThumb(consoleStrings.size());
		slider.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		
		/*
		 * add an entry because consoleStrings.length has to be bigger than the
		 * minimum value from the slider
		 */
		consoleStrings.add("Welcome to PersoSim\n");
		
		SelectionListener sliderListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				// clean text field before filling it with the requested data
				txtOutput.setText("");
				// consoleStrings.indexOf(consoleStrings.get(slider.getSelection()));

				// print first entry in the Linked list. Index in List = value
				// from slider
				appendToGuiFromList((consoleStrings.get(slider.getSelection())));

				// how many lines of text can the text field show without
				// cutting?
				// Max lines = (height of the text field) / (height of the font)
				int maxLineCount = txtOutput.getBounds().height
						/ txtOutput.getFont().getFontData()[0].getHeight();

				/*
				 * After showing the selected entry, also show following entries
				 * until the text field is full.
				 */
				for (int i = 0; i < maxLineCount; i++) {

					/*
					 * checks: 1.: is the next node from the list is the last
					 * one? 2.: is txtOutput already full?
					 */
//					if (slider.getSelection() + i < consoleStrings.size()
//							&& txtOutput.getLineCount() < maxLineCount) {
					if (slider.getSelection() + i < consoleStrings.size()) {

						// take the next entry from the List and print it
						appendToGuiFromList(i+(consoleStrings.get(slider.getSelection() + i)));
						txtInput.setText("Possible lines:"+i+" amount of appends:"+i+" Slider Value:"+slider.getSelection());
					} else break;
				}

			}
		};

		slider.addSelectionListener(sliderListener);  
		
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
		
		PersoSim sim = new PersoSim();
		sim.loadPersonalization("1"); //load default perso with valid TestPKI EF.CardSec etc. (Profile01)
		Thread simThread = new Thread(sim);
		simThread.start();
		
		//following Thread ensures that the buffered UI contents are updated regularly
		Thread uiBufferThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// ignore, timing is not critical here
					}
					appendToGui("");
				}
			}
		});
		uiBufferThread.start();
	
	}
		
	/**
	 * This method activates redirection of System.out.
	 */
	private void grabSysOut() {
	    OutputStream out = new OutputStream() {

			char [] buffer = new char [80];
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
					appendToGui(toPrint);
					
					
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
	
	StringBuilder guiStringBuilder = new StringBuilder();
	long lastGuiFlush = 0;

	//XXX ensure that this method is called often enough, so that the last updates are correctly reflected
	protected void appendToGui(String s) {
		
		//write the String into the Console Buffer
		if (consoleStrings.size() < maxLines && !s.equals("")) {
			consoleStrings.add(s);
			
			sync.asyncExec(new Runnable() {
				
				@Override
				public void run() {
					slider.setMaximum(consoleStrings.size());
//					slider.setSelection(2);
				}
			});
//			appendToGuiFromList(s);
		}
//			saveFile(consoleStrings.getLast());
		else if(!s.equals("")){
			//Buffer is full, delete the oldest entry before adding
			consoleStrings.pollFirst();
			consoleStrings.add(s);
//			appendToGuiFromList(s);
			
//			saveFile(consoleStrings.getLast());
		}		
//		if((guiStringBuilder.length() > 0) || (s.length() > 0)) {
//			if(s.length() > 0) {
//				guiStringBuilder.append(s);
//			}
//			
//			long currentTime = new Date().getTime();
//			if (currentTime-lastGuiFlush > 50) {
//				lastGuiFlush = currentTime;
//				//XXX MBK check why syncExec blocks (possible deadlock with System.out.print())
//				final String toPrint = guiStringBuilder.toString();
//				guiStringBuilder = new StringBuilder();
//				sync.asyncExec(new Runnable() {
//					
//					@Override
//					public void run() {
//						txtOutput.append(toPrint);
//					}
//				});
//			}
//		}
	}
	
	protected void appendToGuiFromList(String s){
		if((guiStringBuilder.length() > 0) || (s.length() > 0)) {
		if(s.length() > 0) {
			guiStringBuilder.append(s);
		}
		
		long currentTime = new Date().getTime();
		if (currentTime-lastGuiFlush > 50) {
			lastGuiFlush = currentTime;
			//XXX MBK check why syncExec blocks (possible deadlock with System.out.print())
			final String toPrint = guiStringBuilder.toString();
			guiStringBuilder = new StringBuilder();
			sync.asyncExec(new Runnable() {
				
				@Override
				public void run() {
					txtOutput.append(toPrint);
				}
			});
		}
	}
	}
	
	//only for testing purposes
	public void saveFile(String s){
		try{
	    	File file =new File("myfile.txt");

	    	if(!file.exists()){
	    	   file.createNewFile();
	    	}
	    	FileWriter fw = new FileWriter(file,true);
	    	BufferedWriter bw = new BufferedWriter(fw);
	    	bw.write(s);
	    	bw.close();
	      }catch(IOException ioe){
	         System.out.println("Exception occurred:");
	    	 ioe.printStackTrace();
	       }
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