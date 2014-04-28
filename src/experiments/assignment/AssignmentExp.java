package experiments.assignment;

import massim.Agent;
import massim.SimulationEngine;
import massim.Team;
import massim.TeamTask;
import massim.agents.advancedactionmap.AdvActionMAPAgent;
import massim.agents.advancedactionmap.AdvActionMAPRepAgent;
import massim.agents.advancedactionmap.AdvActionMAPRepTeam;
import massim.agents.advancedactionmap.AdvActionMAPTeam;
import massim.agents.nohelp.NoHelpTeam;
import massim.agents.nohelp.NoHelpRepTeam;
import massim.agents.nohelp.NoHelpRepAgent;
import massim.ui.PerformanceStats;

/**
 * This is an experiment for initial optimum assignment v.s. random assignment
 * 
 * @author Mojtaba
 * 
 */
public class AssignmentExp {

	public static void main(String[] args) {
		try {
			if(args.length > 1) {
				if(Integer.parseInt(args[1]) == 1)
					runSimulation1(Integer.parseInt(args[0]));
				else if(Integer.parseInt(args[1]) == 2)
					runSimulation2(Integer.parseInt(args[0]));
			} else if(args.length > 0) {
				runSimulation1(Integer.parseInt(args[0]));
			}
			runSimulation2(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void runSimulation1(int numberOfRuns) throws Exception {

		if (numberOfRuns < 1)
			throw new Exception("numberOfRuns is invalid!");

		SimulationEngine.colorRange = new int[] { 0, 1, 2, 3, 4, 5 };
		SimulationEngine.numOfColors = SimulationEngine.colorRange.length;
		SimulationEngine.actionCostsRange = new int[] {10, 40, 100, 150, 250, 300, 350, 500};

		System.out
				.println("Disturbance NoHelpOpt NoHelp Difference NoHelpRepOpt RepCount NoHelpRepSwap RepCount SwapCount "
						+ "Difference");
		
		Team.teamSize = 8;
		
		/* The experiments loop */
		for (int exp1 = 0; exp1 < 11; exp1++) {
			/* Create the teams involved in the simulation */		
			Team[] teams = new Team[4];
			
			teams[0] = new NoHelpTeam();
			teams[0].setOptimumAssign(true);
			teams[1] = new NoHelpTeam();
			
			teams[2] = new NoHelpRepTeam();
			//teams[2].setOptimumAssign(true);
			teams[3] = new NoHelpRepTeam();
			((NoHelpRepTeam)teams[3]).setUseSwap(true);
			
			/* Create the SimulationEngine */
			SimulationEngine se = new SimulationEngine(teams);
			
			/* Set the experiment-wide parameters: */
			/* teams-wide, SimulationEngine, etc params */

			Team.unicastCost = 1;
			Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);
			Agent.calculationCost = 1;
			Agent.planCostCoeff = 0.03;

			TeamTask.helpOverhead = 20;
			TeamTask.cellReward = 100;
			TeamTask.achievementReward = 2000;
			TeamTask.initResCoef = 160;
			TeamTask.swapOverhead = 20;
			
			NoHelpRepAgent.WREP = -0.3;
			
			NoHelpRepAgent.swapBidThreshold = 50;
			NoHelpRepAgent.swapRequestThreshold = 1;
			NoHelpRepAgent.swapResourceThreshold = 100;
			NoHelpRepAgent.swapDeliberationThreshold = -400;
			
			AdvActionMAPAgent.WLL = -0.1;
			AdvActionMAPAgent.requestThreshold = 351;
			AdvActionMAPAgent.lowCostThreshold = 50;
			AdvActionMAPAgent.importanceVersion = 2;
			
			AdvActionMAPRepAgent.WLL = -0.1;
			AdvActionMAPRepAgent.WREP = -0.3;
			AdvActionMAPRepAgent.requestThreshold = 351;
			AdvActionMAPRepAgent.lowCostThreshold = 50;
			AdvActionMAPRepAgent.importanceVersion = 2;

			/* vary the disturbance: */
			SimulationEngine.disturbanceLevel = 0.05 * exp1;
			SimulationEngine.pulseOccurrence = new int[] { 6 };
			SimulationEngine.pulseLevel = 0.8;
			
			/* Initialize and run the experiment */
			se.initializeExperiment(numberOfRuns);
			int[] teamScores = se.runExperiment();
			
			int averageReplan1 = (int)Math.round((double)((NoHelpRepTeam)teams[2]).getReplanCounts()/numberOfRuns);
			int averageReplan2 = (int)Math.round((double)((NoHelpRepTeam)teams[3]).getReplanCounts()/numberOfRuns);
			int averageSwaps = (int)Math.round((double)teams[3].getSwapCounts()/numberOfRuns);
			
			if (teamScores.length > 1) {
				System.out.println(String.format("%.2f" +
						"\t%d\t%d\t%d\t%d" +
						"\t%d\t%d\t%d\t%d\t%d",
						SimulationEngine.disturbanceLevel, 
						teamScores[0], teamScores[1], teamScores[0] - teamScores[1], teamScores[2], 
						averageReplan1, teamScores[3], averageReplan2, averageSwaps, teamScores[2] - teamScores[3]));
			} else
				System.out.println("Score : 0");
		}
	}
	
	public static void runSimulation2(int numberOfRuns) throws Exception {

		if (numberOfRuns < 1)
			throw new Exception("numberOfRuns is invalid!");

		SimulationEngine.colorRange = new int[] { 0, 1, 2, 3, 4, 5 };
		SimulationEngine.numOfColors = SimulationEngine.colorRange.length;
		SimulationEngine.actionCostsRange = new int[] {10, 40, 100, 150, 250, 300, 350, 500};

		System.out.println("Disturbance HelpOpt Help Difference HelpRepOpt RepCount HelpRepSwap RepCount SwapCount Difference");
		
		Team.teamSize = 8;
		
		/* The experiments loop */
		for (int exp1 = 0; exp1 < 11; exp1++) {
			/* Create the teams involved in the simulation */		
			Team[] teams = new Team[2];
			
			//teams[0] = new AdvActionMAPTeam();
			//teams[0].setOptimumAssign(true);
			//teams[1] = new AdvActionMAPTeam();
			
			teams[0] = new AdvActionMAPRepTeam();
			//teams[2].setOptimumAssign(true);
			teams[1] = new AdvActionMAPRepTeam();
			((AdvActionMAPRepTeam)teams[1]).setUseSwap(true);
			
			/* Create the SimulationEngine */
			SimulationEngine se = new SimulationEngine(teams);
			
			/* Set the experiment-wide parameters: */
			/* teams-wide, SimulationEngine, etc params */

			Team.unicastCost = 1;
			Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);
			Agent.calculationCost = 1;
			Agent.planCostCoeff = 0.03;

			TeamTask.helpOverhead = 20;
			TeamTask.cellReward = 100;
			TeamTask.achievementReward = 2000;
			TeamTask.initResCoef = 160;
			TeamTask.swapOverhead = 20;
			
			NoHelpRepAgent.WREP = -0.3;
			
			AdvActionMAPAgent.WLL = -0.1;
			AdvActionMAPAgent.requestThreshold = 351;
			AdvActionMAPAgent.lowCostThreshold = 50;
			AdvActionMAPAgent.importanceVersion = 2;
			
			AdvActionMAPRepAgent.WLL = -0.1;
			AdvActionMAPRepAgent.WREP = -0.3;
			AdvActionMAPRepAgent.requestThreshold = 351;
			AdvActionMAPRepAgent.lowCostThreshold = 50;
			AdvActionMAPRepAgent.importanceVersion = 2;
			
			AdvActionMAPRepAgent.swapBidThreshold = 50;
			AdvActionMAPRepAgent.swapRequestThreshold = 425;
			AdvActionMAPRepAgent.swapResourceThreshold = 100;
			AdvActionMAPRepAgent.swapDeliberationThreshold = 0;

			/* vary the disturbance: */
			SimulationEngine.disturbanceLevel = 0.05 * exp1;
			SimulationEngine.pulseOccurrence = new int[] { 6 };
			SimulationEngine.pulseLevel = 0.8;
			SimulationEngine.maximumNoOfPulses = 2;

			/* Initialize and run the experiment */
			se.initializeExperiment(numberOfRuns);
			int[] teamScores = se.runExperiment();
			
			int averageReplan1 = (int)Math.round((double)((AdvActionMAPRepTeam)teams[0]).getReplanCounts()/numberOfRuns);
			int averageReplan2 = (int)Math.round((double)((AdvActionMAPRepTeam)teams[1]).getReplanCounts()/numberOfRuns);
			int averageSwaps = (int)Math.round((double)teams[1].getSwapCounts()/numberOfRuns);
			
			if (teamScores.length > 1) {
				System.out.println(String.format("%.2f" +
						"\t%d\t%d\t%d\t%d\t%d\t%d",
						SimulationEngine.disturbanceLevel, 
						teamScores[0], averageReplan1, teamScores[1], averageReplan2, averageSwaps, teamScores[0] - teamScores[1]));
			} else
				System.out.println("Score : 0");
		}
	}
}
