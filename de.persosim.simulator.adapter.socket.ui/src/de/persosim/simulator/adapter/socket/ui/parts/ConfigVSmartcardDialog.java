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
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.persosim.simulator.adapter.socket.protocol.VSmartCardProtocol;
import de.persosim.simulator.adapter.socket.ui.PreferenceConstants;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;
import io.nayuki.qrcodegen.QrCode;

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

	private static QrCode qr = null;
	
	private static void setQrCode(NetworkInterface iface, String port){
		qr = QrCode.encodeText("vicc://" + iface.getInetAddresses().nextElement() + ":" + port, QrCode.Ecc.MEDIUM);
	} 
	
	private static void addQrPaintListener(Canvas canvas) {
		final Color BLACK = canvas.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		final Color WHITE = canvas.getDisplay().getSystemColor(SWT.COLOR_WHITE);

		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Rectangle rect = ((Canvas) e.widget).getBounds();

				int shortestSide = rect.height > rect.width ? rect.width : rect.height;
				
				int pixelsPerModule = Math.floorDiv(shortestSide, qr.size);

				e.gc.setBackground(WHITE);
				e.gc.fillRectangle(rect);
				e.gc.setBackground(BLACK);
				for (int y = 0; y < qr.size; y++) {
					for (int x = 0; x < qr.size; x++) {
						if (qr.getModule(x, y))
							e.gc.fillRectangle(x*pixelsPerModule, y*pixelsPerModule, pixelsPerModule, pixelsPerModule);
					}
				}
			}
		});
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

		final Canvas canvas = new Canvas(container, SWT.NONE);
		GridData layoutDataCanvas = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		layoutDataCanvas.minimumWidth = 200;
		layoutDataCanvas.minimumHeight = 200;
		layoutDataCanvas.horizontalSpan = 2;
		canvas.setLayoutData(layoutDataCanvas);
		
		addQrPaintListener(canvas);
		
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

		setQrCode(interfaces.get(0), portFromPrefs);

		
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
			    		setQrCode(i, port.getText());
			    		canvas.redraw();
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
		    		setQrCode(iface, port.getText());
		    		canvas.redraw();
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
