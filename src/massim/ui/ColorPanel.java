package massim.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ColorPanel extends JPanel {
	public ColorPanel(JPanel pnlCont) {
		setLayout(null);
		Color[] colors = {Color.RED, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.BLUE};
		Insets insets = pnlCont.getInsets();
		int iIndex = 0;
		for(Color color : colors) {
			JPanel pnlColor = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
			pnlColor.setBackground(color);
			StyleSet.setBorder(pnlColor, 1);
			add(pnlColor);
			pnlColor.setBounds(iIndex * 25 + insets.left, insets.top, 25, 25);
		}
	}
}
