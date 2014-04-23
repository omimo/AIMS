package massim.ui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import massim.Agent;
import massim.Experiment.SimulationRange;
import massim.SimulationEngine;
import massim.Team;
import massim.agents.nohelp.NoHelpTeam;

public class ChartsPanel extends JScrollPane {
	private HashMap<String, VisualBox> map = new HashMap<String, VisualBox>();
	int boxRow, boxCol; JLabel lblNoCharts; JPanel pnlMain;
	private JInternalFrame parentIntFrame; 
	private JFrame parentFrame; 
	public ChartsPanel() {
		   
		   Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	       setSize(screenSize.width - 100, screenSize.height - 100);
	       
	       pnlMain = new JPanel();
	       setViewportView(pnlMain);
	       pnlMain.setLayout(new GridBagLayout());
	       boxRow = 0; boxCol = 0;
	       
	       lblNoCharts = new JLabel("No charts exist. Please wait till the first set of runs finish.");
	       StyleSet.setTitleFont(lblNoCharts);
	       pnlMain.add(lblNoCharts);
	}
	
	public void addData(List<SimulationRange> lstSimulationParams, double[] teamScores, String[] teamNames)
	{
		if(teamScores.length != teamNames.length) return;
		
		String strKey = getParamString(lstSimulationParams);
		VisualBox vBox = null;
		for(String strKeyName : map.keySet()) {
			if(strKeyName.equalsIgnoreCase(strKey)) {
				vBox = map.get(strKeyName);
				break;
			}
		}
		if(vBox == null) {
			vBox = new VisualBox(strKey, lstSimulationParams.get(lstSimulationParams.size() - 1).getProperty(), "Team Score", teamNames);
			if(boxRow == 0 && boxCol == 0 && lblNoCharts != null && lblNoCharts.isVisible()) {
				lblNoCharts.setVisible(false);
				remove(lblNoCharts);
				lblNoCharts = null;
			}
			vBox.setPreferredSize(new Dimension(500, 500));
			pnlMain.add(vBox, new GridBagConstraints(0, boxRow, 1, 1, 0.1, 1.0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
			boxRow++;
			//if(boxCol > 2) { boxCol = 0; boxRow++; }
			map.put(strKey, vBox);
			StyleSet.setBorder(vBox, 1);
		}
		vBox.addData(lstSimulationParams.get(lstSimulationParams.size() - 1).getCurrentValue(), teamScores);
		vBox.setBackground(Color.YELLOW);
		validate();
		repaint();
	}
	
	private String getParamString(List<SimulationRange> lstSimulationParams)
	{
		String strText =  "";
		for(int index = 0; index < lstSimulationParams.size() - 1; index++) {
			strText += lstSimulationParams.get(index).getProperty() + " = " + lstSimulationParams.get(index).getCurrentValue() + "\n"; 
		}
		return strText;
	}
	
	public void registerKeyDispatcher(KeyEventDispatcher dispatcher)
	{
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(dispatcher);
	}
	
	public static void setNativeLookAndFeel() {
	    try {
	      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch(Exception e) {
	      System.out.println("Error setting native LAF: " + e);
	    }
	}

	public JInternalFrame getParentIntFrame() {
		return parentIntFrame;
	}

	public void setParentIntFrame(JInternalFrame parentIntFrame) {
		this.parentIntFrame = parentIntFrame;
	}

	public JFrame getParentFrame() {
		return parentFrame;
	}

	public void setParentFrame(JFrame parentFrame) {
		this.parentFrame = parentFrame;
	}

	public String getData() {
		StringBuffer buffer = new StringBuffer();
		for(String strKeyName : map.keySet()) {
			VisualBox vBox = map.get(strKeyName);
			buffer.append(strKeyName + "\n");
			buffer.append(vBox.getXAxisName() + ",");
			for(String strSeries: vBox.getSeriesNames()) {
				buffer.append(strSeries + ",");
			}
			buffer.append("\n");
			double[][] data = vBox.getData();
			for(int row = 0; row < data.length; row++) {
				for(int col = 0; col < data[row].length; col++) {
					buffer.append(data[row][col] + ",");
				}
				buffer.append("\n");
			}
			buffer.append("\n\n\n");
		}
		return buffer.toString();
	}
}