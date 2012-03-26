package experiments;

import java.text.DecimalFormat;
import java.util.Scanner;
import java.io.*;

import massim.Agent;
import massim.SimulationEngine;
import massim.Team;
import massim.agents.advancedactionmap.AdvActionMAPAgent;
import massim.agents.advancedactionmap.AdvActionMapTeam;
import massim.agents.basicactionmap.BasicActionMAPAgent;
import massim.agents.basicactionmap.BasicActionMAPTeam;
import massim.agents.empathic.EmpathicAgent;
import massim.agents.empathic.EmpathicTeam;
import massim.agents.nohelp.NoHelpTeam;


/**
 * This is an experiment for testing replanning agents
 * 
 *   
 * @author Omid Alemi
 *
 */
public class ExpMatlab1 {

	public static void main(String[] args) throws IOException {
	
	// Initializing the variables from input.txt
	FileInputStream frstream = new FileInputStream("C:\\input.txt");
	DataInputStream in = new DataInputStream(frstream);
  	BufferedReader br = new BufferedReader(new InputStreamReader(in));
  	String strLine = br.readLine();
  	String[] params = strLine.split(" ");
  	EmpathicAgent.WTH_Threshhold = Double.parseDouble(params[0]);
  	EmpathicAgent.emotState_W = Double.parseDouble(params[1]);
  	EmpathicAgent.salience_W = Double.parseDouble(params[2]);
  	EmpathicAgent.pastExp_W = Double.parseDouble(params[3]);
		
	int numberOfRuns = 100;
		
	SimulationEngine.colorRange = 
		new int[] {0, 1, 2, 3, 4, 5};
	SimulationEngine.numOfColors =  
		SimulationEngine.colorRange.length;
	SimulationEngine.actionCostsRange = 
		new int[] {10, 40, 70, 100, 300, 400, 450,  500};	
	SimulationEngine.numOfMatches = 4;
	
	/* Create the teams involved in the simulation */
		Team.teamSize = 8;
		Team[] teams = new Team[1];		
		teams[0] = new EmpathicTeam();
	
		
			
		
		/* Create the SimulationEngine */
		SimulationEngine se = new SimulationEngine(teams);
		
		
		/* The experiments loop */
		
			/* Set the experiment-wide parameters: */
			/* teams-wide, SimulationEngine, etc params */			
			
			Team.initResCoef = 200;
			Team.unicastCost = 3;
			Team.broadcastCost = Team.unicastCost * (Team.teamSize-1);
			Agent.calculationCost = 3;
			Agent.helpOverhead = 30;
			Agent.cellReward = 100;
			Agent.achievementReward = 2000;

			/* vary the disturbance: */
			SimulationEngine.disturbanceLevel = 0.1;

			/* Initialize and run the experiment */
			se.initializeExperiment(numberOfRuns);
			int[] teamScores = se.runExperiment();

			/* Print the results */
			DecimalFormat df = new DecimalFormat("0.0");
			
			FileWriter fstream = new FileWriter("C:\\result.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			for (int i=0;i<teams.length;i++){
				// int i = 1;
				double r = teamScores[i]/100;
				r = Math.ceil(r+0.5);
				int roundedTeamScore = (int)r;
				System.out.printf("%d",	-roundedTeamScore);
				out.write(Integer.toString(-teamScores[i]));
			}
			System.out.println("");
			out.close();
			


		
	}


}
