package de.persosim.simulator.adapter.socket.ui.parts;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;
import org.globaltester.logging.tags.LogTag;

import de.persosim.simulator.adapter.socket.ui.PreferenceConstants;
import de.persosim.simulator.adapter.socket.ui.vsmartcard.QrViewer;
import de.persosim.simulator.log.PersoSimLogTags;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;

public class ConfigVSmartcardDialog extends Dialog
{
	private static final String DIALOG_NAME = "PersoSim - Configure VSmartcard";

	private Text port;

	public ConfigVSmartcardDialog(Shell parentShell)
	{
		super(parentShell);
		BasicLogger.log(DIALOG_NAME + " dialog opened", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));
	}


	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		button.addListener(SWT.Selection, e -> BasicLogger.log(DIALOG_NAME + " dialog closed", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID)));
	}

	@Override
	protected boolean isResizable()
	{
		return true;
	}

	@Override
	protected void handleShellCloseEvent()
	{
		super.handleShellCloseEvent();
		BasicLogger.log(DIALOG_NAME + " closed", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));
	}

	private static InetAddress getAddress(NetworkInterface iface)
	{
		List<InetAddress> inetAddresses = Collections.list(iface.getInetAddresses());

		inetAddresses.sort((a, b) -> a instanceof Inet4Address ? -1 : 1);

		return inetAddresses.get(0);
	}

	private static String getQrContent(NetworkInterface iface, String port)
	{
		List<InetAddress> inetAddresses = Collections.list(iface.getInetAddresses());

		inetAddresses.sort((a, b) -> a instanceof Inet4Address ? -1 : 1);

		return "vicc://" + getAddress(iface).getHostAddress() + ":" + port;
	}

	private static String networkInterfaceToLabel(Object element, String text)
	{
		if (element instanceof NetworkInterface i) {
			return i.getDisplayName() + " (" + getAddress(i).getHostAddress() + ")";
		}
		return text;
	}

	@Override
	protected Control createDialogArea(Composite parentComposite)
	{
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
		BasicLogger.log("Port loaded from preferences: " + portFromPrefs, LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));

		port.setText(portFromPrefs);
		port.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		port.setEnabled(true);

		List<NetworkInterface> interfaces = Collections.emptyList();
		try {
			interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			interfaces = interfaces.stream().filter(n -> {
				try {
					return !n.isLoopback() && n.isUp() && !n.getInterfaceAddresses().isEmpty();
				}
				catch (SocketException e) {
					BasicLogger.logException(getClass(), e);
					return false;
				}
			}).toList();
		}
		catch (SocketException e1) {
			BasicLogger.logException(getClass(), e1);
		}

		Label interfaceLabel = new Label(container, SWT.NONE);
		interfaceLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		interfaceLabel.setText("Interface:");

		final ComboViewer viewer = new ComboViewer(container, SWT.READ_ONLY);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element)
			{
				return networkInterfaceToLabel(element, super.getText(element));
			}
		});

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.size() > 0) {
					Object firstElement = selection.getFirstElement();
					if (firstElement instanceof NetworkInterface i) {
						qrViewer.update(getQrContent(i, port.getText()));
						PersoSimPreferenceManager.storePreference(PreferenceConstants.VSMARTCARD_LAST_INTERFACE, i.getName());
						BasicLogger.log("Interface stored in preferences: '" + i + "'", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));
					}
				}
			}
		});

		for (NetworkInterface current : interfaces) {
			BasicLogger.log("Available Interface: '" + current + "'", LogLevel.DEBUG, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));
		}
		viewer.setInput(interfaces);

		String lastSelectedInterfaceName = PersoSimPreferenceManager.getPreference(PreferenceConstants.VSMARTCARD_LAST_INTERFACE);
		BasicLogger.log("Last interface loaded from preferences: '" + lastSelectedInterfaceName + "'", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));

		NetworkInterface selected = interfaces.stream().filter(i -> i.getName().equals(lastSelectedInterfaceName)).findAny().orElse(null);
		if (selected == null && !interfaces.isEmpty())
			selected = interfaces.get(0);

		if (selected != null) {
			viewer.setSelection(new StructuredSelection(selected));
			// PersoSimPreferenceManager.storePreference(PreferenceConstants.VSMARTCARD_LAST_INTERFACE, selected.getName());
			// BasicLogger.log("Interface stored in preferences: '" + selected.getName() + "'", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));
		}

		port.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e)
			{
				if (!port.getText().isEmpty()) {
					PersoSimPreferenceManager.storePreference(PreferenceConstants.VSMARTCARD_PORT, port.getText());
					BasicLogger.log("Port stored in preferences: '" + port.getText() + "'", LogLevel.INFO, new LogTag(BasicLogger.LOG_TAG_TAG_ID, PersoSimLogTags.VSMARTCARD_TAG_ID));
					NetworkInterface iface = (NetworkInterface) viewer.getStructuredSelection().getFirstElement();
					qrViewer.update(getQrContent(iface, port.getText()));
				}
			}
		});

		return container;
	}

	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText(DIALOG_NAME);
	}

}
