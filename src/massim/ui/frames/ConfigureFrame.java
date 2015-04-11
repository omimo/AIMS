package massim.ui.frames;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
//import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CMinimizeArea;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.station.split.SplitDockProperty;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.gui.dock.util.extension.Extension;
import bibliothek.gui.dock.util.extension.ExtensionManager;

import javax.swing.JToolBar;
import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JCheckBox;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.SliderUI;

import massim.ui.ExpParamsPane;
import massim.ui.FrameManager;
import massim.ui.TeamsPanel;
import massim.ui.config.ConfigConnector;
import massim.ui.config.ConfigLoader;
import massim.ui.config.ConfigProperty;
import massim.ui.config.ConfigurationValues;
import massim.ui.config.ExperimentConfiguration;
import massim.ui.config.TeamConfiguration;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ConfigureFrame extends JFrame {

	private static final long serialVersionUID = -4224742147367515845L;
	private ExpParamsPane expParams;
	private TeamsPanel teams;
	private JLabel lblSlider;
	private JSlider slider; JRadioButton radioBatch;
	
	private ActionListener btnClick = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btnClicked = (JButton) e.getSource();
			if(btnClicked.getName() != null)
			{
				if(btnClicked.getName().equals("btnHome"))
					openWelcome();
				else
					openRun();
			}
		}
		
	};
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
		Object key = null;
		while(keys.hasMoreElements())
		{
			key = keys.nextElement();
			if(key.toString().endsWith(".font"))
				UIManager.put(key.toString(), new Font("Segoe UI", 0, 11));
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
			    	
					ConfigureFrame frame = new ConfigureFrame();
					frame.setVisible(true);
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		//UIManager.put("MenuBarUI", "massim.ui.GradientMenuBarUI");
	}

	/**
	 * Create the frame.
	 */
	public ConfigureFrame() {
		
		/*
		System.out.println("##### Swing Statistics #####");
    	Date start = new Date();
    	*/
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		FrameManager.setMaximize(this);
		setResizable(true);
		getContentPane().setLayout(new BorderLayout(0, 0));
		setTitle("MASSIM - Configure Experiment");
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		
		JPanel panel = new JPanel();
		toolBar.add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		/*JButton btnHome = new JButton("Home");
		btnHome.setName("btnHome");
		btnHome.addActionListener(btnClick);
		panel.add(btnHome);*/
		
		JSeparator separator = new JSeparator();
		panel.add(separator);
		separator.setOrientation(SwingConstants.VERTICAL);
		
		JPanel pnlSlider = new JPanel();
		pnlSlider.setLayout(new BoxLayout(pnlSlider, BoxLayout.Y_AXIS));
		pnlSlider.setOpaque(false);
		pnlSlider.setMaximumSize(new Dimension(150, 25));
		panel.add(pnlSlider);
		
		slider = new JSlider();
		pnlSlider.add(slider);
		slider.setValue(15);
		//slider.setMaximumSize(new Dimension(80, 15));
		slider.setSnapToTicks(true);
		slider.setPaintLabels(false);
		slider.setMinimum(0);
		slider.setMaximum(60);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(e.getSource() instanceof JSlider)
					lblSlider.setText("Delay: " + ((JSlider)e.getSource()).getValue() + " seconds");
			}
		});
		
		lblSlider = new JLabel("Delay: " + slider.getValue() + " seconds");
		pnlSlider.add(lblSlider);
		
		/*JCheckBox chckbxNewCheckBox = new JCheckBox("Record Execution");
		panel.add(chckbxNewCheckBox);
		chckbxNewCheckBox.setVerticalAlignment(SwingConstants.TOP);*/
		
		JSeparator separator_1 = new JSeparator();
		panel.add(separator_1);
		separator_1.setOrientation(SwingConstants.VERTICAL);
		
		JButton btnSaveCurrentConfiguration = new JButton("Save Current Configuration");
		panel.add(btnSaveCurrentConfiguration);
		btnSaveCurrentConfiguration.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveConfiguration(true);
			}
		});
		
		JRadioButton radioDebug = new JRadioButton("Debug");
	    radioDebug.setActionCommand("Debug");
	    radioDebug.setSelected(true);
	    panel.add(radioDebug);
	    radioBatch = new JRadioButton("Batch");
	    radioBatch.setActionCommand("Batch");
	    panel.add(radioBatch);
	    ButtonGroup group = new ButtonGroup();
	    group.add(radioDebug);
	    group.add(radioBatch);
		
		JButton btnStart = new JButton("Run Simulation");
		btnStart.setName("btnStart");
		btnStart.addActionListener(btnClick);
		panel.add(btnStart);
		
		JButton btnContinue = new JButton("Resume Running");
		btnContinue.setVisible(false);
		panel.add(btnContinue);
		
		JPanel panelToolR = new JPanel();
		toolBar.add(panelToolR);
		panelToolR.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		JButton btnLoadConfiguration = new JButton("Load Configuration");
		panelToolR.add(btnLoadConfiguration);
		btnLoadConfiguration.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openConfigFiles();
			}
		});
		
		/*JButton btnSchedule = new JButton("Schedule");
		panel.add(btnSchedule);*/
		
		JSeparator separator_2 = new JSeparator();
		panel.add(separator_2);
		separator_2.setOrientation(SwingConstants.VERTICAL);
		getContentPane().add(toolBar, BorderLayout.SOUTH);
		toolBar.setBorder(new LineBorder(Color.gray, 2, true));

		ExperimentConfiguration expConfig = ConfigLoader.readConfig("config", null);
		
		DockController controller = new DockController();
		//controller.setTheme(new EclipseTheme());
		controller.setTheme(new NoStackTheme(new EclipseTheme()));
		
		SplitDockStation splitDockStation = new SplitDockStation();
        controller.add( splitDockStation );
        this.add(splitDockStation);
		
        DefaultDockable dockTeams = new DefaultDockable();
        dockTeams.setTitleText("Teams");
        
        teams = new TeamsPanel(expConfig);
        panel.setOpaque(false);
        dockTeams.add(teams);
        splitDockStation.drop(dockTeams, SplitDockProperty.EAST);
        
        DefaultDockable dockExpParams = new DefaultDockable();
        dockExpParams.setTitleText("Simulation Parameters");
        
        expParams = new ExpParamsPane(expConfig);
        panel.setOpaque(false);
        dockExpParams.add(expParams);
        splitDockStation.drop(dockExpParams, SplitDockProperty.WEST);
	}
	private void openWelcome()
	{
		FrameManager.openNewFrame(this, WelcomeFrame.class);
	}
	
	private ExperimentConfiguration getLatestConfig()
	{
		ExperimentConfiguration expConfig = expParams.getExpConfig();
		expConfig.setTeamConfigs(teams.getTeamConfigs());
		return expConfig;
	}
	
	private ExperimentConfiguration saveConfiguration(boolean bAskForFile)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
		String strFileName = "Config" + format.format(Calendar.getInstance().getTime());
		if(bAskForFile) {
			strFileName = (String) JOptionPane.showInputDialog(this, "Please input configuration file name:", "Config File", JOptionPane.PLAIN_MESSAGE, null, null, strFileName);
			if(strFileName == null || strFileName.length() == 0) return null;
		}
		if(!strFileName.endsWith(".xml")) strFileName += ".xml";
		
		ExperimentConfiguration expConfig = getLatestConfig();
		ConfigurationValues config = new ConfigurationValues();
		for(ConfigProperty prop : expConfig.getProperties()) {
			if(prop != null) {
				config.add(prop.getName(), expConfig.getPropertyValue(prop.getName()));
			}
		}
		for(TeamConfiguration teamConfig : expConfig.getTeams()) {
			if(teamConfig != null) {
				ConfigurationValues childConfig = new ConfigurationValues();
				for(ConfigProperty prop : teamConfig.getProperties()) {
					if(prop != null) {
						childConfig.add(prop.getName(), teamConfig.getPropertyValue(prop.getName()));
					}
				}
				config.add(teamConfig.getPropertyValue("Agent Type"), childConfig);
			}
		}
		ConfigLoader.saveConfiguration("config/" + strFileName, config);
		
		if(bAskForFile) {
			JOptionPane.showMessageDialog(this, "File " + strFileName + " saved successfully.");
		}
		return expConfig;
	}
	
	private void openConfigFiles() {
		File dir = new File("config");
		File[] files = dir.listFiles(new FilenameFilter() { 
	         public boolean accept(File dir, String filename)
             { return filename.endsWith(".xml"); }
		} );
		File fileSelected = (File) JOptionPane.showInputDialog(this, "Please select configuration file name:", "Config File", JOptionPane.OK_CANCEL_OPTION, null, files, null);
		if(fileSelected != null) {
			ExperimentConfiguration expConfig = ConfigLoader.readConfig("config", fileSelected.getPath());
			if(expConfig != null) {
				teams.setConfiguration(expConfig);
				expParams.setConfiguration(expConfig);
			}
		}
	}
	
	private void openRun()
	{
		ExperimentConfiguration expConfig = getLatestConfig();
		if(!FrameManager.checkFrame(RunContainerFrame.class)) {
			FrameManager.addFrame(new RunContainerFrame());
		}
		RunContainerFrame frame = (RunContainerFrame)FrameManager.getFrame(RunContainerFrame.class);
		frame.setConfiguration(expConfig, radioBatch.isSelected());
		frame.setDelay(slider.getValue());
		FrameManager.openNewFrame(this, RunContainerFrame.class);
	}
}