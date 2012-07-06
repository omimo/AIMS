package frontends.simplegui;

import javax.swing.*;
import java.awt.*;
import javax.swing.SwingUtilities;

import massim.Agent;
import massim.SEControl;
import massim.SimulationEngine;
import massim.Team;

import massim.agents.nohelp.NoHelpTeam;

public class SimpleSim extends JFrame {

	public SEControl sec;
	ControlBox controlBox; 
	NumericalResultBox resultsBox;
	ParametersBox paramBox;
	
	public SimpleSim() {
		   setTitle("Simple Simulator");
	       setSize(500, 600);
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
		

		
		ss.sec = new SimulationEngine(teams);
		
		Team.initResCoef = 200;
		Team.unicastCost = 7;
		Team.broadcastCost = Team.unicastCost * (Team.teamSize-1);
		Agent.calculationCost = 35;

		Agent.cellReward = 100;
		Agent.achievementReward = 2000;
		
		
		
		ss.sec.addParam("env.disturbance", (Double)0.0);	
		ss.sec.addParam("agent.helpoverhead", 5);
		/*****************************************/
		
		
		ss.controlBox = new ControlBox(ss);
		ss.paramBox = new ParametersBox(ss);
		ss.resultsBox = new NumericalResultBox();
		
		ss.add(ss.controlBox);
		ss.add(ss.paramBox);
		ss.add(ss.resultsBox);
		
		ss.setVisible(true);
	}
	
	public static void setNativeLookAndFeel() {
	    try {
	      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch(Exception e) {
	      System.out.println("Error setting native LAF: " + e);
	    }
	  }

}
