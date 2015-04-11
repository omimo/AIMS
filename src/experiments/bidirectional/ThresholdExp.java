package experiments.bidirectional;

import massim.Agent;
import massim.SimulationEngine;
import massim.Team;
import massim.TeamTask;
import massim.agents.bidirectional.BIAgent;
import massim.agents.bidirectional.BITeam;
import massim.agents.bidirectional.HIAgent;
import massim.agents.bidirectional.HITeam;
import massim.agents.bidirectional.RIAgent;
import massim.agents.bidirectional.RITeam;

/**
 * Experiment for finding best WLL and WHH values for RIAMAP*, HIAMAP*, and BIAMAP
 * 
 * @author Mojtaba
 * @date 2015/01
 */
public class ThresholdExp {

	public static void main(String[] args) {
		try {
			 //runSimulation1(10000);
			 runSimulation2(10000);
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

		System.out.println("WLL	RI*	WHH	HI*");

				for (int expx = 0; expx < 10; expx++)
				{	
					/* Create the teams involved in the simulation */
					Team[] teams = new Team[2];
		
					teams[0] = new RITeam();
					((RITeam) teams[0]).setUseHelp2Character(false);
					((RITeam) teams[0]).setSimHelp(true);
					
					teams[1] = new HITeam();
					((HITeam) teams[1]).setSimHelp(true);
		
					/* Create the SimulationEngine */
					SimulationEngine se = new SimulationEngine(teams);
		
					/* Set the experiment-wide parameters: */
					/* teams-wide, SimulationEngine, etc params */
		
					Team.unicastCost = 9;
					Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);
					Agent.calculationCost = 1;
					Agent.planCostCoeff = 0.0;
		
					TeamTask.helpOverhead = 20;
					TeamTask.cellReward = 100;
					TeamTask.achievementReward = 2000;
					TeamTask.initResCoef = 160;
					
					RIAgent.WLL = -0.1 * expx;
					RIAgent.requestThreshold = 351;
					RIAgent.lowCostThreshold = 50;
					RIAgent.importanceVersion = 2;
					
					HIAgent.WHH = 0.1 * expx;
					HIAgent.offerThreshold = 299;
					HIAgent.importanceVersion = 2;	
		
					/* vary the disturbance: */
					SimulationEngine.disturbanceLevel = 0.2;
		
					/* Initialize and run the experiment */
					se.initializeExperiment(numberOfRuns);
					int[] teamScores = se.runExperiment();
		
					if (teamScores.length > 1) {
						System.out.println(String.format("%.2f" + "\t%d" + "\t%.2f" + "\t%d",
								RIAgent.WLL, teamScores[0], HIAgent.WHH, teamScores[1]));
					} else
						System.out.println("Score : 0");
				}
			}	
	

	public static void runSimulation2(int numberOfRuns) throws Exception {

		if (numberOfRuns < 1)
			throw new Exception("numberOfRuns is invalid!");

		SimulationEngine.colorRange = new int[] { 0, 1, 2, 3, 4, 5 };
		SimulationEngine.numOfColors = SimulationEngine.colorRange.length;
		SimulationEngine.actionCostsRange = new int[] { 10, 40, 100, 150, 250, 300, 350, 500 };

		Team.teamSize = 8;

		System.out.println("WLL	WHH	BI");

			for (int expx = 0; expx < 10; expx++)
			{	
				for (int expy = 0; expy < 10; expy++)
				{	
					/* Create the teams involved in the simulation */
					Team[] teams = new Team[1];
		
					teams[0] = new BITeam();
					((BITeam) teams[0]).setUseHelp2Character(false);
		
					/* Create the SimulationEngine */
					SimulationEngine se = new SimulationEngine(teams);
		
					/* Set the experiment-wide parameters: */
					/* teams-wide, SimulationEngine, etc params */
		
					Team.unicastCost = 9;
					Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);
					Agent.calculationCost = 1;
					Agent.planCostCoeff = 0.0;
		
					TeamTask.helpOverhead = 20;
					TeamTask.cellReward = 100;
					TeamTask.achievementReward = 2000;
					TeamTask.initResCoef = 160;
					
					BIAgent.WLL = -0.1 * expx;
					BIAgent.WHH = 0.1 * expy;
					BIAgent.requestThreshold = 351;
					BIAgent.lowCostThreshold = 50;
					BIAgent.offerThreshold = 299;
					BIAgent.importanceVersion = 2;
		
					/* vary the disturbance: */
					SimulationEngine.disturbanceLevel = 0.2;
		
					/* Initialize and run the experiment */
					se.initializeExperiment(numberOfRuns);
					int[] teamScores = se.runExperiment();
		
					//if (teamScores.length > 1) {
						System.out.println(String.format("%.2f\t%.2f" + "\t%d",
								BIAgent.WLL, BIAgent.WHH, teamScores[0]));
					//} else
						//System.out.println("Score : 0");
				}
			}	
	}

}
