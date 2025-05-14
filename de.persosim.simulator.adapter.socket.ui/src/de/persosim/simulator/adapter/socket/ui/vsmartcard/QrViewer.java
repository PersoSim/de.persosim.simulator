package de.persosim.simulator.adapter.socket.ui.vsmartcard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import io.nayuki.qrcodegen.QrCode;
import io.nayuki.qrcodegen.QrCode.Ecc;

/**
 * This allows to display a qr code on an SWT composite
 */
public class QrViewer extends Canvas{

	private QrCode qr;
	private int border = 5;

	public QrViewer(Composite parent, int style) {
		super(parent, style);

		addQrPaintListener();
	}

	private void addQrPaintListener() {
		final Color BLACK = getDisplay().getSystemColor(SWT.COLOR_BLACK);
		final Color WHITE = getDisplay().getSystemColor(SWT.COLOR_WHITE);

		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if (qr == null)
					return;
				
				Rectangle rect = ((Canvas) e.widget).getBounds();

				int shortestSide = rect.height > rect.width ? rect.width : rect.height;
				int usable = shortestSide - 2 * border;
				
				int pixelsPerModule = Math.floorDiv(usable, qr.size);
				
				int qrSizeInPixels = pixelsPerModule * qr.size;
				
				int offsetx = (rect.width - qrSizeInPixels)/2;
				int offsety = (rect.height - qrSizeInPixels)/2;

				e.gc.setBackground(WHITE);
				e.gc.fillRectangle(0,0,rect.width,rect.height);
				e.gc.setBackground(BLACK);
				for (int y = 0; y < qr.size; y++) {
					for (int x = 0; x < qr.size; x++) {
						if (qr.getModule(x, y))
							e.gc.fillRectangle(offsetx + x * pixelsPerModule, offsety + y * pixelsPerModule, pixelsPerModule,
									pixelsPerModule);
					}
				}
			}
		});
	}

	public void update(String content) {
		qr = QrCode.encodeText(content, Ecc.MEDIUM);
		redraw();
	}
}
