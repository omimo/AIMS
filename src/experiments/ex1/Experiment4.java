package experiments.ex1;

import java.text.DecimalFormat;
import java.util.Scanner;

import massim.Agent;
import massim.SimulationEngine;
import massim.Team;
import massim.TeamTask;
import massim.agents.advancedactionmap.AdvActionMAPAgent;
import massim.agents.advancedactionmap.AdvActionMAPRepAgent;
import massim.agents.advancedactionmap.AdvActionMAPRepTeam;
import massim.agents.advancedactionmap.AdvActionMapTeam;
import massim.agents.basicactionmap.BasicActionMAPAgent;
import massim.agents.basicactionmap.BasicActionMAPTeam;
import massim.agents.nohelp.NoHelpRepAgent;
import massim.agents.nohelp.NoHelpRepTeam;
import massim.agents.nohelp.NoHelpTeam;

/**
 * This is an experiment for testing replanning agents
 * 
 *   
 * @author Omid Alemi
 *
 */
public class Experiment4 {

	public static void main(String[] args) {
	int numberOfRuns = 500;
		
	SimulationEngine.colorRange = 
		new int[] {0, 1, 2, 3, 4, 5};
	SimulationEngine.numOfColors =  
		SimulationEngine.colorRange.length;
	SimulationEngine.actionCostsRange = 
		new int[] {10, 40, 70, 100, 300, 400, 450,  500};	
	
	/* Create the teams involved in the simulation */
		Team.teamSize = 8;
		Team[] teams = new Team[4];		
		teams[0] = new AdvActionMapTeam();
		teams[1] = new AdvActionMAPRepTeam();
		teams[2] = new NoHelpTeam();
		teams[3] = new NoHelpRepTeam();
			
		
		/* Create the SimulationEngine */
		SimulationEngine se = new SimulationEngine(teams);
		
		System.out.println("DISTURBANCE,AD-ACTION-MAP,REP-AD-ACTION-MAP,NO-HELP,REP-NO-HELP");
		
		/* The experiments loop */
		for (int exp=0;exp<11;exp++)
		{
			/* Set the experiment-wide parameters: */
			/* teams-wide, SimulationEngine, etc params */			
			
			TeamTask.initResCoef = 200;
			Team.unicastCost = 7;
			Team.broadcastCost = Team.unicastCost * (Team.teamSize-1);
			Agent.calculationCost = 7;
			TeamTask.helpOverhead = 30;			
			TeamTask.cellReward = 100;
			TeamTask.achievementReward = 2000;
			AdvActionMAPAgent.requestThreshold = 299;
			AdvActionMAPAgent.WLL = 0.8;
			AdvActionMAPAgent.lowCostThreshold = 100;
//			BasicActionMAPAgent.requestThreshold = 299;
			
			AdvActionMAPRepAgent.WREP = 1.0;
			NoHelpRepAgent.WREP = 1.0;
			
			/* vary the disturbance: */
			SimulationEngine.disturbanceLevel = 0.1 * exp;;  
			
			/* Initialize and run the experiment */
			se.initializeExperiment(numberOfRuns);			
			int[] teamScores = se.runExperiment();
			
			/* Print the results */
			DecimalFormat df = new DecimalFormat("0.0");
			System.out.print(exp+","+ 
						df.format(SimulationEngine.disturbanceLevel));
			for (int i=0;i<teams.length;i++)
				System.out.print(","+ 
						teamScores[i]);
			System.out.println("");
//			(new Scanner(System.in)).nextLine();

		}
	}

}
