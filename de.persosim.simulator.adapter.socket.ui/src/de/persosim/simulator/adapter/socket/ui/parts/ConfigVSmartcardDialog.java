package de.persosim.simulator.adapter.socket.ui.parts;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.persosim.simulator.adapter.socket.protocol.VSmartCardProtocol;
import de.persosim.simulator.adapter.socket.ui.PreferenceConstants;
import de.persosim.simulator.adapter.socket.ui.vsmartcard.QrViewer;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;

public class ConfigVSmartcardDialog extends Dialog {

	private MPart readerPart;
	private Text port;

	public ConfigVSmartcardDialog(Shell parentShell, MPart readerPart) {
		super(parentShell);
		this.readerPart = readerPart;
	}
	

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
	}
	
	private static String getQrContent(NetworkInterface iface, String port){
		return "vicc://" + iface.getInetAddresses().nextElement() + ":" + port;
	}
	
	private static String networkInterfaceToLabel(Object element, String text) {
    	if (element instanceof NetworkInterface) {
    		NetworkInterface i = (NetworkInterface) element;
    		return i.getDisplayName();
    	}
    	return text;
	}
	
	@Override
	protected Control createDialogArea(Composite parentComposite) {
		final Composite parent = parentComposite;

		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));

		final QrViewer qrViewer = new QrViewer(container, SWT.NONE);
		GridData layoutDataQrViewer = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		layoutDataQrViewer.minimumWidth = 200;
		layoutDataQrViewer.minimumHeight = 200;
		layoutDataQrViewer.horizontalSpan = 2;
		qrViewer.setLayoutData(layoutDataQrViewer);
		
		Label portLabel = new Label(container, SWT.NONE);
		portLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		portLabel.setText("Port:");
		
		port = new Text(container, SWT.BORDER);
		final String portFromPrefs = PersoSimPreferenceManager.getPreference(PreferenceConstants.VSMARTCARD_PORT, VSmartCardProtocol.DEFAULT_PORT + "");
		
		port.setText(portFromPrefs);
		port.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		port.setEnabled(true);
				
		List<NetworkInterface> interfaces = Collections.emptyList();
		try {
			interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Label interfaceLabel = new Label(container, SWT.NONE);
		interfaceLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		interfaceLabel.setText("Interface:");
		
		final ComboViewer viewer = new ComboViewer(container, SWT.READ_ONLY);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider() {
		    @Override
		    public String getText(Object element) {
		    	return networkInterfaceToLabel(element, super.getText(element));
		    }
		});
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
		    @Override
		    public void selectionChanged(SelectionChangedEvent event) {
		        IStructuredSelection selection = (IStructuredSelection) event
		            .getSelection();
		        if (selection.size() > 0){
		        	Object firstElement = selection.getFirstElement();
					if (firstElement instanceof NetworkInterface) {
		        		NetworkInterface i = (NetworkInterface) firstElement;
		        		qrViewer.update(getQrContent(i, port.getText()));
		        	}
		        }
		    }
		});
		
		viewer.setInput(interfaces);
		viewer.setSelection(new StructuredSelection(interfaces.get(0)));
		
		port.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (!port.getText().isEmpty()){
					PersoSimPreferenceManager.storePreference(PreferenceConstants.VSMARTCARD_PORT, port.getText());
					NetworkInterface iface = (NetworkInterface)viewer.getStructuredSelection().getFirstElement();
	        		qrViewer.update(getQrContent(iface, port.getText()));
				}
			}
		});
		
		return container;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("PersoSim - Configure VSmartcard");
	}

}
