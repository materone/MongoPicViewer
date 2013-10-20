package org.chufan.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Rain
 * @version 创建时间：2010-11-26 上午10:23:48
 */
public class DoubleBuffer {
	Display display = new Display();
	Shell shell = new Shell(display);

	public DoubleBuffer() {
		shell.setLayout(new FillLayout());

		final Canvas drawCanvas = new Canvas(shell, SWT.NONE);

		drawCanvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				// Image image = new
				// Image(display,drawCanvas.getBounds().width,drawCanvas.getBounds().height);
				Image image = (Image) drawCanvas.getData("double-buffer-image");
				if (image == null
						|| image.getBounds().width != drawCanvas.getSize().x
						|| image.getBounds().height != drawCanvas.getSize().y) {
					image = new Image(display, drawCanvas.getSize().x,
							drawCanvas.getSize().y);
					drawCanvas.setData("double-buffer-image", image);
				}
				GC imageGC = new GC(image);
				imageGC.setBackground(e.gc.getBackground());
				imageGC.setForeground(e.gc.getForeground());

				Rectangle imageSize = image.getBounds();
				imageGC.fillRectangle(0, 0, imageSize.width + 1,
						imageSize.height + 1); // 填充背景
				Point size = drawCanvas.getSize();

				int x1 = (int) (Math.random() * size.x);
				int y1 = (int) (Math.random() * size.y);
				int x2 = Math.max(drawCanvas.getBounds().width - x1 - 10, 50);
				int y2 = Math.max(drawCanvas.getBounds().height - y1 - 10, 50);
				imageGC.drawRoundRectangle(x1, y1, x2, y2, 5, 5); // 画圆角矩形

				// imageGC.drawRoundRectangle(10, 10, 50, 40, 5, 5);
				e.gc.drawImage(image, 0, 0);
				imageGC.dispose();
				display.timerExec(20, new Runnable() {
					public void run() {
						drawCanvas.redraw();
					}
				});
			}
		});

		shell.setSize(300, 200);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	public static void main(String... args) {
		new DoubleBuffer();
	}
}
