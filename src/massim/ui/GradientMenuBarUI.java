package massim.ui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.MenuBarUI;

public class GradientMenuBarUI extends MenuBarUI {

	public static ComponentUI createUI(JComponent c) {
        return new GradientMenuBarUI();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
    	super.paint(g, c);
    	float[] FRACTIONS = { 0.0f, 0.22f, 0.23f, 1.0f };
    	Graphics2D g2D = (Graphics2D) g;
    	final Color[] BRIGHT_COLORS = { Color.decode("#D6DEF6"),
    	        Color.WHITE, Color.WHITE, Color.decode("#E6EEF6") };
    	 MultipleGradientPaint DARK_GRADIENT = new LinearGradientPaint(new Point2D.Double(0, 0),
    		        new Point2D.Double(0, 15), FRACTIONS, BRIGHT_COLORS);
        g2D.setPaint(DARK_GRADIENT);
        //g2D.setPaint(new GradientPaint(new Point(0, 0), Color.decode("#E6EEF6"), new Point(0, c.getHeight()), Color.decode("#E6EEF6")));
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        
    }
}
