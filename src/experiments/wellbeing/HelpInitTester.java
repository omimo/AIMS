package experiments.wellbeing;

import java.io.*;

import massim.Agent;
import massim.SimulationEngine;
import massim.Team;
import massim.TeamTask;
import massim.agents.advancedactionmap.AdvActionMAPAgent;
import massim.agents.advancedactionmap.AdvActionMAPRepAgent;
import massim.agents.advancedactionmap.AdvActionMapTeam;
import massim.agents.basicactionmap.BasicActionMAPAgent;
import massim.agents.basicactionmap.BasicActionMAPTeam;
import massim.agents.helperinitactionmap.HelperInitActionMAPAgent;
import massim.agents.helperinitactionmap.HelperInitActionMAPTeam;
import massim.agents.reassignment.RAAgent;


/**
 * This is an experiment for testing wellbeing expression of agents
 * over Resource Multiplier, Disturbance, No of Runs & Expression versions
 * 
 *   
 * @author Denish M
 *
 */
public class HelpInitTester {

	public static void main(String[] args) {
			try {
				runSimulation(10, 2);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	/*
	 * There are two versions of wellbeing.
	 */
	public static void runSimulation(int numberOfRuns, int importanceVersion) throws Exception {
		if(importanceVersion < 1 || importanceVersion > 2)
			throw new Exception("Wellbeing value is invalid!");
		if(numberOfRuns < 1)
			throw new Exception("numberOfRuns is invalid!");
		
		SimulationEngine.colorRange = 
			new int[] {0, 1, 2, 3, 4, 5};
		SimulationEngine.numOfColors =  
			SimulationEngine.colorRange.length;
		SimulationEngine.actionCostsRange = 
		new int[] {10, 40, 70, 100, 300, 400, 450,  500};	
	
		/* Create the teams involved in the simulation */
		Team.teamSize = 8;
		Team[] teams = new Team[2];		
		teams[0] = new AdvActionMapTeam();
		teams[1] = new HelperInitActionMAPTeam();
		
		/* Create the SimulationEngine */
		SimulationEngine se = new SimulationEngine(teams);
		System.out.println("DISTURBANCE\tAD-ACTION-MAP\tBASIC-ACTION");
		/* The experiments loop */
		for (int exp=0;exp<11;exp++)
		{
			/* Set the experiment-wide parameters: */
			/* teams-wide, SimulationEngine, etc params */			
			
			Team.unicastCost = 1;
			Team.broadcastCost = Team.unicastCost * (Team.teamSize-1);
			Agent.calculationCost = 1;
			
			TeamTask.helpOverhead = 30;			
			TeamTask.cellReward = 100;
			TeamTask.achievementReward = 2000;
			TeamTask.initResCoef = 150;
			TeamTask.assignmentOverhead = 10;
			
			AdvActionMAPAgent.requestThreshold = 299;
			AdvActionMAPAgent.WLL = 5.0;
			AdvActionMAPAgent.lowCostThreshold = 100;
			AdvActionMAPAgent.importanceVersion = importanceVersion;
			
			HelperInitActionMAPAgent.requestThreshold = 299;
			HelperInitActionMAPAgent.WHH = 0.3;
			HelperInitActionMAPAgent.WHL = -0.5;
			HelperInitActionMAPAgent.EPSILON = 0.1;
			HelperInitActionMAPAgent.importanceVersion = importanceVersion;
			
			RAAgent.EPSILON = 0.2;
			RAAgent.WREASSIGN = 0.3;
			RAAgent.WREASSIGNREQ = 1.0;
			
			/* vary the disturbance: */
			SimulationEngine.disturbanceLevel = 0.1 * exp;;
			
			/* Initialize and run the experiment */
			se.initializeExperiment(numberOfRuns);			
			int[] teamScores = se.runExperiment();
			
			
			if(teamScores.length > 1)
			{
				System.out.println(String.format("%.2f\t\t%d\t\t%d", SimulationEngine.disturbanceLevel, teamScores[0], teamScores[1]));
			}
			else
				System.out.println("Score : 0");
		}
	}
}