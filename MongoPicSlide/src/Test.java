import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeListener;
 
import javax.swing.JFrame;
import javax.swing.JPanel;
 
 
public class Test extends JFrame {
 
         /**
	 * should i add the serialVersionUID
	 * some update in server
	 */
	private static final long serialVersionUID = -812565268515911417L;

		public Test() {
                  
                   super("Test",GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
                   MoveLabel label = new MoveLabel("带有滚动效果的标签");
                   this.add(label);
 
                   Test.removeListener(this);
                  
                   this.setIgnoreRepaint(false);
                   this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                   this.setSize(600, 300);
                   this.setVisible(true);
         }
        
         public static void main(String args[]) {
                  
                   new Test();
//               System.getProperties().list(System.out);
         }
        
         public static void removeListener(Component component){
                   ComponentListener[] componentListener = component.getComponentListeners();
                   for(int i = 0; i < componentListener.length;i++){
                            component.removeComponentListener(componentListener[i]);
                   }
                  
                   PropertyChangeListener[] changeListener = component.getPropertyChangeListeners();
                   for(int i = 0; i < changeListener.length;i++){
                            component.removePropertyChangeListener(changeListener[i]);
                   }
         }
 
         /**
          * 带有滚动效果的Label标签
          */
         private class MoveLabel extends JPanel implements Runnable{
 
                   /**
			 * 
			 */
			private static final long serialVersionUID = 6700959096170094323L;

				private String text = null;
 
                   private Thread thread = null;
 
                   private int x = 0;
 
                   private int w = 0, h = 0;
 
                   public MoveLabel(String text) {
                            super();
                            this.text = text;
                            this.setBackground(Color.BLACK);
                            this.setFont(new Font("宋体", Font.PLAIN, 300));
                            this.setForeground(Color.red);
                            this.setIgnoreRepaint(false);
                           
                            Test.removeListener(this);
                            thread = new Thread(this);
                            thread.start();
                   }
                  
                     public void update(Graphics g) {
                                paint(g);
                     }
                    
                   /*
                    * 绘制
                    */
                   protected void paintComponent(Graphics g) {
                           
                            super.paintComponent(g);
                            g.setColor(this.getBackground());
                            g.fillRect(0, 0, w = this.getWidth(), h = this.getHeight());
                            g.setColor(this.getForeground());
                            g.setFont(this.getFont());
                            g.drawString(text, x, h - 1);
                   }
 
                   public void run() {
                            while (true) {
                                     x -= 1;
                                     if (x < -w) {
                                               x = w;
                                     }
                                     this.repaint();
                                     try {
                                               Thread.sleep(16);//间隔绘制16毫秒
                                     } catch (InterruptedException e) {
                                               e.printStackTrace();
                                     }
                            }
                   }
         }
 
 
 
}
