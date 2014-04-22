package experiments.importance;

import java.io.*;

import massim.Agent;
import massim.SimulationEngine;
import massim.Team;
import massim.TeamTask;
import massim.agents.advancedactionmap.AdvActionMAPAgent;
import massim.agents.advancedactionmap.AdvActionMAPRepAgent;
import massim.agents.advancedactionmap.AdvActionMAPTeam;
import massim.agents.nohelp.NoHelpTeam;
import massim.agents.reassignment.RAAgent;


/**
 * This is an experiment for testing importance expression of agents
 * over Resource Multiplier, Disturbance, No of Runs & Expression versions
 * 
 *   
 * @author Denish M
 *
 */
public class ImportanceExp {

	public static void main(String[] args) {
			try {
				runSimulation(50, 0.1, 50, 0);
				runSimulation(50, 0.1, 50, 1);
				runSimulation(50, 0.1, 50, 2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public static int runSimulation(double importanceConstant, double disturbanceProbability, int numberOfIterPerRun, int useImportanceV2) throws IOException {
	
	//AdvActionMAPAgent.importanceConstant = importanceConstant;
  	SimulationEngine.disturbanceLevel = disturbanceProbability;
  	
	SimulationEngine.colorRange = 
		new int[] {0, 1, 2, 3, 4, 5};
	SimulationEngine.numOfColors =  
		SimulationEngine.colorRange.length;
	SimulationEngine.actionCostsRange = 
		new int[] {10, 40, 70, 100, 300, 400, 450,  500};	
	
		/* Create the teams involved in the simulation */
		Team.teamSize = 8;
		Team[] teams = new Team[2];		
		teams[0] = new AdvActionMAPTeam();
		teams[1] = new NoHelpTeam();
		
		/* Create the SimulationEngine */
		SimulationEngine se = new SimulationEngine(teams);
		
		/* The experiments loop */
		/* Set the experiment-wide parameters: */
		/* teams-wide, SimulationEngine, etc params */			
		
		Team.unicastCost = 1;
		Team.broadcastCost = Team.unicastCost * (Team.teamSize-1);
		Agent.calculationCost = 1;
	
		TeamTask.initResCoef = 200;
		TeamTask.helpOverhead = 30;			
		TeamTask.cellReward = 100;
		TeamTask.achievementReward = 2000;
		//TeamTask.initResCoef = 200;
		TeamTask.assignmentOverhead = 10;
		
		AdvActionMAPAgent.requestThreshold = 299;
		//AdvActionMAPAgent.importanceV2 = useImportanceV2 > 0;
		//AdvActionMAPAgent.WellBeingV2 = useImportanceV2 > 1;
		AdvActionMAPAgent.WLL = 5.0;
		AdvActionMAPAgent.lowCostThreshold = 100;
		
		AdvActionMAPRepAgent.WREP = 1.0;
		
		RAAgent.EPSILON = 0.2;
		RAAgent.WREASSIGN = 0.3;
		RAAgent.WREASSIGNREQ = 1.0;
		
		/* Initialize and run the experiment */
		se.initializeExperiment(numberOfIterPerRun);			
		int[] teamScores = se.runExperiment();
		System.out.println("constant : "  + importanceConstant);
		if(teamScores.length > 0)
		{
			System.out.println("Score Help : "  + teamScores[0]);
			System.out.println("Score No : "  + teamScores[1]);
			System.out.println("---------------------------------------");
			return teamScores[0];
		}
		System.out.println("Score : 0");
		return 0;
	}
}