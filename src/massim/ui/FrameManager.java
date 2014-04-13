package massim.ui;

import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import massim.ui.frames.WelcomeFrame;

public class FrameManager {

	private static List<JFrame> frames = new ArrayList<JFrame>();
	public static void addFrame(JFrame frame)
	{
		if(frame == null) return;	
		frames.add(frame);
	}
	
	public static JFrame getFrame(Class className)
	{
		if(className == null) return null;
		
		for(JFrame frame : frames)
		{
			if(frame.getClass().equals(className))
			{
				return frame;
			}
		}
		return null;	
	}
	
	public static boolean checkFrame(Class className)
	{
		if(className == null) return false;
		
		for(JFrame frame : frames)
		{
			if(frame.getClass().equals(className))
			{
				return true;
			}
		}
		return false;	
	}
	
	public static void removeFrame(Class className)
	{
		if(className == null) return;
		
		int indexToRemove = -1;
		int index = 0;
		for(JFrame frame : frames)
		{
			if(frame.getClass().equals(className))
			{
				indexToRemove = index;
			}
			index++;
		}
		if(indexToRemove > -1) frames.remove(indexToRemove);
	}
	
	public static void removeFrame(JFrame frame)
	{
		if(frame == null) return;
		frames.remove(frame);
	}
	
	public static void startApplication()
	{
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PerformanceStats pStats = new PerformanceStats("MASSIM - Welcome");
					pStats.startTracking();
					
					WelcomeFrame window = new WelcomeFrame();
					window.setVisible(true);
					
					pStats.endAndPrint();
					FrameManager.addFrame(window);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void openNewFrame(JFrame currFrame, Class<?> className)
	{
		if(className == null) return;
		
		if(currFrame != null) {
			if(!FrameManager.checkFrame(currFrame.getClass()))
				FrameManager.addFrame(currFrame);
		}
		
		JFrame newFrame = null;
		try {
			newFrame = FrameManager.getFrame(className);
			if(newFrame == null)
			{
				PerformanceStats pStats = new PerformanceStats("MASSIM - " + className.getName());
				pStats.startTracking();
				
				newFrame = (JFrame) className.newInstance();
				newFrame.setVisible(true);
				
				pStats.endAndPrint();
				FrameManager.addFrame(newFrame);
			}
			else
				newFrame.setVisible(true);
			
		} catch (Exception e) { e.printStackTrace(); }
		
		if(currFrame != null)
			currFrame.setVisible(false);
	}
	
	public static void openNewFrame(JFrame currFrame, JFrame newFrame)
	{
		if(newFrame == null) return;
		
		if(currFrame != null) {
			if(!FrameManager.checkFrame(currFrame.getClass()))
				FrameManager.addFrame(currFrame);
			currFrame.setVisible(false);
		}
		newFrame.setVisible(true);		
		if(!FrameManager.checkFrame(newFrame.getClass()))
			FrameManager.addFrame(newFrame);
	}
	
	public static void setMaximize(JFrame frame)
	{
		if(frame == null) return;
		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration());         
        Rectangle screenSize = frame.getGraphicsConfiguration().getBounds();
        Rectangle maxBounds = new Rectangle(screenInsets.left + screenSize.x, 
                                    screenInsets.top + screenSize.y, 
                                    screenSize.x + screenSize.width - screenInsets.right - screenInsets.left,
                                    screenSize.y + screenSize.height - screenInsets.bottom - screenInsets.top);
        frame.setMaximizedBounds(maxBounds);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}
	
	public static void setMinimize(JFrame frame)
	{
		if(frame == null) return;
		frame.setExtendedState(JFrame.ICONIFIED);
	}
	
	public static void setNormal(JFrame frame)
	{
		if(frame == null) return;
		frame.setExtendedState(frame.NORMAL);
	}
}
