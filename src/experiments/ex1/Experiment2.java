package experiments.ex1;

import java.util.Scanner;

import massim.Agent;
import massim.SimulationEngine;
import massim.Team;
import massim.agent.advancedactionmap.AdvActionMapTeam;
import massim.agents.dummy.DummyTeam;

public class Experiment2 {

	public static void main(String[] args) {
	int numberOfRuns = 4;
		
		/* Create the teams involved in the simulation */
		Team.teamSize = 8;
		Team[] teams = new Team[2];
		teams[0] = new AdvActionMapTeam();
		teams[1] = new DummyTeam();
			
				
		/* Create the SimulationEngine */
		SimulationEngine se = new SimulationEngine(teams);
		
		/* The experiments loop */
		for (int exp=0;exp<11;exp++)
		{
			/* Set the experiment-wide parameters: */
			/* teams-wide, SimulationEngine, etc params */			
			
			Team.initResCoef = 80;			
			Agent.cellReward = 50;
			
			/* vary the disturbance: */
			SimulationEngine.disturbanceLevel = 0.1 * exp;  
			
			/* Initialize and run the experiment */
			se.initializeExperiment(numberOfRuns);			
			int[] teamScores = se.runExperiment();
			
			/* Print the results */
			for (int i=0;i<teams.length;i++)
				System.out.println("Exp"+exp+": disturbance level="+ 
						SimulationEngine.disturbanceLevel+"; "+ 
						teams[i].getClass().getSimpleName()+
						" average score = "+teamScores[i]);
			(new Scanner(System.in)).nextLine();

		}
	}

}
