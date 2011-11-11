package experiments.ex1;

import java.text.DecimalFormat;

import massim.Agent;
import massim.SimulationEngine;
import massim.Team;
import massim.agents.advancedactionmap.AdvActionMAPAgent;
import massim.agents.advancedactionmap.AdvActionMapTeam;
import massim.agents.nohelp.NoHelpTeam;

public class Experiment2 {

	public static void main(String[] args) {
	int numberOfRuns = 100;
		
		/* Create the teams involved in the simulation */
		Team.teamSize = 8;
		Team[] teams = new Team[2];
		teams[0] = new AdvActionMapTeam();
		teams[1] = new NoHelpTeam();
			
				
		/* Create the SimulationEngine */
		SimulationEngine se = new SimulationEngine(teams);
		
		/* The experiments loop */
		for (int exp=0;exp<11;exp++)
		{
			/* Set the experiment-wide parameters: */
			/* teams-wide, SimulationEngine, etc params */			
			
			Team.initResCoef = 200;
			Team.unicastCost = 3;
			Team.broadcastCost = Team.unicastCost * (Team.teamSize-1);
			Agent.calculationCost = 3;
			Agent.helpOverhead = 50;			
			Agent.cellReward = 50;
			Agent.achievementReward = 10000;
			AdvActionMAPAgent.requestThreshold = 299;
			AdvActionMAPAgent.WLL = 0.8;
			AdvActionMAPAgent.lowCostThreshold = 40;
			
			/* vary the disturbance: */
			SimulationEngine.disturbanceLevel = 0.1 * exp;  
			
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
			//(new Scanner(System.in)).nextLine();

		}
	}

}
