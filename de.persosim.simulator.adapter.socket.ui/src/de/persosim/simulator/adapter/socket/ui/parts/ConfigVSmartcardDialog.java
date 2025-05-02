package de.persosim.simulator.adapter.socket.ui.parts;

import java.net.Inet4Address;
import java.net.InetAddress;
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
import org.globaltester.logging.BasicLogger;

import de.persosim.simulator.adapter.socket.ui.PreferenceConstants;
import de.persosim.simulator.adapter.socket.ui.vsmartcard.QrViewer;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;

public class ConfigVSmartcardDialog extends Dialog {
	private Text port;

	public ConfigVSmartcardDialog(Shell parentShell, MPart readerPart) {
		super(parentShell);
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
	
	private static InetAddress getAddress(NetworkInterface iface) {
		List<InetAddress> inetAddresses = Collections.list(iface.getInetAddresses());
		
		inetAddresses.sort((a,b) -> a instanceof Inet4Address ? -1 : 1);
		
		return inetAddresses.get(0);
	}
	
	private static String getQrContent(NetworkInterface iface, String port){
		List<InetAddress> inetAddresses = Collections.list(iface.getInetAddresses());
		
		inetAddresses.sort((a,b) -> a instanceof Inet4Address ? -1 : 1);
		
		return "vicc://" + getAddress(iface).getHostAddress() + ":" + port;
	}
	
	private static String networkInterfaceToLabel(Object element, String text) {
    	if (element instanceof NetworkInterface) {
    		NetworkInterface i = (NetworkInterface) element;
    		return i.getDisplayName() + " (" + getAddress(i).getHostAddress() + ")";
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
		final String portFromPrefs = PersoSimPreferenceManager.getPreference(PreferenceConstants.VSMARTCARD_PORT, PreferenceConstants.VSMARTCARD_PORT_DEFAULT);
		
		port.setText(portFromPrefs);
		port.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		port.setEnabled(true);
				
		List<NetworkInterface> interfaces = Collections.emptyList();
		try {
			interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			interfaces = interfaces.stream().filter((n) -> {
				try {
					return !n.isLoopback() && n.isUp() && !n.getInterfaceAddresses().isEmpty();
				} catch (SocketException e) {
					BasicLogger.logException(getClass(), e);
					return false;
				}
			}).toList();
		} catch (SocketException e1) {
			BasicLogger.logException(getClass(), e1);
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
						PersoSimPreferenceManager.storePreference(PreferenceConstants.VSMARTCARD_LAST_INTERFACE, i.getName());
		        	}
		        }
		    }
		});
		
		viewer.setInput(interfaces);
		

		String lastSelectedInterfaceName = PersoSimPreferenceManager.getPreference(PreferenceConstants.VSMARTCARD_LAST_INTERFACE);
		NetworkInterface selected = interfaces.stream().filter((i) -> i.getName().equals(lastSelectedInterfaceName)).findAny().orElse(null);
		if (selected == null && interfaces.size() > 0)
			selected = interfaces.get(0);
		
		if (selected != null) {
			viewer.setSelection(new StructuredSelection(selected));
			PersoSimPreferenceManager.storePreference(PreferenceConstants.VSMARTCARD_LAST_INTERFACE, selected.getName());
		}
		
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
