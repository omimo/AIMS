package massim.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;

import massim.ui.config.ConfigLoader;
import massim.ui.config.ConfigProperty;
import massim.ui.config.ConfigurationValues;
import massim.ui.config.TeamConfiguration;

public class TeamPanel extends JPanel {
	
	private JScrollPane scpParams;
	private JPanel pnlColor;
	private JPanel pnlTeamParamCont;
	private JPanel panelteam;
	JToggleButton tglbtnHide;
	private TeamConfiguration teamConfig;
	private JButton btnDelete;
	private int teamId;
	LinkedList<Component> paraComponents;
	JComboBox<String> comboBox;
	JLabel lblTeamTitle;
	
	public TeamPanel(int mID, TeamConfiguration mTeamConfig, boolean bShow)
	{
		this.teamConfig = mTeamConfig;
		this.teamId = mID;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel pnlTeamMain = new JPanel();
		pnlTeamMain.setLayout(new BoxLayout(pnlTeamMain, BoxLayout.Y_AXIS));
		pnlTeamMain.setBorder(new LineBorder(new Color(150, 150, 150)));
		pnlTeamMain.setOpaque(false);
		add(pnlTeamMain);
		setOpaque(false);
		
		JPanel panelTop = new JPanel();
		panelTop.setMaximumSize(new Dimension(10000, 35));
		panelTop.setLayout(new BorderLayout(0, 0));
		pnlTeamMain.add(panelTop);
		
		JPanel panelTopLeft = new JPanel();
		panelTop.add(panelTopLeft, BorderLayout.WEST);
		
		String teamName = "New Team";
		if(teamConfig != null) {
			if(teamConfig.getPropertyValue("Team Name") != null) {
				teamName = teamConfig.getPropertyValue("Team Name");
			}
		}
		lblTeamTitle = new JLabel(teamName);
		StyleSet.setTitleFont2(lblTeamTitle);
		panelTopLeft.add(lblTeamTitle);
		
		comboBox = new JComboBox<String>();
		panelTopLeft.add(comboBox);
		bindConfigFiles();
		
		JButton btnLoad = new JButton("Load");
		panelTopLeft.add(btnLoad);
		btnLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadConfiguration();
			}
		});
		
		JButton btnSave = new JButton("Save");
		panelTopLeft.add(btnSave);
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveConfiguration(true);
				bindConfigFiles();
			}
		});
		
		JPanel pnlTopRight = new JPanel();
		panelTop.add(pnlTopRight, BorderLayout.EAST);
		
		tglbtnHide = new JToggleButton(bShow? "Hide" : "Show");
		tglbtnHide.setSelected(bShow);
		pnlTopRight.add(tglbtnHide);
		tglbtnHide.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if(scpParams != null) {
					JToggleButton btnClicked = (JToggleButton)event.getSource();
					if(btnClicked.isSelected()) {
						scpParams.setVisible(true);
						btnClicked.setText("Hide");
		    		}
		    		else {
		    			scpParams.setVisible(false);
						btnClicked.setText("Show");
		    		}	
				}
			}
		});
		
		btnDelete = new JButton("Delete");
		btnDelete.setName(getTeamId() + "");
		pnlTopRight.add(btnDelete);
		
		pnlColor = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
		pnlColor.setBackground(new Color(0xF9F9F9));
		if(teamConfig != null) {
			if(teamConfig.getPropertyValue("windowcolor") != null) {
				pnlColor.setBackground(Color.decode(teamConfig.getPropertyValue("windowcolor")));
			}
		}
		pnlColor.setPreferredSize(new Dimension(100, 25));
		StyleSet.setBorder(pnlColor, 1);
		pnlTopRight.add(pnlColor);
		
		JButton btnColor = new JButton("Color");
		StyleSet.setBorder(btnColor, 1);
		btnColor.setPreferredSize(new Dimension(50, 18));
		btnColor.addActionListener(new ActionListener()
			{
				@Override
	            public void actionPerformed(ActionEvent event)
	            {
					/*Object[] possibilities = {Color.RED, Color.GREEN, Color.YELLOW};
					Color s = (Color)JOptionPane.showInputDialog(
					                    pnlColor,
					                    "Complete the sentence:\n"
					                    + "\"Green eggs and...\"",
					                    "Customized Dialog",
					                    JOptionPane.PLAIN_MESSAGE,
					                    null,
					                    possibilities,
					                    Color.RED);
					pnlColor.add(new ColorPanel(pnlColor));*/

					Color selectedColor = JColorChooser.showDialog(pnlColor, "Pick a Color"
			                , pnlColor.getBackground());
					pnlColor.setBackground(selectedColor);
					pnlTeamParamCont.setBackground(selectedColor);
	            }
			}
		);
		pnlColor.add(btnColor);
		
		pnlTeamParamCont = new JPanel();
		pnlTeamParamCont.setBackground(pnlColor.getBackground());
		pnlTeamParamCont.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		panelteam = new JPanel();
		//panelteam.setPreferredSize(new Dimension(500, 50));
		pnlTeamParamCont.add(panelteam);
		scpParams = new JScrollPane(pnlTeamParamCont);
		scpParams.setVisible(bShow);
		scpParams.setMaximumSize(new Dimension(10000, 250));
		pnlTeamMain.add(scpParams);
		StyleSet.setEmptyBorder(scpParams, 0);
		addMarginPanel(this);
		
		createTeamParams();
	}
	
	private void addMarginPanel(JPanel panel)
	{
		JPanel panelBottom = new JPanel();
		panelBottom.setMaximumSize(new Dimension(0, 10));
		panel.add(panelBottom);
	}
	
	private void createTeamParams()
	{
		panelteam.setLayout(new GridLayout(0, 2, 5, 5));
		panelteam.setOpaque(false);
		StyleSet.setEmptyBorder(panelteam, 3);
		panelteam.removeAll();
		
		if(teamConfig != null)
		{
			paraComponents = ConfigLoader.loadPropertiesInPanel(teamConfig, panelteam, 300, new TeamParamListener());
		}
		
		if(scpParams.isVisible()) {
			panelteam.revalidate();
		}
	}
	
	public int getTeamId() {
		return teamId;
	}

	public TeamConfiguration getTeamConfig()
	{
		teamConfig.updateConfigForParams(paraComponents);
		teamConfig.updateConfigParam("WindowColor", String.format("#%02x%02x%02x", 
				pnlColor.getBackground().getRed(), pnlColor.getBackground().getGreen(), 
				pnlColor.getBackground().getBlue()));
		return teamConfig;
	}
	
	private void bindConfigFiles() {
		File dir = new File("config/teams");
		File[] files = dir.listFiles(new FilenameFilter() { 
	         public boolean accept(File dir, String filename)
             { return filename.endsWith(".xml"); }
		});
		
		comboBox.removeAllItems();
		comboBox.addItem("-----  Team Configuration  -----");
		for(int i = 0; i < files.length; i++) {
			comboBox.addItem(files[i].getName());
		}
	}
	
	private void loadConfiguration()
	{
		if(comboBox.getSelectedIndex() == 0) {
			JOptionPane.showMessageDialog(this, "Please select configuration file from the list.");
		}
		else {
			teamConfig = ConfigLoader.readConfigOfTeam(null, "config/teams/" + comboBox.getSelectedItem());
			lblTeamTitle.setText(teamConfig.getPropertyValue("Team Name"));
			createTeamParams();
		}
	}
	
	private TeamConfiguration saveConfiguration(boolean bAskForFile)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
		String strFileName = "Team" + format.format(Calendar.getInstance().getTime());
		if(bAskForFile) {
			strFileName = (String) JOptionPane.showInputDialog(this, "Please input team configuration file name:", "Team config file", JOptionPane.PLAIN_MESSAGE, null, null, strFileName);
			if(strFileName == null || strFileName.length() == 0) return null;
		}
		if(!strFileName.endsWith(".xml")) strFileName += ".xml";
		
		TeamConfiguration teamConfig = getTeamConfig();
		ConfigurationValues config = new ConfigurationValues();
		for(ConfigProperty prop : teamConfig.getProperties()) {
			if(prop != null) {
				config.add(prop.getName(), teamConfig.getPropertyValue(prop.getName()));
			}
		}
		ConfigLoader.saveConfiguration("config/teams/" + strFileName, config);
		
		if(bAskForFile) {
			JOptionPane.showMessageDialog(this, "File " + strFileName + " saved successfully.");
		}
		return teamConfig;
	}
	
	public void hideParameterPanel()
	{
		if(tglbtnHide != null && tglbtnHide.isSelected()) {
			tglbtnHide.doClick();
		}
	}
	
	public void showParameterPanel()
	{
		if(tglbtnHide != null && !tglbtnHide.isSelected()) {
			tglbtnHide.doClick();
		}
	}
	
	public void changeAgent(String strAgentType) {
		teamConfig = ConfigLoader.readConfigOfTeam(strAgentType, null);
		createTeamParams();
	}
	
	public void addDeleteEventListener(ActionListener deleteEvent) {
		btnDelete.addActionListener(deleteEvent);
	}

	public class TeamParamListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getActionCommand() != null) {
				if(event.getActionCommand().equalsIgnoreCase("comboBoxChanged")) {
					JComboBox combo = (JComboBox) event.getSource();
					if(combo.getName() != null && combo.getName().equalsIgnoreCase("Agent Type")) {
						changeAgent(combo.getSelectedItem().toString());
					}
				}
			}
		}
	}
}
