package org.chufan.util;

import java.io.InputStream;
import java.net.UnknownHostException;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Group;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

import org.eclipse.swt.widgets.Canvas;

public class MongoPicView1 {

	protected Shell shell;
	protected boolean connFlag = false;
	protected MongoClient mc;
	protected GridFSDBFile gfsdb;
	protected GridFS gfs;
	int cur = 0;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MongoPicView1 window = new MongoPicView1();
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
		shell.setLocation((rect.width-shell.getBounds().width)/2, (rect.height-shell.getBounds().height)/2);
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
		shell.setSize(629, 474);
		shell.setText("SWT Application");
		
		Group grpPicture = new Group(shell, SWT.BORDER | SWT.SHADOW_ETCHED_IN);
		grpPicture.setToolTipText("a viewers of mongo files");
		grpPicture.setText("Picture");
		grpPicture.setBounds(10, 10, 609, 403);
		
		final Composite composite = new Composite(grpPicture, SWT.NONE);
		composite.setBounds(10, 20, 585, 330);
		
		final Canvas canvas = new Canvas(composite, SWT.BORDER);
		canvas.setBounds(10, 10, 565, 310);
		//监听canvas的重绘事件
		canvas.addPaintListener(new PaintListener(){
		public void paintControl(final PaintEvent event){
		   Image image=(Image)canvas.getData();
		   if(image!=null){
		    event.gc.drawImage(image,0,0);//定位图像左上角距canvas左上角的距离
		   }
		}
		});
		
		Button btnNewButton = new Button(grpPicture, SWT.NONE);
		btnNewButton.setBounds(401, 356, 94, 28);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(cur > 0){
					cur--;
					showFile(canvas);
				}
			}
		});
		btnNewButton.setText("|<<");
		
		Button btnNewButton_1 = new Button(grpPicture, SWT.NONE);
		
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cur ++;
				showFile(canvas);
			}			
		});
		btnNewButton_1.setBounds(501, 356, 94, 28);
		btnNewButton_1.setText(">>|");
		
		final Button btnRadioButton = new Button(grpPicture, SWT.RADIO);
		btnRadioButton.setBounds(193, 360, 102, 18);
		btnRadioButton.setText("Conn Status");
		
		final Button btnConn = new Button(grpPicture, SWT.NONE);
		btnConn.setBounds(301, 356, 94, 28);
		btnConn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!connFlag){
					//TODO some conn op
					if(mc == null){
						try {
							mc = new MongoClient();
							DB db = mc.getDB("test");
							DBCollection coll = db.getCollection("fs.files");
							System.out.println("DB Conn!");
							//get fs count
							DBCursor cursor = coll.find().skip(234);
							int count = 0;
							try {
							   while(cursor.hasNext()) {
								   System.out.println("There has count:"+cursor.count());
//								   System.out.println("Skip:"+cursor.skip(14));
								   if(count++==0){
									   System.out.println(cursor.next());
									   break;
								   }
							   }
							} finally {
							   cursor.close();
							}
						} catch (UnknownHostException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}						
					}
					//final reset the btnradio
					btnRadioButton.setSelection(!connFlag);
					connFlag = ! connFlag;
					btnConn.setText("Close");
				}else{
					//TODO some close op
					if(mc != null){
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
		
		Button btnExit = new Button(grpPicture, SWT.NONE);
		btnExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(mc != null){
					mc.close();mc = null;
				}
				System.exit(0);
			}
		});
		btnExit.setBounds(10, 356, 94, 28);
		btnExit.setText("Exit");

	}
	
	private void showFile(final Canvas canvas) {
		if (mc != null){
			gfs = new GridFS(mc.getDB("test"));
			DBCursor dbcur = gfs.getFileList();
			dbcur.skip(cur);
			if(dbcur.hasNext()){				
				DBObject obj = dbcur.next();
				gfsdb = gfs.findOne(obj);
				InputStream is = gfsdb.getInputStream();
				Image img = new Image(shell.getDisplay(),new ImageData(is));
				canvas.setData(img);
				canvas.redraw();
				//draw image
				System.out.println("Drawing");
			}
//			List <GridFSDBFile> fs =  gfs.find("./Unknown-190.jpg");
//			if(fs.size() >0){
//				gfsdb = fs.get(0);
//				InputStream is = gfsdb.getInputStream();
//				Image img = new Image(shell.getDisplay(),new ImageData(is));
//				canvas.setData(img);
//				canvas.redraw();
//				//draw image
//				System.out.println("Drawing");
//			}
		}else{
			MessageBox mb = new MessageBox(shell);
			mb.setMessage("Please Connect to db first");
			mb.open();
		}
	}
}
