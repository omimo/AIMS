package massim.ui;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;

public class BoundedDesktopManager extends DefaultDesktopManager {

	private int DESK_PAN_WIDTH;
	private int DESK_PAN_HEIGHT;
	private int DESK_PAN_TOP_OFFSET;
	
	public BoundedDesktopManager(int width, int height, int offset)
	{
		super();
		DESK_PAN_WIDTH = width;
		DESK_PAN_HEIGHT = height;
		DESK_PAN_TOP_OFFSET = offset;
	}
	  @Override
	  public void beginDraggingFrame(JComponent f) {
	    // Don't do anything. Needed to prevent the DefaultDesktopManager setting the dragMode
	  }

	  @Override
	  public void beginResizingFrame(JComponent f, int direction) {
	    // Don't do anything. Needed to prevent the DefaultDesktopManager setting the dragMode
	  }

	  @Override
	  public void setBoundsForFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
	    boolean didResize = (f.getWidth() != newWidth || f.getHeight() != newHeight);
	    if (!inBounds((JInternalFrame) f, newX, newY, newWidth, newHeight)) {

	      int boundedX = (int) Math.min(Math.max(0, newX), DESK_PAN_WIDTH - newWidth);
	      int boundedY = (int) Math.min(Math.max(DESK_PAN_TOP_OFFSET, newY), DESK_PAN_HEIGHT - newHeight);

	      f.setBounds(boundedX, boundedY, newWidth, newHeight);
	    } else {
	      f.setBounds(newX, newY, newWidth, newHeight);
	    }
	    if(didResize) {
	      f.validate();
	    }
	  }

	  protected boolean inBounds(JInternalFrame f, int newX, int newY, int newWidth, int newHeight) {
	    if (newX < 0 || newY < DESK_PAN_TOP_OFFSET) return false;
	    if (newX + newWidth > DESK_PAN_WIDTH) return false;
	    if (newY + newHeight > DESK_PAN_HEIGHT) return false;
	    return true;
	  }
	}
