package experiments.smc2014;

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

/**
 * Experiment for optimum initial assignment vs random initial assignment
 * 
 * @author Mojtaba
 * 
 */
public class AssignmentExp {

	public static void main(String[] args) {
		try {
			if (args.length > 1) {
				if (Integer.parseInt(args[1]) == 1)
					runSimulation1(Integer.parseInt(args[0]));
				else if (Integer.parseInt(args[1]) == 2)
					runSimulation2(Integer.parseInt(args[0]));
				else if (Integer.parseInt(args[1]) == 3)
					runSimulation3(Integer.parseInt(args[0]));
			}
			runSimulation1(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void runSimulation1(int numberOfRuns) throws Exception {

		if (numberOfRuns < 1)
			throw new Exception("numberOfRuns is invalid!");

		SimulationEngine.colorRange = new int[] { 0, 1, 2, 3, 4, 5 };
		SimulationEngine.numOfColors = SimulationEngine.colorRange.length;
		SimulationEngine.actionCostsRange = new int[] { 10, 40, 100, 150, 250,
				300, 350, 500 };

		Team.teamSize = 8;

		System.out
				.println("Disturbance NoHelp NoHelpOpt Difference NoHelpRep RepCount NoHelpRepOpt RepCount "
						+ "Difference Help HelpOpt Difference HelpRep RepCount HelpRepOpt RepCount Difference");

		/* The experiments loop */
		for (int exp1 = 0; exp1 < 11; exp1++) {
			/* Create the teams involved in the simulation */
			Team[] teams = new Team[8];

			teams[0] = new NoHelpTeam();
			teams[1] = new NoHelpTeam();
			teams[1].setOptimumAssign(true);

			teams[2] = new NoHelpRepTeam();
			teams[3] = new NoHelpRepTeam();
			teams[3].setOptimumAssign(true);

			teams[4] = new AdvActionMAPTeam();
			teams[5] = new AdvActionMAPTeam();
			teams[5].setOptimumAssign(true);

			teams[6] = new AdvActionMAPRepTeam();
			teams[7] = new AdvActionMAPRepTeam();
			teams[7].setOptimumAssign(true);

			/* Create the SimulationEngine */
			SimulationEngine se = new SimulationEngine(teams);

			/* Set the experiment-wide parameters: */
			/* teams-wide, SimulationEngine, etc params */

			Team.unicastCost = 1;
			Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);
			Agent.calculationCost = 1;
			Agent.planCostCoeff = 0.025;

			TeamTask.helpOverhead = 20;
			TeamTask.cellReward = 100;
			TeamTask.achievementReward = 2000;
			TeamTask.initResCoef = 160;

			NoHelpRepAgent.WREP = -0.25;

			AdvActionMAPAgent.WLL = -0.1;
			AdvActionMAPAgent.requestThreshold = 351;
			AdvActionMAPAgent.lowCostThreshold = 50;
			AdvActionMAPAgent.importanceVersion = 2;

			AdvActionMAPRepAgent.WLL = -0.1;
			AdvActionMAPRepAgent.WREP = -0.25;
			AdvActionMAPRepAgent.requestThreshold = 351;
			AdvActionMAPRepAgent.lowCostThreshold = 50;
			AdvActionMAPRepAgent.importanceVersion = 2;

			/* vary the disturbance: */
			SimulationEngine.disturbanceLevel = 0.05 * exp1;

			/* Initialize and run the experiment */
			se.initializeExperiment(numberOfRuns);
			int[] teamScores = se.runExperiment();

			int averageReplan2 = (int) Math
					.round((double) ((NoHelpRepTeam) teams[2])
							.getReplanCounts() / numberOfRuns);
			int averageReplan3 = (int) Math
					.round((double) ((NoHelpRepTeam) teams[3])
							.getReplanCounts() / numberOfRuns);
			int averageReplan6 = (int) Math
					.round((double) ((AdvActionMAPRepTeam) teams[6])
							.getReplanCounts() / numberOfRuns);
			int averageReplan7 = (int) Math
					.round((double) ((AdvActionMAPRepTeam) teams[7])
							.getReplanCounts() / numberOfRuns);

			if (teamScores.length > 1) {
				System.out.println(String.format("%.2f" + "\t%d\t%d\t%d\t%d"
						+ "\t%d\t%d\t%d\t%d" + "\t%d\t%d\t%d\t%d"
						+ "\t%d\t%d\t%d\t%d",
						SimulationEngine.disturbanceLevel, teamScores[0],
						teamScores[1], teamScores[1] - teamScores[0],
						teamScores[2], averageReplan2, teamScores[3],
						averageReplan3, teamScores[3] - teamScores[2],
						teamScores[4], teamScores[5], teamScores[5]
								- teamScores[4], teamScores[6], averageReplan6,
						teamScores[7], averageReplan7, teamScores[7]
								- teamScores[6]));
			} else
				System.out.println("Score : 0");
		}
	}

	public static void runSimulation2(int numberOfRuns) throws Exception {

		if (numberOfRuns < 1)
			throw new Exception("numberOfRuns is invalid!");

		SimulationEngine.colorRange = new int[] { 0, 1, 2, 3, 4, 5 };
		SimulationEngine.numOfColors = SimulationEngine.colorRange.length;
		SimulationEngine.actionCostsRange = new int[] { 10, 40, 100, 150, 250,
				300, 350, 500 };

		Team.teamSize = 8;

		System.out
				.println("InitResource NoHelp NoHelpOpt Difference NoHelpRep RepCount NoHelpRepOpt RepCount "
						+ "Difference Help HelpOpt Difference HelpRep RepCount HelpRepOpt RepCount Difference");

		for (int exp2 = 0; exp2 < 11; exp2++) {

			Team[] teams = new Team[8];

			teams[0] = new NoHelpTeam();
			teams[1] = new NoHelpTeam();
			teams[1].setOptimumAssign(true);

			teams[2] = new NoHelpRepTeam();
			teams[3] = new NoHelpRepTeam();
			teams[3].setOptimumAssign(true);

			teams[4] = new AdvActionMAPTeam();
			teams[5] = new AdvActionMAPTeam();
			teams[5].setOptimumAssign(true);

			teams[6] = new AdvActionMAPRepTeam();
			teams[7] = new AdvActionMAPRepTeam();
			teams[7].setOptimumAssign(true);

			SimulationEngine se = new SimulationEngine(teams);

			Team.unicastCost = 1;
			Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);
			Agent.calculationCost = 1;
			Agent.planCostCoeff = 0.025;

			TeamTask.helpOverhead = 20;
			TeamTask.cellReward = 100;
			TeamTask.achievementReward = 2000;

			TeamTask.initResCoef = 100 + 10 * exp2;

			NoHelpRepAgent.WREP = -0.25;

			AdvActionMAPAgent.WLL = -0.1;
			AdvActionMAPAgent.requestThreshold = 351;
			AdvActionMAPAgent.lowCostThreshold = 50;
			AdvActionMAPAgent.importanceVersion = 2;

			AdvActionMAPRepAgent.WLL = -0.1;
			AdvActionMAPRepAgent.WREP = -0.25;
			AdvActionMAPRepAgent.requestThreshold = 351;
			AdvActionMAPRepAgent.lowCostThreshold = 50;
			AdvActionMAPRepAgent.importanceVersion = 2;

			SimulationEngine.disturbanceLevel = 0.1;

			se.initializeExperiment(numberOfRuns);
			int[] teamScores = se.runExperiment();

			int averageReplan2 = (int) Math
					.round((double) ((NoHelpRepTeam) teams[2])
							.getReplanCounts() / numberOfRuns);
			int averageReplan3 = (int) Math
					.round((double) ((NoHelpRepTeam) teams[3])
							.getReplanCounts() / numberOfRuns);
			int averageReplan6 = (int) Math
					.round((double) ((AdvActionMAPRepTeam) teams[6])
							.getReplanCounts() / numberOfRuns);
			int averageReplan7 = (int) Math
					.round((double) ((AdvActionMAPRepTeam) teams[7])
							.getReplanCounts() / numberOfRuns);

			if (teamScores.length > 1) {
				System.out.println(String.format("%d" + "\t%d\t%d\t%d\t%d"
						+ "\t%d\t%d\t%d\t%d" + "\t%d\t%d\t%d\t%d"
						+ "\t%d\t%d\t%d\t%d", TeamTask.initResCoef,
						teamScores[0], teamScores[1], teamScores[1]
								- teamScores[0], teamScores[2], averageReplan2,
						teamScores[3], averageReplan3, teamScores[3]
								- teamScores[2], teamScores[4], teamScores[5],
						teamScores[5] - teamScores[4], teamScores[6],
						averageReplan6, teamScores[7], averageReplan7,
						teamScores[7] - teamScores[6]));
			} else
				System.out.println("Score : 0");
		}
	}

	public static void runSimulation3(int numberOfRuns) throws Exception {

		if (numberOfRuns < 1)
			throw new Exception("numberOfRuns is invalid!");

		SimulationEngine.colorRange = new int[] { 0, 1, 2, 3, 4, 5 };
		SimulationEngine.numOfColors = SimulationEngine.colorRange.length;
		SimulationEngine.actionCostsRange = new int[] { 10, 40, 100, 150, 250,
				300, 350, 500 };

		Team.teamSize = 8;

		System.out
				.println("UnicastCost NoHelp NoHelpOpt Difference NoHelpRep RepCount NoHelpRepOpt RepCount "
						+ "Difference Help HelpOpt Difference HelpRep RepCount HelpRepOpt RepCount Difference");

		for (int exp3 = 0; exp3 < 11; exp3++) {

			Team[] teams = new Team[8];

			teams[0] = new NoHelpTeam();
			teams[1] = new NoHelpTeam();
			teams[1].setOptimumAssign(true);

			teams[2] = new NoHelpRepTeam();
			teams[3] = new NoHelpRepTeam();
			teams[3].setOptimumAssign(true);

			teams[4] = new AdvActionMAPTeam();
			teams[5] = new AdvActionMAPTeam();
			teams[5].setOptimumAssign(true);

			teams[6] = new AdvActionMAPRepTeam();
			teams[7] = new AdvActionMAPRepTeam();
			teams[7].setOptimumAssign(true);

			SimulationEngine se = new SimulationEngine(teams);

			Team.unicastCost = 1 + 2 * exp3;

			Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);
			Agent.calculationCost = 1;
			Agent.planCostCoeff = 0.025;

			TeamTask.helpOverhead = 20;
			TeamTask.cellReward = 100;
			TeamTask.achievementReward = 2000;
			TeamTask.initResCoef = 160;

			NoHelpRepAgent.WREP = -0.25;

			AdvActionMAPAgent.WLL = -0.1;
			AdvActionMAPAgent.requestThreshold = 351;
			AdvActionMAPAgent.lowCostThreshold = 50;
			AdvActionMAPAgent.importanceVersion = 2;

			AdvActionMAPRepAgent.WLL = -0.1;
			AdvActionMAPRepAgent.WREP = -0.25;
			AdvActionMAPRepAgent.requestThreshold = 351;
			AdvActionMAPRepAgent.lowCostThreshold = 50;
			AdvActionMAPRepAgent.importanceVersion = 2;

			SimulationEngine.disturbanceLevel = 0.1;

			se.initializeExperiment(numberOfRuns);
			int[] teamScores = se.runExperiment();

			int averageReplan2 = (int) Math
					.round((double) ((NoHelpRepTeam) teams[2])
							.getReplanCounts() / numberOfRuns);
			int averageReplan3 = (int) Math
					.round((double) ((NoHelpRepTeam) teams[3])
							.getReplanCounts() / numberOfRuns);
			int averageReplan6 = (int) Math
					.round((double) ((AdvActionMAPRepTeam) teams[6])
							.getReplanCounts() / numberOfRuns);
			int averageReplan7 = (int) Math
					.round((double) ((AdvActionMAPRepTeam) teams[7])
							.getReplanCounts() / numberOfRuns);

			if (teamScores.length > 1) {
				System.out.println(String.format("%d" + "\t%d\t%d\t%d\t%d"
						+ "\t%d\t%d\t%d\t%d" + "\t%d\t%d\t%d\t%d"
						+ "\t%d\t%d\t%d\t%d", Team.unicastCost, teamScores[0],
						teamScores[1], teamScores[1] - teamScores[0],
						teamScores[2], averageReplan2, teamScores[3],
						averageReplan3, teamScores[3] - teamScores[2],
						teamScores[4], teamScores[5], teamScores[5]
								- teamScores[4], teamScores[6], averageReplan6,
						teamScores[7], averageReplan7, teamScores[7]
								- teamScores[6]));
			} else
				System.out.println("Score : 0");
		}
	}

}
