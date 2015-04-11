package experiments.bidirectional;

import massim.Agent;
import massim.SimulationEngine;
import massim.Team;
import massim.TeamTask;
import massim.agents.bidirectional.RIAgent;
import massim.agents.bidirectional.RITeam;
import massim.agents.bidirectional.HIAgent;
import massim.agents.bidirectional.HITeam;

/**
 * Experiment for comparing RIAMAP* and HIAMAP* vs. RIAMAP and HIAMAP.
 * 
 * @author Mojtaba Malek Akhlagh
 * @date 2015/01
 */
public class RIHIExp {

	public static void main(String[] args) {
		try {
			 //runSimulation1(10000);
			 //runSimulation2(10000);
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

		System.out.println("D	RI	HI	RI*	HI*");

		/* The experiments loop */
		for (int expx = 0; expx < 11; expx++)
		{  
				/* Create the teams involved in the simulation */
				Team[] teams = new Team[4];
	
				teams[0] = new RITeam();
				((RITeam) teams[0]).setUseHelp2Character(false);
				((RITeam) teams[0]).setSimHelp(false);
				
				teams[1] = new HITeam();
				((HITeam) teams[1]).setSimHelp(false);
				
				teams[2] = new RITeam();
				((RITeam) teams[2]).setUseHelp2Character(false);
				((RITeam) teams[2]).setSimHelp(true);
				
				teams[3] = new HITeam();
				((HITeam) teams[3]).setSimHelp(true);
				
				/* Create the SimulationEngine */
				SimulationEngine se = new SimulationEngine(teams);
	
				/* Set the experiment-wide parameters: */
				/* teams-wide, SimulationEngine, etc params */
	
				Team.unicastCost = 21;
				Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);
				Agent.calculationCost = 1;
				Agent.planCostCoeff = 0;
	
				TeamTask.helpOverhead = 20;
				TeamTask.cellReward = 100;
				TeamTask.achievementReward = 2000;
				TeamTask.initResCoef = 200;
				
				RIAgent.WLL = -0.3;
				RIAgent.requestThreshold = 351;
				RIAgent.lowCostThreshold = 50;
				RIAgent.importanceVersion = 2;
				
				HIAgent.WHH = 0.4;
				HIAgent.offerThreshold = 299;
				HIAgent.importanceVersion = 2;
	
				/* vary the disturbance: */
				SimulationEngine.disturbanceLevel = 0.05 * expx;
	
				/* Initialize and run the experiment */
				se.initializeExperiment(numberOfRuns);
				int[] teamScores = se.runExperiment();
	
				if (teamScores.length > 1) {
					System.out.println(String.format("%.2f" + "\t%d\t%d\t%d\t%d",
							SimulationEngine.disturbanceLevel, teamScores[0], teamScores[1], teamScores[2], teamScores[3]));
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

		System.out.println("U	RI	HI	RI*	HI*");

		/* The experiments loop */
		for (int expx = 0; expx < 11; expx++)
		{  
				/* Create the teams involved in the simulation */
				Team[] teams = new Team[4];
	
				teams[0] = new RITeam();
				((RITeam) teams[0]).setUseHelp2Character(false);
				((RITeam) teams[0]).setSimHelp(false);
				
				teams[1] = new HITeam();
				((HITeam) teams[1]).setSimHelp(false);
				
				teams[2] = new RITeam();
				((RITeam) teams[2]).setUseHelp2Character(false);
				((RITeam) teams[2]).setSimHelp(true);
				
				teams[3] = new HITeam();
				((HITeam) teams[3]).setSimHelp(true);
				
				/* Create the SimulationEngine */
				SimulationEngine se = new SimulationEngine(teams);
	
				/* Set the experiment-wide parameters: */
				/* teams-wide, SimulationEngine, etc params */
	
				Team.unicastCost = 10 + 2 * expx;
				Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);
				Agent.calculationCost = 1;
				Agent.planCostCoeff = 0;
	
				TeamTask.helpOverhead = 20;
				TeamTask.cellReward = 100;
				TeamTask.achievementReward = 2000;
				TeamTask.initResCoef = 160;
				
				RIAgent.WLL = -0.3;
				RIAgent.requestThreshold = 351;
				RIAgent.lowCostThreshold = 50;
				RIAgent.importanceVersion = 2;
				
				HIAgent.WHH = 0.4;
				HIAgent.offerThreshold = 299;
				HIAgent.importanceVersion = 2;
	
				/* vary the disturbance: */
				SimulationEngine.disturbanceLevel = 0.2;
	
				/* Initialize and run the experiment */
				se.initializeExperiment(numberOfRuns);
				int[] teamScores = se.runExperiment();
	
				if (teamScores.length > 1) {
					System.out.println(String.format("%d\t%d\t%d\t%d\t%d",
							Team.unicastCost, teamScores[0], teamScores[1], teamScores[2], teamScores[3]));
				} else
					System.out.println("Score : 0");
			}
		}
	
	public static void runSimulation3(int numberOfRuns) throws Exception {

		if (numberOfRuns < 1)
			throw new Exception("numberOfRuns is invalid!");

		SimulationEngine.colorRange = new int[] { 0, 1, 2, 3, 4, 5 };
		SimulationEngine.numOfColors = SimulationEngine.colorRange.length;
		SimulationEngine.actionCostsRange = new int[] { 10, 40, 100, 150, 250, 300, 350, 500 };

		Team.teamSize = 8;

		System.out.println("R	RI	HI	RI*	HI*");

		/* The experiments loop */
		for (int expx = 0; expx < 11; expx++)
		{  
				/* Create the teams involved in the simulation */
				Team[] teams = new Team[4];
	
				teams[0] = new RITeam();
				((RITeam) teams[0]).setUseHelp2Character(false);
				((RITeam) teams[0]).setSimHelp(false);
				
				teams[1] = new HITeam();
				((HITeam) teams[1]).setSimHelp(false);
				
				teams[2] = new RITeam();
				((RITeam) teams[2]).setUseHelp2Character(false);
				((RITeam) teams[2]).setSimHelp(true);
				
				teams[3] = new HITeam();
				((HITeam) teams[3]).setSimHelp(true);
				
				/* Create the SimulationEngine */
				SimulationEngine se = new SimulationEngine(teams);
	
				/* Set the experiment-wide parameters: */
				/* teams-wide, SimulationEngine, etc params */
	
				Team.unicastCost = 3;
				Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);
				Agent.calculationCost = 1;
				Agent.planCostCoeff = 0;
	
				TeamTask.helpOverhead = 20;
				TeamTask.cellReward = 100;
				TeamTask.achievementReward = 2000;
				TeamTask.initResCoef = 100 + 10 * expx;

				RIAgent.WLL = -0.3;
				RIAgent.requestThreshold = 351;
				RIAgent.lowCostThreshold = 50;
				RIAgent.importanceVersion = 2;
				
				HIAgent.WHH = 0.4;
				HIAgent.offerThreshold = 299;
				HIAgent.importanceVersion = 2;
	
				/* vary the disturbance: */
				SimulationEngine.disturbanceLevel = 0.3;
	
				/* Initialize and run the experiment */
				se.initializeExperiment(numberOfRuns);
				int[] teamScores = se.runExperiment();
	
				if (teamScores.length > 1) {
					System.out.println(String.format("%d\t%d\t%d\t%d\t%d",
							TeamTask.initResCoef, teamScores[0], teamScores[1], teamScores[2], teamScores[3]));
				} else
					System.out.println("Score : 0");
			}
		}
	
	}
