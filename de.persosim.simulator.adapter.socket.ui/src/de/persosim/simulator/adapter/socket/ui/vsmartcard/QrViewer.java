package de.persosim.simulator.adapter.socket.ui.vsmartcard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import io.nayuki.qrcodegen.QrCode;
import io.nayuki.qrcodegen.QrCode.Ecc;

public class QrViewer {

	private QrCode qr;
	private Canvas canvas;

	public QrViewer(Composite container) {
		canvas = new Canvas(container, SWT.NONE);
		GridData layoutDataCanvas = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		layoutDataCanvas.minimumWidth = 200;
		layoutDataCanvas.minimumHeight = 200;
		layoutDataCanvas.horizontalSpan = 2;
		canvas.setLayoutData(layoutDataCanvas);

		addQrPaintListener(canvas);
	}

	private void addQrPaintListener(Canvas canvas) {
		final Color BLACK = canvas.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		final Color WHITE = canvas.getDisplay().getSystemColor(SWT.COLOR_WHITE);

		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if (qr == null)
					return;
				
				Rectangle rect = ((Canvas) e.widget).getBounds();

				int shortestSide = rect.height > rect.width ? rect.width : rect.height;

				int pixelsPerModule = Math.floorDiv(shortestSide, qr.size);

				e.gc.setBackground(WHITE);
				e.gc.fillRectangle(rect);
				e.gc.setBackground(BLACK);
				for (int y = 0; y < qr.size; y++) {
					for (int x = 0; x < qr.size; x++) {
						if (qr.getModule(x, y))
							e.gc.fillRectangle(x * pixelsPerModule, y * pixelsPerModule, pixelsPerModule,
									pixelsPerModule);
					}
				}
			}
		});
	}

	public void update(String content) {
		qr = QrCode.encodeText(content, Ecc.MEDIUM);
		canvas.redraw();
	}
}
