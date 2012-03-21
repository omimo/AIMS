package experiments;

import java.text.DecimalFormat;
import java.util.Scanner;

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
public class Experiment1 {

	public static void main(String[] args) {
	int numberOfRuns = 1000;
		
	SimulationEngine.colorRange = 
		new int[] {0, 1, 2, 3, 4, 5};
	SimulationEngine.numOfColors =  
		SimulationEngine.colorRange.length;
	SimulationEngine.actionCostsRange = 
		new int[] {10, 40, 70, 100, 300, 400, 450,  500};	
	SimulationEngine.numOfMatches = 5;
	
	/* Create the teams involved in the simulation */
		Team.teamSize = 8;
		EmpathicTeam.useExp = true;
		AdvActionMapTeam.useExp = false;
		NoHelpTeam.useExp = true;
		Team[] teams = new Team[3];		
		teams[0] = new EmpathicTeam();
		teams[1] = new AdvActionMapTeam();
		teams[2] = new NoHelpTeam();
		
			
		
		/* Create the SimulationEngine */
		SimulationEngine se = new SimulationEngine(teams);
		
		System.out.println("DISTURBANCE,EMP,AAMAP,NO-HELP");
		
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

			AdvActionMAPAgent.requestThreshold = 299;
			AdvActionMAPAgent.WLL = 0.8;
			AdvActionMAPAgent.lowCostThreshold = 100;
			
			EmpathicAgent.WTH_Threshhold = 2.343;
		  	EmpathicAgent.emotState_W = 0.417;
		  	EmpathicAgent.salience_W = 2.611;
		  	EmpathicAgent.pastExp_W = 1.072;
		  	EmpathicAgent.requestThreshold = 299;
		  	
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
			// int i = 1;
			System.out.printf(",%d",
			teamScores[i]);
			System.out.println("");
//			 (new Scanner(System.in)).nextLine();

		}
	}

}
