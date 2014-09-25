package de.persosim.simulator.ui.parts;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.persosim.simulator.PersoSim;

/**
 * @author slutters
 *
 */
public class PersoSimGuiMain {
	
	// get UISynchronize injected as field
	@Inject UISynchronize sync;
	
	private Text txtInput, txtOutput;
	
	private final InputStream originalSystemIn = System.in;
	private final PrintStream originalSystemOut = System.out;
	
	private PrintStream newSystemOut;
	
    private final PipedInputStream outPipe = new PipedInputStream();
	private final PipedInputStream inPipe = new PipedInputStream();;
	
	private PrintWriter inWriter;
	
	private Job job;
	
	private boolean continueScanning;
	
	Composite parent;

	@PostConstruct
	public void createComposite(Composite parentComposite) {
		parent = parentComposite;
		
		grabSysOut();
		grabSysIn();
		
		parent.setLayout(new GridLayout(1, false));
		
		txtOutput = new Text(parent, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		
		txtOutput.setText("PersoSim GUI" + System.lineSeparator());
		txtOutput.setEditable(false);
		txtOutput.setCursor(null);
//		txtOutput.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		txtOutput.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		initializeSystemOutScanner();
		
		parent.setLayout(new GridLayout(1, false));
		
		txtInput = new Text(parent, SWT.BORDER);
		txtInput.setMessage("Enter command here");
		
		txtInput.addKeyListener(new KeyAdapter() {
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
		Thread simThread = new Thread(sim);
		simThread.start();
		
	}
	
	public void initializeSystemOutScanner() {
		continueScanning = true;
		job = new Job("System Out Scanner Job") {
		  @Override
		  protected IStatus run(IProgressMonitor monitor) {
		    final Scanner s = new Scanner(outPipe);
	        while (continueScanning && s.hasNextLine()) {
	        	// update the UI
	        	sync.syncExec(new Runnable() {
	  		      @Override
	  		      public void run() {
	  		        // manipulate user interface
	  		    	txtOutput.append(s.nextLine() + System.lineSeparator());
	  		      }
	  		    });
	        }
	        
	        txtOutput.append("Job finished");
	        
	        s.close();
		    
		    return Status.OK_STATUS;
		  }
		};

		// Start the Job
		job.schedule();
	}
	
	/**
	 * This method activates redirection of System.out.
	 */
	private void grabSysOut() {
	    try {
	    	newSystemOut = new PrintStream(new PipedOutputStream(outPipe), true);
	    	System.setOut(newSystemOut);
	    	
	    	if(newSystemOut != null) {
	    		originalSystemOut.println("activated redirection of System.out");
	    	}
	    }
	    catch(IOException e) {
	    	originalSystemOut.println("Error: " + e);
	    	return;
	    }
	    
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
		continueScanning = false;
//		job.cancel();
		releaseSysOut();
		releaseSysIn();
		System.exit(0);
	}

	@Focus
	public void setFocus() {
		txtOutput.setFocus();
	}
	
	public String toString() {
		return "OutputHandler here!";
	}
	
}