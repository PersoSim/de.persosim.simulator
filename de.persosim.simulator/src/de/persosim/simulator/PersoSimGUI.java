package de.persosim.simulator;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;


public class PersoSimGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = -7479595500325169910L;

	JTextArea txtOut;
	JTextField txtIn;
	
	

	private final PipedInputStream inPipe = new PipedInputStream(); 
    private final PipedInputStream outPipe = new PipedInputStream(); 

	PrintWriter inWriter;
	
	PersoSimGUI(PersoSim persoSim) {
		super("PersoSim GUI");

	 
	    // Set the System.in and System.out streams 
	    System.setIn(inPipe); 
	    try {
	    	System.setOut(new PrintStream(new PipedOutputStream(outPipe), true)); 
	    	inWriter = new PrintWriter(new PipedOutputStream(inPipe), true); 
	    }
	    catch(IOException e) {
	    	System.out.println("Error: " + e);
	    	return;
	    }
	    
	    JPanel p = new JPanel();
	    p.setLayout(new BorderLayout(0, 0));
	    
	    txtOut = new JTextArea();
	    txtOut.setEditable(false);
	    txtOut.setCursor(null);  
	    txtOut.setOpaque(false);  
	    txtOut.setFocusable(false);
	    txtOut.setAutoscrolls(true);
	    p.add(new JScrollPane(txtOut));
	    
	    txtIn = new JTextField();
	    txtIn.addActionListener(this);
	    p.add(txtIn, BorderLayout.SOUTH);
	    
	    getContentPane().add(p);
	    
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		setSize(500, 500);
		
	    new SwingWorker<Void, String>() { 
	        @Override
			protected Void doInBackground() throws Exception { 
	            Scanner s = new Scanner(outPipe);
	            while (s.hasNextLine()) {
	            		 String line = s.nextLine();
		            	 publish(line);
	            }
	            s.close();
	            return null; 
	        } 
	         @Override protected void process(java.util.List<String> chunks) { 
	             for (String line : chunks) txtOut.append(line + "\n");
	         } 

	    }.execute(); 
	    
	    addWindowListener(new WindowAdapter() {
	        @Override
			public void windowClosing(WindowEvent e) {
	        	inWriter.println("exit"); 
	        }
	      });

		
		persoSim.startPersoSim();
		System.exit(0);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String text = txtIn.getText();
		txtIn.setText("");
		inWriter.println(text); 
	}

	public static void main(String[] args) {
		boolean startConsoleOnly = false;
		
		for (String curArg : args) {
			if (Simulator.CMD_CONSOLE_ONLY.equals(curArg)) {
				startConsoleOnly = true;
			} 
		}
		
		if (startConsoleOnly) {
			PersoSim.main(args);
		} else {
			PersoSim sim = new PersoSim(args);
			new PersoSimGUI(sim);
		}
	}
}

