package frontends.batch;

import java.text.DecimalFormat;
import java.util.Scanner;

import massim.Agent;
import massim.SEControl;
import massim.SimulationEngine;
import massim.Team;
import massim.agents.advancedactionmap.AdvActionMAPAgent;
import massim.agents.advancedactionmap.AdvActionMapTeam;
import massim.agents.empathic.EmpathicAgent;
import massim.agents.empathic.EmpathicTeam;
import massim.agents.nohelp.NoHelpTeam;


/**
 * 
 *   
 * @author Omid Alemi
 *
 */
public class BatchSim {

	public static void main(String[] args) {
		
	if (args.length !=2)
	{
		System.err.println("Error in arguments!");
		return;
	}
		
	int numberOfRuns = Integer.parseInt(args[0]);
	int exp = Integer.parseInt(args[1]);
		
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
		
		NoHelpTeam.useExp = false;
		Team[] teams = new Team[3];		
		teams[0] = new EmpathicTeam();
		teams[1] = new AdvActionMapTeam();
		teams[2] = new NoHelpTeam();
		
			
		
		/* Create the SimulationEngine */
		SimulationEngine se = new SimulationEngine(teams);
		SEControl sec = se;
		
		
		sec.addParam("env.disturbance", (Double)0.0);
		sec.addParam("agent.helpoverhead", 5);
		
		//System.out.println("DISTURBANCE,EMP,AAMAP,NO-HELP");
		
		/* The experiments loop */
		//for (int exp=0;exp<11;exp++)
		
			// percentage
			EmpathicAgent.nHelpActs = 0;
			EmpathicAgent.nHelpRequests =0;
			
			/* Set the experiment-wide parameters: */
			/* teams-wide, SimulationEngine, etc params */			
			
			Team.initResCoef = 200;
			Team.unicastCost = 7;
			Team.broadcastCost = Team.unicastCost * (Team.teamSize-1);
			Agent.calculationCost = 35;
			
			Agent.cellReward = 100;
			Agent.achievementReward = 2000;

			AdvActionMAPAgent.requestThreshold = 299;
			AdvActionMAPAgent.WLL = 0.8;
			AdvActionMAPAgent.lowCostThreshold = 100;
			
			EmpathicAgent.WTH_Threshhold = 3.5;
		  	EmpathicAgent.emotState_W = 0.3;
		  	EmpathicAgent.salience_W = 1.5;
		  	EmpathicAgent.pastExp_W = 2.0;
		  	EmpathicAgent.requestThreshold = 299;
		  	
			/* vary the disturbance: */
			//SimulationEngine.disturbanceLevel = 0.1 * exp;
			sec.changeParam("env.disturbance",  exp/100.0);
			
			/* Initialize and run the experiment */
			sec.setupExeperiment(numberOfRuns);
			int[] teamScores = sec.startExperiment();


			/* Print the results */
			DecimalFormat df = new DecimalFormat("0.00");
			System.out.print(
			df.format(sec.getParamD("env.disturbance")));
			for (int i=0;i<teams.length;i++)
				System.out.printf(",%d", teamScores[i]);
			System.out.println("");

	}

}
