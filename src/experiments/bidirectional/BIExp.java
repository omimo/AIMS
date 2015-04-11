package experiments.bidirectional;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import massim.Agent;
import massim.SimulationEngine;
import massim.Team;
import massim.TeamTask;
import massim.agents.bidirectional.RITeam;
import massim.agents.bidirectional.RIAgent;
import massim.agents.bidirectional.HITeam;
import massim.agents.bidirectional.HIAgent;
import massim.agents.bidirectional.BITeam;
import massim.agents.bidirectional.BIAgent;

/**
 * Experiment for comparing Bidirectionally Initiated Action MAP (BIAMAP) vs. requester-initiated & helper-initiated protocols (RIAMAP* & HIAMAP*)
 * 
 * @author Mojtaba
 * @date 2015/01
 */
public class BIExp {

	public static void main(String[] args) {
		try {
			 runSimulation1(10000);
			 runSimulation2(10000);
			 runSimulation3(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void runSimulation1(int numberOfRuns) throws Exception {

		if (numberOfRuns < 1)
			throw new Exception("numberOfRuns is invalid!");

		SimulationEngine.colorRange = new int[] { 0, 1, 2, 3, 4, 5 };
		SimulationEngine.numOfColors = SimulationEngine.colorRange.length;
		SimulationEngine.actionCostsRange = new int[] { 10, 40, 100, 150, 250, 300, 350, 500 };

		Team.teamSize = 8;

		System.out.println("D	iRes	BI	RI	HI	Diff	Diff");

		/* The experiments loop */
		for (int expx = 0; expx < 11; expx++)
		{  
			for (int expy = 0; expy < 11; expy++)
			{	
				/* Create the teams involved in the simulation */
				Team[] teams = new Team[3];
				
				teams[0] = new BITeam();
				((BITeam) teams[0]).setUseHelp2Character(false);
	
				teams[1] = new RITeam();
				((RITeam) teams[1]).setUseHelp2Character(false);
				((RITeam) teams[1]).setSimHelp(true);
				
				teams[2] = new HITeam();
				((HITeam) teams[2]).setSimHelp(true);
				
				/* Create the SimulationEngine */
				SimulationEngine se = new SimulationEngine(teams);
	
				/* Set the experiment-wide parameters: */
				/* teams-wide, SimulationEngine, etc params */
	
				Team.unicastCost = 9;
				Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);
				Agent.calculationCost = 1;
				Agent.planCostCoeff = 0;
	
				TeamTask.helpOverhead = 20;
				TeamTask.cellReward = 100;
				TeamTask.achievementReward = 2000;
				TeamTask.initResCoef = 100 + 10 * expy;
	
				RIAgent.WLL = -0.2;
				RIAgent.requestThreshold = 351;
				RIAgent.lowCostThreshold = 50;
				RIAgent.importanceVersion = 2;
				
				HIAgent.WHH = 0.4;
				HIAgent.offerThreshold = 299;
				HIAgent.importanceVersion = 2;
				
				BIAgent.WLL = -0.8;
				BIAgent.WHH = 0.6;
				BIAgent.requestThreshold = 351;
				BIAgent.lowCostThreshold = 50;
				BIAgent.offerThreshold = 299;
				BIAgent.importanceVersion = 2;
	
				/* vary the disturbance: */
				SimulationEngine.disturbanceLevel = 0.05 * expx;
	
				/* Initialize and run the experiment */
				se.initializeExperiment(numberOfRuns);
				int[] teamScores = se.runExperiment();
	
				if (teamScores.length > 1) {
					System.out.println(String.format("%.2f" + "\t%d\t%d\t%d\t%d\t%d\t%d",
							SimulationEngine.disturbanceLevel, TeamTask.initResCoef, teamScores[0], teamScores[1], teamScores[2],
							teamScores[0] - teamScores[1], teamScores[0] - teamScores[2]));
					
					try {
						PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Results1" + ".txt", true)));
					    out.println(SimulationEngine.disturbanceLevel + ", " + TeamTask.initResCoef + ", " + teamScores[0] + ", " + teamScores[1] + ", " + teamScores[2]);
					    out.close();
					} catch (IOException e) {
						System.err.println("Error writing file!");
					}
				
				} else
					System.out.println("Score : 0");
			}
		}
	}
	
	public static void runSimulation2(int numberOfRuns) throws Exception {

		if (numberOfRuns < 1)
			throw new Exception("numberOfRuns is invalid!");

		SimulationEngine.colorRange = new int[] { 0, 1, 2, 3, 4, 5 };
		SimulationEngine.numOfColors = SimulationEngine.colorRange.length;
		SimulationEngine.actionCostsRange = new int[] { 10, 40, 100, 150, 250, 300, 350, 500 };

		Team.teamSize = 8;

		System.out.println("iRes	U	BI	RI	HI	Diff	Diff");

		/* The experiments loop */
		for (int expx = 0; expx < 11; expx++)
		{  
			for (int expy= 0; expy < 11; expy++)
			{	
				/* Create the teams involved in the simulation */
				Team[] teams = new Team[3];
				
				teams[0] = new BITeam();
				((BITeam) teams[0]).setUseHelp2Character(false);
	
				teams[1] = new RITeam();
				((RITeam) teams[1]).setUseHelp2Character(false);
				((RITeam) teams[1]).setSimHelp(true);
				
				teams[2] = new HITeam();
				((HITeam) teams[2]).setSimHelp(true);
				
				/* Create the SimulationEngine */
				SimulationEngine se = new SimulationEngine(teams);
	
				/* Set the experiment-wide parameters: */
				/* teams-wide, SimulationEngine, etc params */
	
				Team.unicastCost = 1 + 2 * expy;
				Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);
				Agent.calculationCost = 1;
				Agent.planCostCoeff = 0;
	
				TeamTask.helpOverhead = 20;
				TeamTask.cellReward = 100;
				TeamTask.achievementReward = 2000;
				TeamTask.initResCoef = 100 + 10 * expx;
	
				RIAgent.WLL = -0.2;
				RIAgent.requestThreshold = 351;
				RIAgent.lowCostThreshold = 50;
				RIAgent.importanceVersion = 2;
				
				HIAgent.WHH = 0.4;
				HIAgent.offerThreshold = 299;
				HIAgent.importanceVersion = 2;
				
				BIAgent.WLL = -0.8;
				BIAgent.WHH = 0.6;
				BIAgent.requestThreshold = 351;
				BIAgent.lowCostThreshold = 50;
				BIAgent.offerThreshold = 299;
				BIAgent.importanceVersion = 2;
	
				/* vary the disturbance: */
				SimulationEngine.disturbanceLevel = 0.2;
	
				/* Initialize and run the experiment */
				se.initializeExperiment(numberOfRuns);
				int[] teamScores = se.runExperiment();
	
				if (teamScores.length > 1) {
					System.out.println(String.format("%d\t%d\t%d\t%d\t%d\t%d\t%d",
							TeamTask.initResCoef, Team.unicastCost, teamScores[0], teamScores[1], teamScores[2],
							teamScores[0] - teamScores[1], teamScores[0] - teamScores[2]));
					
					try {
						PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Results2" + ".txt", true)));
					    out.println(TeamTask.initResCoef + ", " +  Team.unicastCost + ", " + teamScores[0] + ", " + teamScores[1] + ", " + teamScores[2]);
					    out.close();
					} catch (IOException e) {
						System.err.println("Error writing file!");
					}
					
				} else
					System.out.println("Score : 0");
			}
		}
	}
	
	public static void runSimulation3(int numberOfRuns) throws Exception {

		if (numberOfRuns < 1)
			throw new Exception("numberOfRuns is invalid!");

		SimulationEngine.colorRange = new int[] { 0, 1, 2, 3, 4, 5 };
		SimulationEngine.numOfColors = SimulationEngine.colorRange.length;
		SimulationEngine.actionCostsRange = new int[] { 10, 40, 100, 150, 250, 300, 350, 500 };

		Team.teamSize = 8;

		System.out.println("D	U	BI	RI	HI	Diff	Diff");

		/* The experiments loop */
		for (int expx = 0; expx < 11; expx++)
		{  
			for (int expy = 0; expy < 11; expy++)
			{	
				/* Create the teams involved in the simulation */
				Team[] teams = new Team[3];
				
				teams[0] = new BITeam();
				((BITeam) teams[0]).setUseHelp2Character(false);
	
				teams[1] = new RITeam();
				((RITeam) teams[1]).setUseHelp2Character(false);
				((RITeam) teams[1]).setSimHelp(true);
				
				teams[2] = new HITeam();
				((HITeam) teams[2]).setSimHelp(true);
				
				/* Create the SimulationEngine */
				SimulationEngine se = new SimulationEngine(teams);
	
				/* Set the experiment-wide parameters: */
				/* teams-wide, SimulationEngine, etc params */
	
				Team.unicastCost = 1 + 2 * expy;
				Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);
				Agent.calculationCost = 1;
				Agent.planCostCoeff = 0;
	
				TeamTask.helpOverhead = 20;
				TeamTask.cellReward = 100;
				TeamTask.achievementReward = 2000;
				TeamTask.initResCoef = 160;
	
				RIAgent.WLL = -0.2;
				RIAgent.requestThreshold = 351;
				RIAgent.lowCostThreshold = 50;
				RIAgent.importanceVersion = 2;
				
				HIAgent.WHH = 0.4;
				HIAgent.offerThreshold = 299;
				HIAgent.importanceVersion = 2;
				
				BIAgent.WLL = -0.8;
				BIAgent.WHH = 0.6;
				BIAgent.requestThreshold = 351;
				BIAgent.lowCostThreshold = 50;
				BIAgent.offerThreshold = 299;
				BIAgent.importanceVersion = 2;
	
				/* vary the disturbance: */
				SimulationEngine.disturbanceLevel = 0.05 * expx;
	
				/* Initialize and run the experiment */
				se.initializeExperiment(numberOfRuns);
				int[] teamScores = se.runExperiment();
	
				if (teamScores.length > 1) {
					System.out.println(String.format("%.2f" + "\t%d\t%d\t%d\t%d\t%d\t%d",
							SimulationEngine.disturbanceLevel, Team.unicastCost, teamScores[0], teamScores[1], teamScores[2],
							teamScores[0] - teamScores[1], teamScores[0] - teamScores[2]));
				
					try {
						PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Results3" + ".txt", true)));
					    out.println(SimulationEngine.disturbanceLevel + ", " + Team.unicastCost + ", " + teamScores[0] + ", " + teamScores[1] + ", " + teamScores[2]);
					    out.close();
					} catch (IOException e) {
						System.err.println("Error writing file!");
					}
				
				} else
					System.out.println("Score : 0");
			}
		}
	}
}
