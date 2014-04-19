package massim.ui.frames;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Point;
import java.awt.Robot;
import java.awt.ScrollPane;
import java.awt.TextField;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.DefaultDesktopManager;
import javax.swing.DesktopManager;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import java.awt.Label;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalBorders.ToolBarBorder;
import javax.swing.ImageIcon;

import massim.Experiment.AgentStats;
import massim.ui.BoundedDesktopManager;
import massim.ui.ChartsPanel;
import massim.ui.FrameManager;
import massim.ui.RunTeamPanel;
import massim.ui.RunTeamPanel.WindowState;
import massim.ui.StyleSet;
import massim.ui.config.ConfigConnector;
import massim.ui.config.ExperimentConfiguration;
import massim.ui.config.TeamConfiguration;

public class RunContainerFrame extends JFrame {

	JDesktopPane desktop;
	Dimension screenSize;
	private int DESK_PAN_WIDTH;
	private int DESK_PAN_HEIGHT;
	JInternalFrame expConfigFrame, logFrame;
	JTextArea textExpConfig, textDetailLog;
	JTextArea textExpParams = new JTextArea();
	ExperimentConfiguration expConfig; ChartsPanel chartPanel;
	ConfigConnector connector;
	List<RunTeamPanel> lstTeamPanels;
	JLabel lblSlider; JLabel lblDelayTimer, lblPauseStatus;
	JSlider slider; JButton btnStopCancel, btnPause;
	boolean bValidConfiguration = false;
	List<JMenuItem> menuItems = new ArrayList<JMenuItem>();
	long lastNextRun; JTextField textNumOfRuns;
	
	public RunContainerFrame()
	{
		if(Toolkit.getDefaultToolkit() != null)
			screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		DESK_PAN_WIDTH = Math.max(screenSize.width, 1024);
		DESK_PAN_HEIGHT = Math.max(screenSize.height, 700) - 105;
		
		desktop = new JDesktopPane();
		desktop.setBounds(0, 30, DESK_PAN_WIDTH, DESK_PAN_HEIGHT);
		
		FrameManager.setMaximize(this);
		setMinimumSize(new Dimension(Math.min(screenSize.width, 800), Math.min(screenSize.height, 700)));
		
		JScrollPane pane = new JScrollPane();
		pane.setViewportView(desktop);
		setContentPane(pane);
		
		setTitle("MASSIM - Run Simulation");
		desktop.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
	    panel.setBorder(new LineBorder(new Color(0, 0, 0)));
	    panel.setBackground(new Color(245, 245, 245));
	    desktop.add(panel, BorderLayout.NORTH);
	    
	    JLabel label = new JLabel("Running Simulation");
	    panel.add(label);
	    StyleSet.setTitleFont(label);
	    
		DesktopManager manager = new BoundedDesktopManager(DESK_PAN_WIDTH, DESK_PAN_HEIGHT, panel.getHeight());
	    desktop.setDesktopManager(manager);
	    
	    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                if(btnStopCancel != null) 
                	btnStopCancel.doClick();
                else
                	openConfiguration(null);
            }
        });
		addWindowStateListener(new WindowStateListener() {
			@Override
			public void windowStateChanged(WindowEvent event) {
				int oldState = event.getOldState();
		        int newState = event.getNewState();
		        
		         if ((oldState & Frame.ICONIFIED) == 0 && (newState & Frame.ICONIFIED) != 0) {
					if(lstTeamPanels != null) {
						for(RunTeamPanel panel : lstTeamPanels) {
							if(panel != null && panel.getParentFrame() != null && panel.getParentFrame().isVisible())
							{
								panel.setParentExtendState(panel.getParentFrame().getExtendedState());
								FrameManager.setMinimize(panel.getParentFrame());
							}
						}
					}
				} else if ((oldState & Frame.ICONIFIED) != 0 && (newState & Frame.ICONIFIED) == 0) {
					if(lstTeamPanels != null) {
						for(RunTeamPanel panel : lstTeamPanels) {
							if(panel != null && panel.getParentFrame() != null && panel.getParentFrame().isVisible())
							{
								panel.getParentFrame().setExtendedState(panel.getParentExtendState());
							}
						}
					}
				}
			}
		});
	}
	
	private void setToolBar(boolean isBatchMode)
	{
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		desktop.add(toolBar, BorderLayout.SOUTH);
		StyleSet.setBorder(toolBar, 1, 0 , 0 , 0);
		
		JPanel panelTool = new JPanel();
		toolBar.add(panelTool, BorderLayout.CENTER);
		panelTool.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JPanel panelToolR = new JPanel();
		toolBar.add(panelToolR, BorderLayout.EAST);
		panelToolR.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		if(isBatchMode) {
			
			JButton btnStart = new JButton("Start");
			btnStart.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(connector != null) {
						if(!connector.isActive())
							connector.runSimulation();
						if(btnStopCancel != null) {
							btnStopCancel.setText("Stop");
						}
					}
					if(e.getSource() instanceof JButton)
						((JButton)e.getSource()).setEnabled(false);
				}
			});
			panelTool.add(btnStart);
			
			btnStopCancel = new JButton("Cancel");
			btnStopCancel.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if(connector != null && connector.isActive()) {
						stopSimulation();
					} else {
						openConfiguration(null);
					}
				}
			});
			panelTool.add(btnStopCancel);
			
			JLabel lblCaption = new JLabel("Number of Runs = ");
			panelTool.add(lblCaption);
			
			textNumOfRuns = new JTextField();
			textNumOfRuns.setColumns(10);
			panelTool.add(textNumOfRuns);
			JButton btnUpdate = new JButton("Update");
			btnUpdate.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					updateNumOfRuns();
				}
			});
			panelTool.add(btnUpdate);
			
			JToggleButton tgbtnExperimentConfig = new JToggleButton("Simulation Configuration");
			panelToolR.add(tgbtnExperimentConfig);
			tgbtnExperimentConfig.setSelected(true);
			tgbtnExperimentConfig.addActionListener(new ActionListener() {
				@Override
	            public void actionPerformed(ActionEvent event)
	            {
					if(expConfigFrame != null)
						expConfigFrame.setVisible(((JToggleButton) event.getSource()).isSelected());
	            }
			});
			
		} else {
			
			JPanel pnlSlider = new JPanel();
			pnlSlider.setLayout(new BoxLayout(pnlSlider, BoxLayout.Y_AXIS));
			pnlSlider.setOpaque(false);
			pnlSlider.setMaximumSize(new Dimension(150, 25));
			panelTool.add(pnlSlider);
			
			slider = new JSlider();
			pnlSlider.add(slider);
			//slider.setMaximumSize(new Dimension(80, 15));
			slider.setSnapToTicks(true);
			slider.setPaintLabels(false);
			slider.setMinimum(0);
			slider.setMaximum(60);
			slider.setMajorTickSpacing(10);
			slider.setMinorTickSpacing(1);
			slider.setValue(20);
			slider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if(e.getSource() instanceof JSlider)
						lblSlider.setText("Delay: " + ((JSlider)e.getSource()).getValue() + " seconds");
					if(connector != null)
						connector.setThreadDelay(slider.getValue());
				}
			});
			
			lblSlider = new JLabel("Delay: " + slider.getValue() + " seconds");
			pnlSlider.add(lblSlider);
			
			JCheckBox chckbxNewCheckBox = new JCheckBox("Enable Logging");
			chckbxNewCheckBox.setSelected(true);
			chckbxNewCheckBox.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent event) {
					if(connector != null && event.getSource() instanceof JCheckBox) {
						connector.setLogOn(((JCheckBox)event.getSource()).isSelected());
					}
				}
			});
			panelTool.add(chckbxNewCheckBox);
			chckbxNewCheckBox.setVerticalAlignment(SwingConstants.TOP);
			
			JButton btnStart = new JButton("Start");
			btnStart.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(connector != null) {
						if(!connector.isActive())
							connector.runSimulation();
						else
							connector.clearStepMode();
						btnStopCancel.setText("Stop");
						cancelIfPaused();
					}
				}
			});
			panelTool.add(btnStart);
		
			JMenuBar menuBar = new JMenuBar() {
				@Override
			    public void paintComponent(Graphics g) {
			    	super.paintComponent(g);
			    	float[] FRACTIONS = { 0.0f, 0.30f, 1.0f };
			    	if(g != null && g instanceof Graphics2D) {
				    	Graphics2D g2D = (Graphics2D) g;
				    	Color[] BRIGHT_COLORS = { Color.decode("#CCCCF6"),
				    	        Color.WHITE, Color.decode("#CCCCF6") };
				    	MultipleGradientPaint DARK_GRADIENT = new LinearGradientPaint(new Point2D.Double(0, 0),
				    		        new Point2D.Double(0, getHeight()), FRACTIONS, BRIGHT_COLORS);
				        g2D.setPaint(DARK_GRADIENT);
			    	}
			    	g.fillRect(0, 0, getWidth(), getHeight());
			    }
			};
			menuBar.setOpaque(false);
			menuBar.setPreferredSize(new Dimension(90, 25));
			StyleSet.setBorder(menuBar, 1);
			panelTool.add(menuBar, BorderLayout.EAST);
			
			JMenu dispMenu = new JMenu("Fast Forward");
			dispMenu.setPreferredSize(new Dimension(90, 25));
			menuBar.add(dispMenu);
			
			JMenuItem cbItem = new JMenuItem("Round");
			cbItem.setHorizontalTextPosition(JMenuItem.RIGHT);
			cbItem.setAccelerator(KeyStroke.getKeyStroke('D', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			dispMenu.add(cbItem);
			cbItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					nextStep(0);
				}
			});
			menuItems.add(cbItem);
			
			cbItem = new JMenuItem("Match");
			cbItem.setHorizontalTextPosition(JMenuItem.RIGHT);
			cbItem.setAccelerator(KeyStroke.getKeyStroke('M', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			dispMenu.add(cbItem);
			cbItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					nextStep(1);
				}
			});
			menuItems.add(cbItem);
			
			cbItem = new JMenuItem("Run");
			cbItem.setHorizontalTextPosition(JMenuItem.RIGHT);
			cbItem.setAccelerator(KeyStroke.getKeyStroke('R', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			dispMenu.add(cbItem);
			cbItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					nextStep(2);
				}
			});
			menuItems.add(cbItem);
			
			cbItem = new JMenuItem("Experiment");
			cbItem.setHorizontalTextPosition(JMenuItem.RIGHT);
			cbItem.setAccelerator(KeyStroke.getKeyStroke('E', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
			dispMenu.add(cbItem);
			cbItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					nextStep(3);
				}
			});
			menuItems.add(cbItem);
			
			btnPause = new JButton("Pause");
			panelTool.add(btnPause);
			btnPause.setEnabled(false);
			btnPause.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					if(connector != null && event.getSource() instanceof JButton) {
						JButton btnClicked = ((JButton)event.getSource());
						lblPauseStatus.setVisible(true);
						if(btnClicked.getText() != null 
								&& btnClicked.getText().equalsIgnoreCase("Pause")) {
							connector.pauseSimulation();
							btnClicked.setText("Resume");
							lblPauseStatus.setText("Paused");
							btnClicked.setBackground(Color.LIGHT_GRAY);
						} else {
							connector.resumeSimulation();
							btnClicked.setText("Pause");
							lblPauseStatus.setText("");
							btnClicked.setBackground(null);
						}
					}
				}
			});
			
			btnStopCancel = new JButton("Cancel");
			btnStopCancel.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if(connector != null && connector.isActive()) {
						stopSimulation();
					} else {
						openConfiguration(null);
					}
				}
			});
			panelTool.add(btnStopCancel);
			
			JSeparator separator_1 = new JSeparator();
			panelTool.add(separator_1);
			separator_1.setOrientation(SwingConstants.VERTICAL);
			
			JButton btnConfigure = new JButton("Configure");
			btnConfigure.setVisible(false);
			panelTool.add(btnConfigure);
			
			lblPauseStatus = new JLabel();
			panelToolR.add(lblPauseStatus);
			
			lblDelayTimer = new JLabel();
			panelToolR.add(lblDelayTimer);
			
			JToggleButton tgbtnChart = new JToggleButton("Simulation Charts");
			panelToolR.add(tgbtnChart);
			tgbtnChart.setSelected(false);
			tgbtnChart.addActionListener(new ActionListener() {
				@Override
	            public void actionPerformed(ActionEvent event)
	            {
					if(chartPanel != null && chartPanel.getParentFrame() != null)
						chartPanel.getParentFrame().setVisible(((JToggleButton) event.getSource()).isSelected());
	            }
			});
			
			JToggleButton tgbtnLog = new JToggleButton("Execution Log");
			panelToolR.add(tgbtnLog);
			tgbtnLog.setSelected(false);
			tgbtnLog.addActionListener(new ActionListener() {
				@Override
	            public void actionPerformed(ActionEvent event)
	            {
					if(logFrame != null)
						logFrame.setVisible(((JToggleButton) event.getSource()).isSelected());
	            }
			});
			
			JToggleButton tgbtnExperimentConfig = new JToggleButton("Simulation Configuration");
			panelToolR.add(tgbtnExperimentConfig);
			tgbtnExperimentConfig.setSelected(true);
			tgbtnExperimentConfig.addActionListener(new ActionListener() {
				@Override
	            public void actionPerformed(ActionEvent event)
	            {
					if(expConfigFrame != null)
						expConfigFrame.setVisible(((JToggleButton) event.getSource()).isSelected());
	            }
			});
			
		}
	}
	
	protected void updateNumOfRuns() {
		if(textNumOfRuns != null) {
			int numOfRuns = 0;
			try {
				numOfRuns = Integer.parseInt(textNumOfRuns.getText());
			} catch(NumberFormatException ex) { }
			if(numOfRuns > 0) {
				expConfig.updateConfigParam("Number of Runs", numOfRuns + "");
				JOptionPane.showMessageDialog(this, "The Number of Runs updated successfully.");
			}
			textNumOfRuns.setText(expConfig.getPropertyValue("Number of Runs"));
			if(textExpConfig != null)
				textExpConfig.setText(expConfig.toStringParams());
		}
	}

	protected void nextStep(int type) {
		long newStamp = Calendar.getInstance().getTimeInMillis();
		if(newStamp < (lastNextRun + 500)) return;
		lastNextRun = newStamp;
		
		if(connector != null) {
			if(!connector.isActive()) { 
				connector.runSimulation();
				btnStopCancel.setText("Stop");
			}
			if(type == 0)
				connector.nextRound();
			else if(type == 1)
				connector.nextMatch();
			else if(type == 2)
				connector.nextRun();
			else if(type == 3)
				connector.nextExperiment();
			cancelIfPaused();
		}
	}

	public boolean setConfiguration(ExperimentConfiguration mExpConfig, boolean isBatchMode)
	{
		if(mExpConfig == null) return false;
		
		this.expConfig = mExpConfig;
		setToolBar(isBatchMode);
		if(textNumOfRuns != null)
			textNumOfRuns.setText(expConfig.getPropertyValue("Number of Runs"));
		
		connector = new ConfigConnector();
		connector.setBatchMode(isBatchMode);
		long experimentId = connector.initExperiment(expConfig);
		if(connector.initSimulation()) {
			bValidConfiguration = true;
			lstTeamPanels = new ArrayList<RunTeamPanel>();
			if(slider != null)
				connector.setThreadDelay(slider.getValue());
			connector.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent event) {
					updateFrames(event.getPropertyName(), event.getNewValue());
					if(event.getPropertyName() != null 
							&& event.getPropertyName().equalsIgnoreCase("Waiting") 
							&& connector != null && !connector.isInStepMode()  && !connector.isPaused()) {
						lblDelayTimer.setVisible(true);
						Timer timer = new Timer();
				        timer.scheduleAtFixedRate(new TimerTask() {
				            int seconds = slider.getValue();
				            public void run() {
				            	seconds--;
				            	lblDelayTimer.setText(seconds + " Seconds before next step.");
				                if (seconds < 1 || connector.isInStepMode() || connector.isPaused()) {
				                	lblDelayTimer.setText("");
				                	cancel();
				                }
				            }
				        }, 1000, 1000);
					}
				}
			});
			addExperimentConfiguration();
			createChartFrame(isBatchMode);
			if(!isBatchMode) {
				createLogFrame();
				addFrames();
			}
		} else {
			bValidConfiguration = false;
			openConfiguration(this);
		}
		return bValidConfiguration;
	}
	
	private boolean isValidConfiguration() {
		return bValidConfiguration;
	}
	
	public void setDelay(int threadDelay) {
		if(slider != null)
			slider.setValue(threadDelay);
	}
	
	private void cancelIfPaused()
	{
		lblPauseStatus.setVisible(false);
		btnPause.setText("Pause");
		btnPause.setEnabled(true);
		btnPause.setBackground(null);
	}
	
	public void setVisible(boolean bVisible) {
		if(bVisible && !isValidConfiguration()) {
			super.setVisible(true);
			if(connector != null)
				JOptionPane.showMessageDialog(this, "Config validation failed with following errors:\n" + connector.getErrorMessage());
			else
				JOptionPane.showMessageDialog(this, "Config is invalid. Please check if all parameters are correct.");
			bVisible = false;
			FrameManager.removeFrame(this);
		}
		super.setVisible(bVisible);
	}
	
	private void addFrames()
	{
		int teamSize = Integer.parseInt(expConfig.getPropertyValue("Team Size"));
		int boardSize = Integer.parseInt(expConfig.getPropertyValue("Board Size"));
		
		int iIndex = 0;
		for(TeamConfiguration teamConfig : expConfig.getTeams()) {
			if(teamConfig == null) continue;
			createFrame(iIndex, teamSize, boardSize, teamConfig);
			iIndex++;
		}
		desktop.add(new JInternalFrame("Team Name", true, false, false, true));
	}
	
	private void createFrame(int iIndex, int iTeamSize, int iBoardSize, TeamConfiguration teamConfig)
	{
		JInternalFrame frame = createInternalFrameInstance(teamConfig.getPropertyValue("Team Name"), iIndex);		
		int width = desktop.getBounds().width;
		int height = desktop.getBounds().height - 70;
		frame.setSize((width - 50) / 2, height/ 2);
		frame.setLocation(getFrameLocation(iIndex, width, height));
		
		RunTeamPanel controller = new RunTeamPanel(iTeamSize, iBoardSize, teamConfig, WindowState.Internal);
		controller.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if(event.getPropertyName() != null 
						&& event.getPropertyName().equalsIgnoreCase("WindowState")) {
					if(event.getSource() instanceof RunTeamPanel) {
						RunTeamPanel controller = (RunTeamPanel)event.getSource();
						if((WindowState)event.getNewValue() == WindowState.Independent) {
							if(controller.getParentFrame() == null) {
								controller.setParentFrame(new JFrame());
								controller.getParentFrame().setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
								controller.getParentFrame().addWindowListener(new WindowAdapter() {
						            @Override
						            public void windowClosing(WindowEvent we) {
						            	if(we.getSource() instanceof JFrame && ((JFrame)we.getSource()).getContentPane() != null) {
							            	if(((JFrame)we.getSource()).getContentPane().getComponentCount() > 0 &&
							            			((JFrame)we.getSource()).getContentPane().getComponent(0) instanceof RunTeamPanel)
							            	{
							            		((RunTeamPanel)((JFrame)we.getSource()).getContentPane().getComponent(0)).moveToWindow();
							            	}
							            	((JFrame)we.getSource()).setVisible(false);
						            	}
						            }
						        });
							}
							controller.getParentFrame().add(controller);
							controller.getParentFrame().pack();
							controller.getParentFrame().setVisible(true);
							if(controller.getParentIntFrame() != null) {
								controller.getParentIntFrame().setVisible(false);
							}
						} else {
							if(controller.getParentIntFrame() == null) {
								JInternalFrame frame = createInternalFrameInstance(controller.getTeamName(), 0);
								controller.setParentIntFrame(frame);
							}
							controller.getParentIntFrame().add(controller);
							controller.getParentIntFrame().setVisible(true);
							if(controller.getParentFrame() != null) {
								controller.getParentFrame().setVisible(false);
							}
						}
					}
				}
			}
		});
		controller.setParentIntFrame(frame);
		controller.registerKeyDispatcher(new RunKeyDispatcher());
		frame.add(controller);
		lstTeamPanels.add(controller);
		Dimension maxSize = controller.getMaximumSize();
		if(maxSize.width > width) maxSize.width = width;
		if(maxSize.height > height) maxSize.height = height;
		frame.setMaximumSize(maxSize);
	}
	
	private JInternalFrame createInternalFrameInstance(String teamName, int iIndex)
	{
		JInternalFrame frame = new JInternalFrame(teamName, true, false, false, true);
		if(frame.getUI() != null 
				&& ((javax.swing.plaf.basic.BasicInternalFrameUI) frame.getUI()).getNorthPane() != null)
			((javax.swing.plaf.basic.BasicInternalFrameUI) frame.getUI()).getNorthPane().remove(0);
		frame.setBorder(new LineBorder(new Color(70, 70, 70), 2));
		frame.setFrameIcon(new ImageIcon(RunContainerFrame.class.getResource("/massim/ui/images/Recording-ico.png")));
		
		frame.setVisible(true);
		desktop.add(frame);
		return frame;
	}
	
	private Point getFrameLocation(int iIndex, int width, int height)
	{
		switch (iIndex) {
		case 0:
			return new Point(10, 45);
		case 1:
			return new Point(width / 2, 45);
		case 2:
			return new Point(10, (height / 2) + 55);
		case 3:
			return new Point(width / 2, (height / 2) + 55);
		default:
			break;
		}
		return new Point(10, 45);
	}
	
	private void createLogFrame()
	{
		logFrame = new JInternalFrame("Execution Log", true, false, false, true);
		if(logFrame.getUI() != null 
				&& ((javax.swing.plaf.basic.BasicInternalFrameUI) logFrame.getUI()).getNorthPane() != null)
			((javax.swing.plaf.basic.BasicInternalFrameUI) logFrame.getUI()).getNorthPane().remove(0);
		logFrame.setBorder(new LineBorder(new Color(70, 70, 70), 2));
		logFrame.setFrameIcon(new ImageIcon(RunContainerFrame.class.getResource("/massim/ui/images/Recording-ico.png")));		
		int width = screenSize.width;
		int height = desktop.getBounds().height - 70;
		logFrame.setSize((width - 50) / 2, height/ 2);
		logFrame.setLocation(getFrameLocation(3, width, height));
		
		JScrollPane scrollPane = new JScrollPane();
		logFrame.add(scrollPane);
		
		JPanel pnlLog = new JPanel();
		pnlLog.setLayout(new BoxLayout(pnlLog, BoxLayout.Y_AXIS));
		scrollPane.setViewportView(pnlLog);
		
		textDetailLog = new JTextArea();
		pnlLog.add(textDetailLog);
		textDetailLog.setEditable(false);
        StyleSet.setRegular(textDetailLog);
		
		logFrame.setVisible(false);
		desktop.add(logFrame);
	}
	
	private void createChartFrame(boolean isBatchMode)
	{
		chartPanel = new ChartsPanel();
		if(isBatchMode) {
			JInternalFrame chartFrame = createInternalFrameInstance("Charts", 0);
			chartFrame.add(chartPanel);
			chartPanel.setParentIntFrame(chartFrame);
			int width = desktop.getBounds().width;
			int height = desktop.getBounds().height - 70;
			desktop.setPreferredSize(new Dimension(width- 5, height));
		} else {
			JFrame chartFrame = new JFrame();
			chartFrame.setTitle("MASSIM - Charts");
			chartFrame.setLocationRelativeTo(null);
			chartFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			chartFrame.addWindowListener( new WindowAdapter() {
		            @Override
		            public void windowClosing(WindowEvent we) {
		            	((JFrame)we.getSource()).setVisible(false);
		            }
			});
			FrameManager.setMaximize(chartFrame);
			chartFrame.add(chartPanel);
			chartPanel.registerKeyDispatcher(new RunKeyDispatcher());
			chartPanel.setParentFrame(chartFrame);
		}
	}
	
	private void updateFrames(String strPropertyName, Object propValue) 
	{
		if(strPropertyName == null || strPropertyName.length() == 0) return;
		if(strPropertyName.equalsIgnoreCase("RunInit")) {
			String strParamText = (propValue == null ? "" : propValue.toString());
			textExpParams.setText(strParamText.replaceAll("\t", "\n   "));
			for(RunTeamPanel team : lstTeamPanels) {
				team.newRunInitialized();
			}
		}
		else if(strPropertyName.equalsIgnoreCase("Step")) {
			String strStatus = (propValue == null ? "" : propValue.toString());
			if(strStatus.equalsIgnoreCase("Init")) {
				lblDelayTimer.setVisible(true);
				lblDelayTimer.setText("Working on step..");
			} else if(!strStatus.equalsIgnoreCase("End")) {
				lblDelayTimer.setVisible(true);
				lblDelayTimer.setText("Working on step (Loading " + strStatus + ")");
			} else if(strStatus.equalsIgnoreCase("End")) {
				lblDelayTimer.setVisible(false);
				lblDelayTimer.setText("");
			}
		}
		else if(strPropertyName.equalsIgnoreCase("AgentStats")
				&& propValue != null && propValue instanceof List<?>) {
			List<List<AgentStats>> lstAgentStats = (List<List<AgentStats>>)propValue;
			if(lstTeamPanels.size() == 0) addFrames();
			int index = 0;
			for(RunTeamPanel team : lstTeamPanels) {
				if(team != null && lstAgentStats.size() > index) {
					team.updateAgentStats(lstAgentStats.get(index), (connector != null && !connector.isInStepMode() && slider != null) ? slider.getValue() : 0);
					team.revalidate();
				}
				index++;
			}
		}
		else if(strPropertyName.equalsIgnoreCase("Board")) {
			int[][] theBoard = (int[][])propValue;
			if(lstTeamPanels.size() == 0) addFrames();
			for(RunTeamPanel team : lstTeamPanels) {
				if(team != null) {
					team.updateBoard(theBoard);
					team.revalidate();
				}
			}
		}
		else if(strPropertyName.equalsIgnoreCase("TeamScore")) {
			List<Integer> lstTeamScores = (List<Integer>)propValue;
			int index = 0;
			for(RunTeamPanel team : lstTeamPanels) {
				if(team != null && lstTeamScores.size() > index) {
					team.updateTeamScore(lstTeamScores.get(index));
					team.revalidate();
				}
				index++;
			}
		}
		else if(strPropertyName.equalsIgnoreCase("ExpScores")) {
			int[] lstTeamScores = (int[])propValue;
			double[] teamScores = new double[lstTeamScores.length];
			String[] teamNames = new String[lstTeamScores.length];
			for(int index = 0; index < lstTeamScores.length; index++) {
				teamScores[index] = lstTeamScores[index];
				teamNames[index] = expConfig.getTeams().get(index).getPropertyValue("Team Name");
			}
			chartPanel.addData(connector.getCurrentSimulationParameters(), teamScores, teamNames);
		}
		else if(strPropertyName.equalsIgnoreCase("Log")) {
			String detailLog = propValue + "";
			textDetailLog.setText(textDetailLog.getText() + "\n" + detailLog);
		}
		else if(strPropertyName.equalsIgnoreCase("TeamLog")) {
			List<String> lstDetailLogs = (List<String>)propValue;
			int index = 0;
			for(RunTeamPanel team : lstTeamPanels) {
				if(team != null) {
					team.updateLog(lstDetailLogs.get(index));
					team.revalidate();
				}
				index++;
			}
		}
		else if(strPropertyName.equalsIgnoreCase("SimComplete")) {
			JOptionPane.showMessageDialog(this, "The simulation finished successfully.");
			if(lblDelayTimer != null)
				lblDelayTimer.setVisible(false);
			if(btnStopCancel != null) {
				btnStopCancel.setText("Finish");
			}
		}
	}
	
	private void addExperimentConfiguration()
	{
		if(expConfigFrame == null) {
			expConfigFrame = new JInternalFrame("");
			if(expConfigFrame.getUI() != null 
					&& ((javax.swing.plaf.basic.BasicInternalFrameUI) expConfigFrame.getUI()).getNorthPane() != null)
				((javax.swing.plaf.basic.BasicInternalFrameUI) expConfigFrame.getUI()).setNorthPane(null);
			StyleSet.setBorder(expConfigFrame, 1);
			expConfigFrame.setSize(new Dimension(202, 404));
			expConfigFrame.setLayout(new BoxLayout(expConfigFrame.getContentPane(), BoxLayout.Y_AXIS));
			expConfigFrame.setVisible(true);
			expConfigFrame.setLocation(DESK_PAN_WIDTH - 210, desktop.getBounds().height - 400);
			expConfigFrame.setLayer(1);
			desktop.add(expConfigFrame, BorderLayout.EAST);
			
			JPanel pnlExpParams = new JPanel();
			pnlExpParams.setLayout(new BorderLayout());
			expConfigFrame.getContentPane().add(pnlExpParams);
			JLabel lblTitle = new JLabel("Simulation Params:");
			StyleSet.setTitleFont2(lblTitle);
			lblTitle.setHorizontalAlignment(Label.LEFT);
			pnlExpParams.add(lblTitle, BorderLayout.NORTH);
			textExpConfig = new JTextArea();
			pnlExpParams.add(textExpConfig, BorderLayout.CENTER);
			textExpConfig.setEditable(false);
			textExpConfig.setPreferredSize(new Dimension(202, 200));
			textExpConfig.setAutoscrolls(true);
	        StyleSet.setRegular(textExpConfig);
	        StyleSet.setEmptyBorder(textExpConfig, 5);
	        
	        JPanel pnlChngParams = new JPanel();
	        pnlChngParams.setLayout(new BorderLayout());
			expConfigFrame.getContentPane().add(pnlChngParams);
	        lblTitle = new JLabel("Parameters of current Exp.:");
	        lblTitle.setHorizontalAlignment(Label.LEFT);
	        StyleSet.setTitleFont2(lblTitle);
	        pnlChngParams.add(lblTitle, BorderLayout.NORTH);
			
	        pnlChngParams.add(textExpParams, BorderLayout.CENTER);
			textExpParams.setEditable(false);
			textExpParams.setPreferredSize(new Dimension(202, 200));
			textExpParams.setLineWrap(true);
			StyleSet.setRegular(textExpParams);
	        StyleSet.setEmptyBorder(textExpParams, 5);
		}
		
		textExpConfig.setText(expConfig.toStringParams());
	}
	
	private void stopSimulation()
	{
		int confirmStop = JOptionPane.showConfirmDialog(
			    this,
			    "Are you sure you want to stop this experiment?",
			    "Stop",
			    JOptionPane.YES_NO_OPTION);
		if(confirmStop == 0) {
			if(connector != null) {
				connector.stopExperiment();
			}
			openConfiguration(null);
		}
	}
	
	private void openConfiguration(JFrame currentInstance)
	{
		this.setVisible(false);
		if(lstTeamPanels != null)
		{
			for(RunTeamPanel panel : lstTeamPanels) {
				if(panel != null && panel.getParentFrame() != null)
				{
					panel.getParentFrame().setVisible(false);
					panel.getParentFrame().dispose();
				}
			}
		}
		if(chartPanel != null && chartPanel.getParentFrame() != null) {
			chartPanel.getParentFrame().setVisible(false);
			chartPanel.getParentFrame().dispose();
		}
		if(currentInstance == null) {
			FrameManager.removeFrame(this);
			this.dispose();
		}
		FrameManager.openNewFrame(currentInstance, ConfigureFrame.class);
	}

	private class RunKeyDispatcher implements KeyEventDispatcher {
		@Override
        public boolean dispatchKeyEvent(KeyEvent event) {
	        dispatchKey(event);
            return false;
        }
    }
	
	private void dispatchKey(KeyEvent event)
	{
		if (event.getID() != KeyEvent.KEY_PRESSED || !event.isControlDown()) {
			return;
		}
		
		if(menuItems != null) {
			for(JMenuItem menuItem : menuItems) {
				if(menuItem != null && menuItem.getAccelerator() != null) {
					if(menuItem.getAccelerator().getKeyCode() == event.getKeyCode()) {
						menuItem.doClick();
						return;
					}
				}
			}
		}
	}
}