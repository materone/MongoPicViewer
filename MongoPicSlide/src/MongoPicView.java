import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Hashtable;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;

public class MongoPicView {

	protected Shell shell;
	protected boolean connFlag = false;
	protected MongoClient mc;
	protected GridFSDBFile gfsdb;
	protected GridFS gfs;
	protected String HOST = "localhost";
	long cur = 0;
	long max = 0;
	protected String message = null;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MongoPicView window = new MongoPicView();
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
		Rectangle rect = display.getClientArea();
		shell.setLocation((rect.width - shell.getBounds().width) / 2,
				(rect.height - shell.getBounds().height) / 2);
		shell.open();
		shell.layout();
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
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				if(mc != null){
					mc.close();
				}
			}
		});
		shell.setSize(451, 639);
		shell.setText("SWT Application");
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setBounds(10, 10, 419, 559);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		final Canvas canvas = new Canvas(scrolledComposite, SWT.BORDER);
		scrolledComposite.setContent(canvas);
		scrolledComposite.setMinSize(canvas.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		// 监听canvas的重绘事件
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(final PaintEvent event) {
				Image image = (Image) canvas.getData("image");
				Point point = (Point) canvas.getData("point");
				if (image != null) {
					String s = "How About this image";
					event.gc.setFont(new Font(shell.getDisplay(), "Arial", 26, SWT.BOLD));
					event.gc.drawImage(image, point.x, point.y);// 定位图像左上角距canvas左上角的距离
					event.gc.drawRoundRectangle(point.x+1, point.y+1, event.gc.getCharWidth('c')*(s.length()-1), 28, 10, 10);
					event.gc.drawString(s, point.x, point.y,true);
				}
			}
		});

		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.setBounds(288, 575, 59, 28);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (cur >= 0) {
					showFile(canvas);
					cur--;
					if (cur < 0) {
						cur = max - 1;
					}
				}
			}
		});
		btnNewButton.setText("|<<");

		Button btnNewButton_1 = new Button(shell, SWT.NONE);

		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showFile(canvas);
				cur++;
				if (cur == max)
					cur = 0;
			}
		});
		btnNewButton_1.setBounds(353, 575, 68, 28);
		btnNewButton_1.setText(">>|");

		final Button btnRadioButton = new Button(shell, SWT.RADIO);
		btnRadioButton.setBounds(84, 579, 102, 18);
		btnRadioButton.setText("Conn Status");

		final Button btnConn = new Button(shell, SWT.NONE);
		btnConn.setBounds(192, 575, 68, 28);
		btnConn.setSelection(false);
		btnConn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!connFlag) {
					// TODO some conn op
					if (mc == null) {
						try {
							mc = new MongoClient(HOST);
							DB db = mc.getDB("test");
							DBCollection coll = db.getCollection("fs.files");
							System.out.println("DB Conn!");
							max = coll.getCount();
							// //get fs count
							// DBCursor cursor = coll.find().skip(234);
							// int count = 0;
							// try {
							// while(cursor.hasNext()) {
							// System.out.println("There has count:"+cursor.count());
							// // System.out.println("Skip:"+cursor.skip(14));
							// if(count++==0){
							// System.out.println(cursor.next());
							// break;
							// }
							// }
							// } finally {
							// cursor.close();
							// }
						} catch (UnknownHostException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					// final reset the btnradio
					btnRadioButton.setSelection(!connFlag);
					connFlag = !connFlag;
					btnConn.setText("Close");
				} else {
					// TODO some close op
					if (mc != null) {
						mc.close();
						mc = null;
						System.out.println("DB Closed!");
					}
					btnRadioButton.setSelection(!connFlag);
					connFlag = !connFlag;
					btnConn.setText("Conn");
				}
			}
		});
		btnConn.setText("Conn");

		Button btnExit = new Button(shell, SWT.NONE);
		btnExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (mc != null) {
					mc.close();
					mc = null;
				}
				System.exit(0);
			}
		});
		btnExit.setBounds(10, 575, 68, 28);
		btnExit.setText("Exit");

	}

	private void showFile(final Canvas canvas) {
		if (mc != null) {
			gfs = new GridFS(mc.getDB("test"));
			DBCursor dbcur = gfs.getFileList();
			dbcur.skip((int) cur);
			if (dbcur.hasNext()) {
				DBObject obj = dbcur.next();
				gfsdb = gfs.findOne(obj);
				InputStream is = gfsdb.getInputStream();
				// resize to fit the canvas
				ImageData id = new ImageData(is);
				canvas.setSize(id.width, id.height);
				float a=0,a1=0,w=0,h=0,canvW,canvH,imgW,imgH,x,y;
				canvW = canvas.getSize().x;
				canvH = canvas.getSize().y;
				imgW = id.width;
				imgH = id.height;
				System.out.println(canvW+":"+canvH+":"+imgW+":"+imgH);
				a = canvW/canvH;
				a1 = imgW/imgH;
				System.out.println(a+":"+a1);		
				if(a1>a){
					if(imgW > canvW){
						w = canvW;
						h = w/a1;
						x = 0;
						y = (canvH - h)/2;
					}else{
						w = imgW;
						h = w/a1;
						x= (canvW-w)/2;
						y = (canvH-h)/2;
					}
				}else if(a1 <a){
					if(imgH > canvH){
						h = canvH;
						w = h * a1;	
						y = 0;
					}else{
						h = imgH;
						w = h * a1;	
						y = (canvH-h)/2;
					}
					x = (canvW-w)/2;
				}else{
					System.out.println("==");
					if(imgW>canvW){
						w=canvW;
						h=canvH;
						x = 0;
						y = (h-canvH)/2;
					}else{
						w = imgW;
						h = imgH;
						y = (canvW-w)/2;
						x = (canvW-w)/2;
					}
				}
				Image img = new Image(shell.getDisplay(), id.scaledTo((int)w,(int)h));
				
				canvas.setData("image",img);
				canvas.setData("point",new Point((int)x, (int)y));
				canvas.redraw();
				// draw image
				System.out.println("ID:" + gfsdb.getId()+" N:"+gfsdb.getFilename()+" L:"+gfsdb.getLength() +"O,W/H"+imgW+"/"+ imgH+" w/h:" + w +"/"+h);
			}
			// List <GridFSDBFile> fs = gfs.find("./Unknown-190.jpg");
			// if(fs.size() >0){
			// gfsdb = fs.get(0);
			// InputStream is = gfsdb.getInputStream();
			// Image img = new Image(shell.getDisplay(),new ImageData(is));
			// canvas.setData(img);
			// canvas.redraw();
			// //draw image
			// System.out.println("Drawing");
			// }
		} else {
			MessageBox mb = new MessageBox(shell);
			mb.setMessage("Please Connect to db first");
			mb.open();
		}
	}

	/**
	 * computer the scale size of image for canvas
	 * 
	 * @param imgW
	 * @param imgH
	 * @param canvW
	 * @param canvH
	 * @return
	 */
	Hashtable<String, Integer> scal(int imgW,int imgH,int canvW,int canvH){
		Hashtable<String, Integer> ret = new Hashtable<String, Integer>();
		int a=0,a1=0,w=0,h=0;
		a = canvW/canvH;
		a1 = imgW/imgH;
				
		if(a1>a){
			w = canvW;
			h = w/a1;
		}else if(a1 <a){
			h = canvH;
			w = h * a1;				
		}else{
			w=canvW;
			h=canvH;
		}
		
		ret.put("width", w);
		ret.put("heigh", h);
		ret.put("startx",0);
		ret.put("starty", 0);
		return ret;
	}
}
