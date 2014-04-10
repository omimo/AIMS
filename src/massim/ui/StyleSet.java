package massim.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.UIManager;

public class StyleSet {

	public static void doInitialStyleSettings()
	{
		java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
		Object key = null;
		while(keys.hasMoreElements())
		{
			key = keys.nextElement();
			if(key.toString().endsWith(".font"))
				UIManager.put(key.toString(), new Font("Segoe UI", 0, 11));
		}
		UIManager.put( "InternalFrame.titleFont", new Font("Segoe UI", 0, 11));
	}
	
	public static void setWelcomeFont(JComponent component)
	{
		component.setFont(new Font("Segoe UI", Font.BOLD , 20));
	}
	
	public static void setTitleFont(JComponent component)
	{
		component.setFont(new Font("Segoe UI", Font.BOLD , 14));
	}
	
	public static void setTitleFont2(JComponent component)
	{
		component.setFont(new Font("Segoe UI", Font.BOLD , 13));
	}
	
	public static void setRegular(JComponent component)
	{
		component.setFont(new Font("Segoe UI", 0, 12));
	}
	
	public static void setEmptyBorder(JComponent component, int thickness)
	{
		component.setBorder(BorderFactory.createEmptyBorder(thickness,thickness,thickness,thickness));
	}
	
	public static void setEmptyBorder(JComponent component, int top, int right, int bottom, int left)
	{
		component.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
	}

	public static void setBorder(JComponent component, int thickness)
	{
		component.setBorder(BorderFactory.createMatteBorder(thickness, thickness, thickness, thickness, Color.gray));
	}
	
	public static void setBorder(JComponent component, int top, int right, int bottom, int left)
	{
		component.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.gray));
	}
}
