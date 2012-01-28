package experiments.ex1;

import java.text.DecimalFormat;
import java.util.Scanner;

import massim.Agent;
import massim.SimulationEngine;
import massim.Team;
import massim.agents.advancedactionmap.AdvActionMAP2Agent;
import massim.agents.advancedactionmap.AdvActionMAP2Team;
import massim.agents.advancedactionmap.AdvActionMAPAgent;
import massim.agents.advancedactionmap.AdvActionMapTeam;
import massim.agents.basicactionmap.BasicActionMAPAgent;
import massim.agents.basicactionmap.BasicActionMAPTeam;
import massim.agents.nohelp.NoHelpTeam;

/**
 * This is an experiment in the regular branch v1.3
 *   
 * @author Omid Alemi
 *
 */
public class Experiment5 {

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
		teams[0] = new BasicActionMAPTeam();
		teams[1] = new AdvActionMapTeam();
		teams[2] = new AdvActionMAP2Team();	
		teams[3] = new NoHelpTeam();
			
		
		
		/* Create the SimulationEngine */
		SimulationEngine se = new SimulationEngine(teams);
				
		System.out.println("DISTURBANCE,BasicActionMAP,AdvActionMAP,AdvActionMAP2,NO-HELP");
		
		/* The experiments loop */
		for (int exp=0;exp<11;exp++)
		{
			/* Set the experiment-wide parameters: */
			/* teams-wide, SimulationEngine, etc params */			
			
			Team.initResCoef = 200;
			Team.unicastCost = 3;
			Team.broadcastCost = Team.unicastCost * (Team.teamSize-1);
			Agent.calculationCost = 3;
			Agent.helpOverhead = 30;			
			Agent.cellReward = 100;
			Agent.achievementReward = 2000;
			AdvActionMAPAgent.requestThreshold = 399;
			AdvActionMAPAgent.WLL = 0.8;
			AdvActionMAPAgent.lowCostThreshold = 100;
			BasicActionMAPAgent.requestThreshold = 299;
			
			AdvActionMAP2Agent.EPSILON = 0.2;
			AdvActionMAP2Agent.requestThreshold = 399;
			AdvActionMAP2Agent.WLL = 0.8;
			AdvActionMAP2Agent.lowCostThreshold = 100;
			
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
//			int i = 1;
				System.out.printf(",%d", 						
						teamScores[i]);
			System.out.println("");
//			(new Scanner(System.in)).nextLine();

		}
		System.out.println(AdvActionMAP2Agent.cond1count);
		System.out.println(AdvActionMAP2Agent.cond2count);
		System.out.println(AdvActionMAP2Agent.cond3count);
		System.out.println(AdvActionMAP2Agent.twbbcast);
		/* Print the header(footer!) */
	//	System.out.println("param, dist,( #req, #bids, #suc offers,#unsuc help req, score ) x team");
	}

}
