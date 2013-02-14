package frontends.simplegui;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import massim.Agent;
import massim.SEControl;
import massim.SimulationEngine;
import massim.Team;
import massim.agents.nohelp.NoHelpTeam;

public class SimpleSim extends JFrame {
	private static Logger logger = Logger.getLogger("all");
	
	public SEControl sec;
	ControlBox controlBox; 
	ConsoleBox consoleBox;
	ParametersBox paramBox;
	VisualBox visualBox;

	JTextArea logJTextArea;
	
	public SimpleSim() {
		   setupJdkLoggerHandler(false);
		   setTitle("Simple Simulator");
	       setSize(1000, 700);
	       setLocationRelativeTo(null);
	       setNativeLookAndFeel();
	       setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
		 
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                runGUI();
            }
        });		
	}
	
	public static void runGUI() {
		SimpleSim ss = new SimpleSim();
		
		ss.setLayout(new BoxLayout(ss.getContentPane(), BoxLayout.Y_AXIS));

		/*   */
		SimulationEngine.colorRange = 
				new int[] {0, 1, 2, 3, 4, 5};
		SimulationEngine.numOfColors =  
				SimulationEngine.colorRange.length;
		SimulationEngine.actionCostsRange = 
				new int[] {10, 40, 70, 100, 300, 400, 450,  500};	
		SimulationEngine.numOfMatches = 5;
		NoHelpTeam.useExp = false;

		Team.teamSize = 8;
		Team[] teams = new Team[1];		
		teams[0] = new NoHelpTeam();
		

		
		ss.sec = new SimulationEngine();
		ss.sec.loadTeams(teams);
		
		Team.initResCoef = 200;
		Team.unicastCost = 7;
		Team.broadcastCost = Team.unicastCost * (Team.teamSize-1);
		Agent.calculationCost = 35;

		Agent.cellReward = 100;
		Agent.achievementReward = 2000;
		
		
		
		ss.sec.addParam("env.disturbance", (Double)0.0);	
		ss.sec.addParam("agent.helpoverhead", 5);
		/*****************************************/
		
		ss.setLayout(new BorderLayout());
		
		ss.controlBox = new ControlBox(ss);
		ss.paramBox = new ParametersBox(ss);
		ss.consoleBox = new ConsoleBox();
		ss.logJTextArea = ss.consoleBox.getLogArea();
		
		ss.visualBox = new VisualBox(ss,massim.microworlds.twctgrid.gui.Board.class);
		
		JSplitPane spl1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,ss.paramBox,ss.controlBox);
		ss.paramBox.setMinimumSize(new Dimension(300,350));
		ss.add(spl1,BorderLayout.WEST);
		ss.add(ss.consoleBox,BorderLayout.SOUTH);
		ss.add(ss.visualBox,BorderLayout.CENTER);
		ss.setVisible(true);
		
	    //
		try {
			FileHandler fh = new FileHandler("/home/omid/test3.log", false);
			fh.setFormatter(new LogFormatter());
			logger.addHandler(fh);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	   
	
	    
	}
	
	protected  void setupJdkLoggerHandler( boolean logInGUI) {
		// This code attaches the handler to the text area
		
		StringBuffer buf = new StringBuffer();
		if (logInGUI) {
			TextAreaHandler.setTextArea(logJTextArea);
			
		// Normally configuration would be done via a properties file
		// that would be read in with LogManager.getLogManager().readConfiguration()
		// But I create an inputstream here to keep it local.
		// See JAVA_HOME/jre/lib/logging.properties for more description of these settings.
		//
		
		//buf.append("handlers = frontends.simplegui.TextAreaHandler, java.util.logging.ConsoleHandler"); // A default handler and our custom handler
		buf.append("handlers = frontends.simplegui.TextAreaHandler\n"); // our custom handler
		buf.append(".level = INFO"); // Set the default logging level see: C:\software\sun\jdk141_05\docs\api\index.html
		buf.append("\n");
		buf.append("frontends.simplegui.TextAreaHandler = ALL"); // Custom Handler logging level
		buf.append("\n");
		buf.append("java.util.logging.ConsoleHandler.level = ALL"); // Custom Handler logging level
		buf.append("\n");
//		buf.append("java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter"); //
//		buf.append("\n");
//		buf.append("frontends.simplegui.TextAreaHandler.formatter = frontends.simplegui.LogFormatter"); //
//		buf.append("\n");
//		buf.append("java.awt.KeyboardFocusManager.level = INFO");  // Set the logging level for this logger  
		}
		else
		{
			
			//buf.append("handlers = frontends.simplegui.TextAreaHandler, java.util.logging.ConsoleHandler"); // A default handler and our custom handler
			buf.append("handlers = \n"); // our custom handler
			buf.append(".level = NONE"); // Set the default logging level see: C:\software\sun\jdk141_05\docs\api\index.html
			buf.append("\n");
			buf.append("frontends.simplegui.TextAreaHandler = ALL"); // Custom Handler logging level
			buf.append("\n");
			buf.append("java.util.logging.ConsoleHandler.level = ALL"); // Custom Handler logging level
			buf.append("\n");
		}
		try {
			java.util.logging.LogManager.getLogManager().readConfiguration(
					new ByteArrayInputStream(buf.toString().getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setNativeLookAndFeel() {
	    try {
	      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch(Exception e) {
	      System.out.println("Error setting native LAF: " + e);
	    }
	  }

}
