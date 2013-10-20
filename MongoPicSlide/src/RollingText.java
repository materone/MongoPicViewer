import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;


public class RollingText {

	protected Shell shell;
	private Text txtTest;
	protected Canvas canvas;
	int x = 0;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			RollingText window = new RollingText();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		Rectangle rect = display.getClientArea();
		shell.setLocation((rect.width - shell.getBounds().width) / 2,
				(rect.height - shell.getBounds().height) / 2);
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		
		canvas = new Canvas(shell, SWT.NONE);
		canvas.setBounds(10, 39, 430, 64);
		canvas.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				// TODO Auto-generated method stub
				Image img = (Image)canvas.getData("double-buffer-image");
				if(img == null|| img.getBounds().width != canvas.getSize().x
						|| img.getBounds().height != canvas.getSize().y) {
					img = new Image(shell.getDisplay(), canvas.getSize().x,
							canvas.getSize().y);
					canvas.setData("double-buffer-image", img);
				}
				GC gc = new GC(img);
				gc.setBackground(e.gc.getBackground());
				gc.setForeground(e.gc.getForeground());
				gc.fillRectangle(img.getBounds());
				gc.setFont(new Font(shell.getDisplay(), "Arial", 26, SWT.BOLD));
				gc.drawString("英文兔子 VS 中文土豪", x++, 10);
				if(x==canvas.getSize().x) x= 0;
				e.gc.drawImage(img, 0, 0);
				gc.dispose();
				shell.getDisplay().timerExec(25, new Runnable() {					
					@Override
					public void run() {
						canvas.redraw();
					}
				});
			}
		});
		
		txtTest = new Text(shell, SWT.BORDER);
		txtTest.setText("Test \u571F\u8C6A");
		txtTest.setBounds(10, 121, 333, 19);
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.getDisplay().timerExec(20, new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						canvas.redraw();
					}
				});
			}
		});
		btnNewButton.setBounds(346, 117, 94, 28);
		btnNewButton.setText("SCroll");

	}
	protected Canvas getCanvas() {
		return canvas;
	}
}
