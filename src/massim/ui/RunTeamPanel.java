package massim.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.Toolkit;
//import java.util.Date;

import javax.management.timer.TimerMBean;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/*import com.github.sarxos.l2fprod.sheet.AnnotatedBeanInfo;
import com.github.sarxos.l2fprod.sheet.DefaultBeanBinder;
import com.github.sarxos.l2fprod.sheet.annotation.PropertyInfo;
import com.l2fprod.common.demo.PropertySheetPage1;
import com.l2fprod.common.demo.PropertySheetPage2;
import com.l2fprod.common.demo.PropertySheetPage3;
import com.l2fprod.common.demo.PropertySheetPage4;
import com.l2fprod.common.propertysheet.PropertySheet;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.swing.LookAndFeelTweaks;

import GUI.Experiment;
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
import bibliothek.gui.dock.station.split.SplitDockProperty;*/
import javax.swing.JToolBar;
import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.ScrollPaneLayout;
import javax.swing.SwingConstants;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.TableColumn;

import massim.Experiment.AgentStats;
import massim.ui.config.ConfigProperty;
import massim.ui.config.TeamConfiguration;
import massim.ui.config.Utilities;
import massim.ui.frames.RunContainerFrame;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RunTeamPanel extends JScrollPane {

	private static final long serialVersionUID = -4224742147367515845L;
	JLayeredPane squares[][] = new JLayeredPane[10][10];
	int boardSize;
	int teamSize;
	private JTextArea textAreaScore, textDetailLog;
	private CustomTable table;
	JPanel pnlTopLeft;
	JPanel pnlTopRight;
	JPanel pnlBottom;
	ImageIcon iconWhite = new ImageIcon(RunContainerFrame.class.getResource("/massim/ui/images/white-round.png"));
	ImageIcon iconBlack = new ImageIcon(RunContainerFrame.class.getResource("/massim/ui/images/black-round.png"));
	ImageIcon iconGray = new ImageIcon(RunContainerFrame.class.getResource("/massim/ui/images/goal-icon.png"));
	ImageIcon iconCorner = new ImageIcon(RunContainerFrame.class.getResource("/massim/ui/images/left-corner.png"));
	boolean bNewRun = true;
	JPanel pnlMain; JPanel pnlLogCont; JLabel lblTeamName; JMenuItem menuItemWindow;
	WindowState winState; JInternalFrame parentIntFrame; 
	JFrame parentFrame; int parentExtendState;
	
	/**
	 * Create the frame.
	 */
	public RunTeamPanel(int iTeamSize, int iBoardSize, TeamConfiguration teamConfig, WindowState winState) {		
		
		if(teamConfig == null)
			return;
		
		this.teamSize = iTeamSize;
		this.boardSize = iBoardSize;
		this.winState = winState;
		
		Color color = Color.lightGray;
		if(teamConfig.getPropertyValue("windowcolor") != null) {
			color = Color.decode(teamConfig.getPropertyValue("windowcolor"));
		}
		setBackground(color);
		StyleSet.setEmptyBorder(this, 0);
		
		JPanel pnlContainer = new JPanel(new BorderLayout());
		this.setViewportView(pnlContainer);
		pnlContainer.setBackground(color);
		StyleSet.setEmptyBorder(pnlContainer, 5);
		
		JPanel pnlHead = new JPanel(new BorderLayout(0, 0));
		pnlContainer.add(pnlHead, BorderLayout.NORTH);
		pnlHead.setPreferredSize(new Dimension(0, 25));
		lblTeamName = new JLabel(teamConfig.getPropertyValue("Team Name"));
		StyleSet.setTitleFont2(lblTeamName);
		pnlHead.add(lblTeamName, BorderLayout.WEST);
		pnlHead.setBackground(color);
		
		pnlMain = new JPanel();
		pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
		pnlMain.setOpaque(false);
		pnlContainer.add(pnlMain, BorderLayout.CENTER);
		
		JMenuBar menuBar = new JMenuBar();
		pnlHead.add(menuBar, BorderLayout.EAST);
		
		JMenu logMenu = new JMenu("Team Log");
		menuBar.add(logMenu);
		
		JCheckBoxMenuItem cbItem = new
	               JCheckBoxMenuItem("Show Log");
		cbItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		cbItem.setAccelerator(KeyStroke.getKeyStroke('L',
	                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		logMenu.add(cbItem);
		cbItem.setSelected(true);
		cbItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(pnlLogCont != null) {
					pnlLogCont.setVisible(!pnlLogCont.isVisible());
				}
			}
		});
		
		JMenu dispMenu = new JMenu("Display Options");
		menuBar.add(dispMenu);
		
		cbItem = new JCheckBoxMenuItem("Show Board");
		cbItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		cbItem.setAccelerator(KeyStroke.getKeyStroke('B',
	                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		dispMenu.add(cbItem);
		cbItem.setSelected(true);
		cbItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(pnlTopLeft != null) {
					pnlTopLeft.setVisible(!pnlTopLeft.isVisible());
				}
			}
		});
		
		cbItem = new
	               JCheckBoxMenuItem("Show Team Config");
		cbItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		cbItem.setAccelerator(KeyStroke.getKeyStroke('G',
	                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		dispMenu.add(cbItem);
		cbItem.setSelected(true);
		cbItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(pnlTopRight != null) {
					pnlTopRight.setVisible(!pnlTopRight.isVisible());
				}
			}
		});
		
		cbItem = new
	               JCheckBoxMenuItem("Show Team Score");
		cbItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		cbItem.setAccelerator(KeyStroke.getKeyStroke('S',
	                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		dispMenu.add(cbItem);
		cbItem.setSelected(true);
		cbItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(textAreaScore != null) {
					textAreaScore.setVisible(!textAreaScore.isVisible());
				}
			}
		});
		
		cbItem = new
	               JCheckBoxMenuItem("Show Agent Stats");
		cbItem.setHorizontalTextPosition(JMenuItem.RIGHT);
		cbItem.setAccelerator(KeyStroke.getKeyStroke('T',
	                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		dispMenu.add(cbItem);
		cbItem.setSelected(true);
		cbItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(pnlBottom != null) {
					pnlBottom.setVisible(!pnlBottom.isVisible());
				}
			}
		});
		
		menuItemWindow = new JMenuItem("New Window");
		menuItemWindow.setHorizontalTextPosition(JMenuItem.RIGHT);
		menuItemWindow.setAccelerator(KeyStroke.getKeyStroke('W',
	                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menuItemWindow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				moveToWindow();
			}
		});
		dispMenu.add(menuItemWindow);
		
		JPanel pnlTop = new JPanel();
		pnlMain.add(pnlTop);
		pnlTop.setBackground(color);
		pnlTop.setLayout(new GridBagLayout());
		
		pnlTopLeft = new JPanel();
		pnlTopLeft.setLayout(new BoxLayout(pnlTopLeft, BoxLayout.Y_AXIS));
		pnlTopLeft.setPreferredSize(new Dimension(Math.max(200, 32 * boardSize), 32 * boardSize + 50));
		pnlTop.add(pnlTopLeft, new GridBagConstraints(0, 0, 1, 1, 0.1, 1.0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
		pnlTopLeft.setOpaque(false);
		
		for (int i = 0; i < iBoardSize; i++) {
			JPanel pnlRow = new JPanel(new FlowLayout(FlowLayout.LEFT,0, 0));
			pnlRow.setMaximumSize(new Dimension(30 * boardSize, 30));
			pnlTopLeft.add(pnlRow);
	        for (int j = 0; j < iBoardSize; j++) {
	            squares[i][j] = new JLayeredPane() {
	            	@Override
	            	public void paint(Graphics g) {
	            		super.paint(g);
	        			drawLine(this, g);
	            	};
	            };
	            squares[i][j].setBounds(i, j, 30, 30);
	            squares[i][j].setPreferredSize(new Dimension(30, 30));
	            squares[i][j].setOpaque(true);
	            squares[i][j].setBackground(Color.WHITE);
	            pnlRow.add(squares[i][j]);
	            StyleSet.setBorder(squares[i][j], 1);
	        }
		}
		JPanel pnlRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		pnlRow.setOpaque(false);
		pnlTopLeft.add(pnlRow);
		
		JLabel lbl = new JLabel("Current Pos");
		lbl.setIcon(iconWhite);
		pnlRow.add(lbl);
		
		lbl = new JLabel("Goal Pos");
		lbl.setIcon(iconGray);
		pnlRow.add(lbl);
		
		lbl = new JLabel("Reached Goal");
		lbl.setIcon(iconBlack);
		pnlRow.add(lbl);
		
		lbl = new JLabel("Cell Disturbed");
		lbl.setIcon(iconCorner);
		pnlRow.add(lbl);
		
		pnlTopRight = new JPanel();
		pnlTopRight.setLayout(new BoxLayout(pnlTopRight, BoxLayout.Y_AXIS));
		StyleSet.setBorder(pnlTopRight, 1);
		pnlTop.add(pnlTopRight, new GridBagConstraints(1, 0, 1, 1, 0.9, 1.0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		
		JTextArea textArea = new JTextArea();
		textArea.setPreferredSize(new Dimension(200, 160));
		textArea.setText(teamConfig.toString());
		textArea.setBackground(color);
		StyleSet.setRegular(textArea);
		textArea.setEditable(false);
        StyleSet.setEmptyBorder(textArea, 5);
		pnlTopRight.add(textArea);
		
		textAreaScore = new JTextArea();
		textAreaScore.setBackground(color);
		StyleSet.setTitleFont2(textAreaScore);
		textAreaScore.setEditable(false);
        StyleSet.setEmptyBorder(textAreaScore, 5);
		pnlTopRight.add(textAreaScore);
		
		pnlBottom = new JPanel();
		StyleSet.setEmptyBorder(pnlBottom, 5);
		pnlMain.add(pnlBottom);
		pnlBottom.setOpaque(false);
		pnlBottom.setLayout(new BorderLayout(0, 10));
		
		JLabel lblAgentStatsTitle = new JLabel("Agent Stats");
		StyleSet.setTitleFont2(lblAgentStatsTitle);
		pnlBottom.add(lblAgentStatsTitle, BorderLayout.NORTH);
		
		JPanel pnlTableCont = new JPanel();
		pnlBottom.add(pnlTableCont, BorderLayout.CENTER);
		table = new CustomTable(pnlTableCont);
		
		pnlLogCont = new JPanel();
		pnlLogCont.setLayout(new BorderLayout(0, 10));
		StyleSet.setEmptyBorder(pnlLogCont, 5);
		JLabel lblLogTitle = new JLabel("Team Log");
		StyleSet.setTitleFont2(lblLogTitle);
		pnlLogCont.add(lblLogTitle, BorderLayout.NORTH);
		pnlLogCont.setOpaque(false);
		pnlMain.add(pnlLogCont);
		
		JScrollPane scrollLog = new JScrollPane();
		scrollLog.setPreferredSize(new Dimension(getMaximumSize().width - 80, 200));
		pnlLogCont.add(scrollLog, BorderLayout.CENTER);
		JPanel pnlLog = new JPanel();
		scrollLog.setViewportView(pnlLog);
		pnlLog.setLayout(new BoxLayout(pnlLog, BoxLayout.Y_AXIS));
		pnlLog.setOpaque(false);
		textDetailLog = new JTextArea();
		pnlLog.add(textDetailLog);
		textDetailLog.setEditable(false);
        StyleSet.setRegular(textDetailLog);
        
		bNewRun = true;
	}
	
	
	public Dimension getMaximumSize() {
		return new Dimension(boardSize * 32 + 350, boardSize * 32 + 400);
	}
	
	public void newRunInitialized() {
		bNewRun = true;
		for (int i = 0; i < boardSize; i++) {
		    for (int j = 0; j < boardSize; j++) {
		    	if(squares[i][j] == null) continue;
		    	
		    	squares[i][j].setBackground(Color.WHITE);
		    	squares[i][j].removeAll();
		    	squares[i][j].revalidate();
		    	squares[i][j].repaint();
		    }
		}
		textAreaScore.setText("");
		table.clearData();
	}
	
	public void updateAgentStats(List<AgentStats> lstAgentStats, int threadDelay) {
		List<int[]> lstInitPos = new ArrayList<int[]>();
		List<int[]> lstGoalPos = new ArrayList<int[]>();
		List<Integer> lstInitResPoints = new ArrayList<Integer>();
		List<Integer> lstRemResPoints = new ArrayList<Integer>();
		List<String> lstLastAction = new ArrayList<String>();
		List<int[]> lstCurrPos = new ArrayList<int[]>();
		List<List<int[]>> lstPath = new ArrayList<List<int[]>>();
		for(AgentStats stat : lstAgentStats) {
			if(stat == null) continue;
			
			lstInitPos.add(new int[] { stat.getInitX(), stat.getInitY() });
			lstGoalPos.add(new int[] { stat.getGoalX(), stat.getGoalY() });
			lstCurrPos.add(new int[] { stat.getCurrX(), stat.getCurrY() });
			lstInitResPoints.add(stat.getInitResources());
			lstRemResPoints.add(stat.getRemainResources());
			lstLastAction.add(stat.getLastAction());
			lstPath.add(stat.getPath());
		}
		
		Timer timer = new Timer();
		timer.schedule(new UpdateAgentTask(lstInitPos, lstGoalPos, lstCurrPos, lstPath), bNewRun? 0 : (threadDelay * 1000 / 2));
		bNewRun = false;
		table.updateData(lstCurrPos, lstInitPos, lstGoalPos, lstInitResPoints, lstRemResPoints, lstLastAction);
	}
	
	public void updateBoard(int[][] boardColors) {
		List<Color> colors = new ArrayList<Color>();
		colors.add(Color.red); colors.add(Color.green); colors.add(Color.blue);colors.add(Color.yellow);colors.add(Color.orange);colors.add(Color.magenta);
		colors.add(Color.CYAN);colors.add(Color.lightGray);colors.add(Color.PINK);colors.add(Color.black);
		
		for (int i = 0; i < boardSize; i++) {
	        for (int j = 0; j < boardSize; j++) {
	        	if(squares[i][j] == null) continue;
	        	
	        	for(int iLoop = 0; iLoop < squares[i][j].getComponentCount(); iLoop++) {
		    		if(squares[i][j].getLayer(squares[i][j].getComponent(iLoop)) == -1) {
		    			squares[i][j].remove(iLoop);
		    			iLoop--;
		    		}
		    	}
	        	
        		while(colors.size() <= boardColors[i][j]) {
        			colors.add(Color.getHSBColor((float)Math.random(), (float)Math.random(), (float)Math.random()));
        		}
	        	Color newColor = colors.get(boardColors[i][j]);
	        	if(squares[i][j].getBackground().equals(Color.WHITE)) {
	        		squares[i][j].setBackground(newColor);
	        	}
	        	else if(!newColor.equals(squares[i][j].getBackground())) {
	        		JLabel lbl = new JLabel();
	        	    lbl.setIcon(iconCorner);
	            	StyleSet.setRegular(lbl);
	            	lbl.setBounds(0, 0, iconCorner.getIconWidth(), iconCorner.getIconHeight());
	            	lbl.setVerticalAlignment(SwingConstants.TOP);
	            	squares[i][j].add(lbl);
	            	squares[i][j].setLayer(lbl, -1);
	        		squares[i][j].setBackground(newColor);
	        	}
	        	squares[i][j].revalidate();
	        	squares[i][j].repaint();
	        }
		}
	}
	
	public void updateTeamScore(int teamScore) {
		if(textAreaScore != null) {
			textAreaScore.setText("Team Score : " + teamScore);
		}
	}
	
	public void updateLog(String strDetailLog) {
		if(textDetailLog != null) {
			textDetailLog.setText(textDetailLog.getText() + "\n" + strDetailLog);
		}
	}
	
	private boolean isLeft(List<int[]> path, int index, int refIndex) {
		if(path.size() <= Math.max(index, refIndex)) return false;
		
		if(path.get(refIndex)[0] ==  path.get(index)[0]
				&& path.get(refIndex)[1] + 1 ==  path.get(index)[1])
			return true;
		return false;
	}
	
	private boolean isRight(List<int[]> path, int index, int refIndex) {
		if(path.size() <= Math.max(index, refIndex)) return false;
		
		if(path.get(refIndex)[0] ==  path.get(index)[0]
				&& path.get(refIndex)[1] - 1 ==  path.get(index)[1])
			return true;
		return false;
	}
	
	private boolean isTop(List<int[]> path, int index, int refIndex) {
		if(path.size() <= Math.max(index, refIndex)) return false;
		
		if(path.get(refIndex)[0] + 1 ==  path.get(index)[0]
				&& path.get(refIndex)[1] ==  path.get(index)[1])
			return true;
		return false;
	}
	
	private boolean isBottom(List<int[]> path, int index, int refIndex) {
		if(path.size() <= Math.max(index, refIndex)) return false;
		
		if(path.get(refIndex)[0] - 1 ==  path.get(index)[0]
				&& path.get(refIndex)[1] ==  path.get(index)[1])
			return true;
		return false;
	}
	
	private void drawLine(JLayeredPane pane, Graphics g)
	{
		if(pane.getName() == null || pane.getName().length() == 0) return;
		
		int halfMark = 15;
		if(pane.getComponentCount() > 0)
		{
			if(pane.getLayer(pane.getComponent(0)) == -1) {
				if(pane.getComponentCount() > 1)
					halfMark = 5;
			}
			else
			{
				halfMark = 5;
			}
		}
		String[] parts = pane.getName().split(",");
		for(int iLoop = 0; iLoop < parts.length; iLoop++)
		{
			int type = Integer.parseInt(parts[iLoop]);
			if(type == 0)//left
			{
				g.drawLine(0, 15, halfMark, 15);
			}
			if(type == 1)//top
			{
				g.drawLine(15, 0, 15, halfMark);
			}
			if(type == 2)//right
			{
				g.drawLine(30 - halfMark, 15, 30, 15);
			}
			if(type == 3)//bottom
			{
				g.drawLine(15, 30 - halfMark, 15, 30);
			}
		}
	}
	
	public void moveToWindow()
	{
		if(winState == WindowState.Internal) {
			firePropertyChange("WindowState", WindowState.Internal, WindowState.Independent);
			winState = WindowState.Independent;
			menuItemWindow.setText("Return to Run Window");
		}
		else { 
			firePropertyChange("WindowState", WindowState.Independent, WindowState.Internal);
			winState = WindowState.Internal;
			menuItemWindow.setText("New Window");
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
	
	public String getTeamName() {
		return lblTeamName.getText();
	}

	public int getParentExtendState() {
		return parentExtendState;
	}

	public void setParentExtendState(int parentExtendState) {
		this.parentExtendState = parentExtendState;
	}
	
	public void registerKeyDispatcher(KeyEventDispatcher dispatcher)
	{
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(dispatcher);
	}

	public enum WindowState
	{
		Internal,
		Independent
	}
	
	class CustomTable extends JTable
	{
		private JTableHeader header;
		private String[] columnNames = {"Ag#","Ini > Curr > Goal","Steps Moved","Steps Remain","Init Resources","Res. Remain","Last Action"};
		DefaultTableCellRenderer tableRender;
		
		public CustomTable(JPanel pnlContainer)
		{
			pnlContainer.setLayout(new BorderLayout());

			header = getTableHeader();
			
			DefaultTableModel tableModel = new DefaultTableModel() {
				@Override
	            public boolean isCellEditable(int row, int col) {
	                return false;
	            }
			};
			tableModel.setColumnIdentifiers(columnNames);
			setModel(tableModel);
			
			tableRender = new DefaultTableCellRenderer() {
	            @Override
	            public Component getTableCellRendererComponent(JTable table, Object
	                value, boolean isSelected, boolean hasFocus, int row, int column) {
	                super.getTableCellRendererComponent(
	                    table, value, isSelected, hasFocus, row, column);
	                setHorizontalAlignment(JLabel.CENTER);
	                
	                return this;
	            }
	        };
	        
			setRowHeight(25);
			header.setPreferredSize(new Dimension(getWidth(), 25));
			
			pnlContainer.add(header, BorderLayout.NORTH);
			pnlContainer.add(this, BorderLayout.CENTER);
			
			setShowGrid(true);
			setGridColor(Color.gray);
			setBorder(new EtchedBorder(EtchedBorder.RAISED));
	        setColumnSelectionAllowed(true);
	        setCellEditor(null);
	        
	        StyleSet.setRegular(this);
	        StyleSet.setRegular(header);
		}
		
		public void updateData(List<int[]> currentPos, List<int[]> initPos, List<int[]> goalPos, 
				List<Integer> initResPoints, List<Integer> remResPoints, List<String> lstLastAction)
		{
			Object[][] data = new Object[teamSize][7];
			
			for(int iIndex = 0; iIndex < teamSize; iIndex++)
			{
				data[iIndex][0] = iIndex + 1;
				data[iIndex][1] = "(" + (initPos.get(iIndex)[0] + 1) + "," + (initPos.get(iIndex)[1] + 1) + ")" + " > " 
									+ "(" + (currentPos.get(iIndex)[0] + 1) + "," + (currentPos.get(iIndex)[1] + 1) + ")" 
									+ " > (" + (goalPos.get(iIndex)[0] + 1) + "," + (goalPos.get(iIndex)[1] + 1) + ")";
				data[iIndex][2] = Math.abs(currentPos.get(iIndex)[0] - initPos.get(iIndex)[0]) + Math.abs(currentPos.get(iIndex)[1] - initPos.get(iIndex)[1]);
				data[iIndex][3] = Math.abs(goalPos.get(iIndex)[0] - currentPos.get(iIndex)[0]) + Math.abs(goalPos.get(iIndex)[1] - currentPos.get(iIndex)[1]);
				data[iIndex][4] = initResPoints.get(iIndex);
				data[iIndex][5] = remResPoints.get(iIndex);
				data[iIndex][6] = lstLastAction.get(iIndex); //strValues[randm.nextInt(3)];
			}
			
			((DefaultTableModel)getModel()).setDataVector(data, columnNames);
			for(int iCol = 0; iCol < columnNames.length; iCol++)
			{
				TableColumn col = getColumnModel().getColumn(iCol);
				col.setCellRenderer(tableRender);
				col.setPreferredWidth(getColumnWidth(iCol));
			}
			this.revalidate();
		}
		
		public void clearData()
		{
			((DefaultTableModel)getModel()).setDataVector(null, columnNames);
			this.revalidate();
		}
		
		private int getColumnWidth(int colIndex)
		{
			switch(colIndex)
			{
				case 0:
					return 15;
				case 1:
					return 90;
				case 4:
					return 60;
			}
			return 50;
		}

	}
	
	private class UpdateAgentTask extends TimerTask
	{
		List<int[]> lstInitPos, lstGoalPos, lstCurrPos;
		List<List<int[]>> lstPath;
		public UpdateAgentTask(List<int[]> lstInitPos, List<int[]> lstGoalPos,List<int[]>  lstCurrPos, List<List<int[]>> lstPath)
		{
			super();
			this.lstInitPos = lstInitPos;
			this.lstGoalPos = lstGoalPos;
			this.lstCurrPos = lstCurrPos;
			this.lstPath = lstPath;
		}
		@Override
		public void run() {
			try {
				for (int i = 0; i < boardSize; i++) {
				    for (int j = 0; j < boardSize; j++) {
				    	if(squares[i][j] == null) continue;
				    	
				    	for(int iLoop = 0; iLoop < squares[i][j].getComponentCount(); iLoop++) {
				    		if(squares[i][j].getLayer(squares[i][j].getComponent(iLoop)) > -1) {
				    			squares[i][j].remove(iLoop);
				    			iLoop--;
				    		}
				    	}
				    	squares[i][j].setName("");
				    	squares[i][j].revalidate();
				    	squares[i][j].repaint();
				    }
				}
				
				
				//Add agents
				for(int iIndex = 0; iIndex < teamSize; iIndex++) {
					
					//not reached goal then draw path
					if(lstCurrPos.get(iIndex)[0] != lstGoalPos.get(iIndex)[0] 
									|| lstCurrPos.get(iIndex)[1] != lstGoalPos.get(iIndex)[1])
					{
						List<int[]> path = lstPath.get(iIndex);
						boolean bPathStart = false;
						for(int index = 0; index < path.size(); index++)
						{
							if(!bPathStart)
							{
								if(path.get(index)[0] == lstCurrPos.get(iIndex)[0] 
										&& path.get(index)[1] == lstCurrPos.get(iIndex)[1])
									bPathStart = true;
							}
							
							if(bPathStart)
							{
								String strTypes = "";
								if(isLeft(path, index, index + 1))
					    			strTypes += "0,";
					    		else if(isTop(path, index, index + 1))
					    			strTypes += "1,";
					    		else if(isRight(path, index, index + 1))
					    			strTypes += "2,";
					    		else if(isBottom(path, index, index + 1))
					    			strTypes += "3,";
					    		
								//draw incoming line if not current position
								if(path.get(index)[0] != lstCurrPos.get(iIndex)[0] 
										|| path.get(index)[1] != lstCurrPos.get(iIndex)[1])
								{
						    		if(index > 0 && isLeft(path, index, index - 1))
						    			strTypes += "0,";
						    		else if(index > 0 && isTop(path, index, index - 1))
						    			strTypes += "1,";
						    		else if(index > 0 && isRight(path, index, index - 1))
						    			strTypes += "2,";
						    		else if(index > 0 && isBottom(path, index, index - 1))
						    			strTypes += "3,";
								}
						    		
						    	strTypes = Utilities.trim(strTypes, ',');
						    	String strName = squares[path.get(index)[0]][path.get(index)[1]].getName();
						    	if(strName == null || strName.length() == 0)
						    		strName = strTypes;
						    	else
						    		strName += "," + strTypes;
						    	squares[path.get(index)[0]][path.get(index)[1]].setName(strName);
						    	squares[path.get(index)[0]][path.get(index)[1]].repaint();
							}
						}
					}
					
					//Add goal
					JLabel lbl = new JLabel("  " + String.valueOf(iIndex + 1));
		        	lbl.setHorizontalTextPosition(JLabel.CENTER);
		    	    lbl.setVerticalTextPosition(JLabel.CENTER);
		    	    lbl.setIcon(iconGray);
		        	StyleSet.setRegular(lbl);
		        	lbl.setForeground(Color.BLACK);
		        	lbl.setBounds(0, 0, iconGray.getIconWidth(), iconGray.getIconHeight());
					squares[lstGoalPos.get(iIndex)[0]][lstGoalPos.get(iIndex)[1]].add(lbl, 1);
					//squares[lstGoalPos.get(iIndex)[0]][lstGoalPos.get(iIndex)[1]].setLayer(lbl, 0, 0);
					
					//Add current
					if(lstGoalPos.get(iIndex)[0] == lstCurrPos.get(iIndex)[0] 
							&& lstGoalPos.get(iIndex)[1] == lstCurrPos.get(iIndex)[1]) {
						lbl.setIcon(iconBlack); //reached goal
						lbl.setForeground(Color.WHITE);
						lbl.setText(lbl.getText().trim());
					} 
					else {
						lbl = new JLabel(String.valueOf(iIndex + 1));
		            	lbl.setHorizontalTextPosition(JLabel.CENTER);
		        	    lbl.setVerticalTextPosition(JLabel.CENTER);
		        	    lbl.setIcon(iconWhite);
		        	    lbl.setBounds(0, 0, iconWhite.getIconWidth(), iconWhite.getIconHeight());
		            	StyleSet.setRegular(lbl);
		            	squares[lstCurrPos.get(iIndex)[0]][lstCurrPos.get(iIndex)[1]].add(lbl, 0);
		            	//squares[lstGoalPos.get(iIndex)[0]][lstGoalPos.get(iIndex)[1]].setLayer(lbl, 1);
		            	//squares[lstCurrPos.get(iIndex)[0]][lstCurrPos.get(iIndex)[1]].setLayer(lbl, squares[lstCurrPos.get(iIndex)[0]][lstCurrPos.get(iIndex)[1]].getComponentCount());
					}
				}
				
				for (int i = 0; i < boardSize; i++) {
				    for (int j = 0; j < boardSize; j++) {
				    	if(squares[i][j] == null) continue;
				    	
				    	if(squares[i][j].getComponentCount() > 1) {
				    		for(int cIndex = 0; cIndex < squares[i][j].getComponentCount(); cIndex++)
				    		{
				    			Component comp = squares[i][j].getComponent(cIndex);
				    			if(squares[i][j].getLayer(comp) == -1) continue;
				    			
				    			comp.addMouseListener(new MouseListener() {
									
				    				@Override
									public void mouseClicked(MouseEvent event) {
				    					JLabel lbl = (JLabel)event.getSource();
				    					JLayeredPane pane = (JLayeredPane)lbl.getParent();
				    					pane.moveToBack(lbl);
									}
				    				
									@Override
									public void mouseReleased(MouseEvent event) { }
									
									@Override
									public void mousePressed(MouseEvent event) { }
									
									@Override
									public void mouseExited(MouseEvent event) { }
									
									@Override
									public void mouseEntered(MouseEvent event) { }
								});
				    			if(cIndex == 0) continue;
				    			
					    		Rectangle bound = comp.getBounds();
					    		bound.x = bound.x + 2;
					    		bound.y = bound.y + 2;
					    		comp.setBounds(bound);
				    		}
				    	}
				    }
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
}