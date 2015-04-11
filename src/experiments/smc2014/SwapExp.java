package experiments.smc2014;

import massim.Agent;
import massim.SimulationEngine;
import massim.Team;
import massim.TeamTask;
import massim.agents.advancedactionmap.AdvActionMAPAgent;
import massim.agents.advancedactionmap.AdvActionMAPRepAgent;
import massim.agents.advancedactionmap.AdvActionMAPRepTeam;
import massim.agents.advancedactionmap.AdvActionMAPTeam;
import massim.agents.nohelp.NoHelpRepTeam;
import massim.agents.nohelp.NoHelpRepAgent;
import massim.ui.PerformanceStats;

/**
 * Experiment for Swapping protocol with optimum initial assignment
 * 
 * @author Mojtaba
 * 
 */
public class SwapExp {

	public static void main(String[] args) {
		try {
			if (args.length > 1) {
				if (Integer.parseInt(args[1]) == 1)
					runSimulation1(Integer.parseInt(args[0]));
				else if (Integer.parseInt(args[1]) == 2)
					runSimulation2(Integer.parseInt(args[0]));
				else if (Integer.parseInt(args[1]) == 3)
					runSimulation3(Integer.parseInt(args[0]));
				else if (Integer.parseInt(args[1]) == 4)
					runSimulation4(Integer.parseInt(args[0]));
				else if (Integer.parseInt(args[1]) == 5)
					runSimulation5(Integer.parseInt(args[0]));
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

		System.out
				.println("Disturbance NoHelpRepOpt RepCount Help2Opt HelpCount Help2RepOpt HelpCount "
						+ "RepCount NoHelpRepOptSwap RepCount SwapCount SwapReq SwapBid SwapAbort "
						+ "Help2RepOptSwap HelpCount RepCount SwapCount SwapReq SwapBid SwapAbort");

		Team.teamSize = 8;

		/* The experiments loop */
		for (int exp1 = 0; exp1 < 11; exp1++) {
			/* Create the teams involved in the simulation */
			Team[] teams = new Team[5];

			teams[0] = new NoHelpRepTeam();
			teams[0].setOptimumAssign(true);

			teams[1] = new AdvActionMAPTeam();
			teams[1].setOptimumAssign(true);
			((AdvActionMAPTeam) teams[1]).setUseHelp2Character(true);

			teams[2] = new AdvActionMAPRepTeam();
			teams[2].setOptimumAssign(true);
			((AdvActionMAPRepTeam) teams[2]).setUseHelp2Character(true);

			teams[3] = new NoHelpRepTeam();
			teams[3].setOptimumAssign(true);
			((NoHelpRepTeam) teams[3]).setUseSwap(true);

			teams[4] = new AdvActionMAPRepTeam();
			teams[4].setOptimumAssign(true);
			((AdvActionMAPRepTeam) teams[4]).setUseHelp2Character(true);
			((AdvActionMAPRepTeam) teams[4]).setUseSwap(true);

			/* Create the SimulationEngine */
			SimulationEngine se = new SimulationEngine(teams);

			/* Set the experiment-wide parameters: */
			/* teams-wide, SimulationEngine, etc params */

			Team.unicastCost = 1;
			Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);
			Agent.calculationCost = 1;
			Agent.planCostCoeff = 0.025;

			TeamTask.helpOverhead = 20;
			TeamTask.swapOverhead = 20;
			TeamTask.cellReward = 100;
			TeamTask.achievementReward = 2000;
			TeamTask.initResCoef = 110;

			NoHelpRepAgent.WREP = -0.25;

			NoHelpRepAgent.swapBidThreshold = 50;
			NoHelpRepAgent.swapRequestThreshold = 100;
			NoHelpRepAgent.swapResourceThreshold = 100;
			NoHelpRepAgent.swapDeliberationThreshold = 0;

			AdvActionMAPAgent.WLL = -0.1;
			AdvActionMAPAgent.requestThreshold = 351;
			AdvActionMAPAgent.lowCostThreshold = 50;
			AdvActionMAPAgent.importanceVersion = 2;

			AdvActionMAPRepAgent.WLL = -0.1;
			AdvActionMAPRepAgent.WREP = -0.25;
			AdvActionMAPRepAgent.requestThreshold = 351;
			AdvActionMAPRepAgent.lowCostThreshold = 50;
			AdvActionMAPRepAgent.importanceVersion = 2;

			AdvActionMAPRepAgent.swapBidThreshold = 50;
			AdvActionMAPRepAgent.swapRequestThreshold = 100;
			AdvActionMAPRepAgent.swapResourceThreshold = 100;
			AdvActionMAPRepAgent.swapDeliberationThreshold = 0;

			/* vary the disturbance: */
			SimulationEngine.disturbanceLevel = 0.05 * exp1;
			SimulationEngine.pulseOccurrence = new int[] { 4 };
			SimulationEngine.pulseLevel = 0.8;

			/* Initialize and run the experiment */
			se.initializeExperiment(numberOfRuns);
			int[] teamScores = se.runExperiment();

			int averageHelp1 = (int) Math.round((double) (teams[1])
					.getSucOffersCounts() / numberOfRuns);
			int averageHelp2 = (int) Math.round((double) (teams[2])
					.getSucOffersCounts() / numberOfRuns);
			int averageHelp4 = (int) Math.round((double) (teams[4])
					.getSucOffersCounts() / numberOfRuns);

			int averageReplan0 = (int) Math
					.round((double) ((NoHelpRepTeam) teams[0])
							.getReplanCounts() / numberOfRuns);
			int averageReplan2 = (int) Math
					.round((double) ((AdvActionMAPRepTeam) teams[2])
							.getReplanCounts() / numberOfRuns);
			int averageReplan3 = (int) Math
					.round((double) ((NoHelpRepTeam) teams[3])
							.getReplanCounts() / numberOfRuns);
			int averageReplan4 = (int) Math
					.round((double) ((AdvActionMAPRepTeam) teams[4])
							.getReplanCounts() / numberOfRuns);

			int averageSwap3 = (int) Math.round((double) teams[3]
					.getSwapCounts() / numberOfRuns);
			int averageSwap4 = (int) Math.round((double) teams[4]
					.getSwapCounts() / numberOfRuns);

			int averageSwapReq3 = (int) Math.round((double) teams[3]
					.getSwapRequests() / numberOfRuns);
			int averageSwapReq4 = (int) Math.round((double) teams[4]
					.getSwapRequests() / numberOfRuns);

			int averageSwapBid3 = (int) Math.round((double) teams[3]
					.getSwapBids() / numberOfRuns);
			int averageSwapBid4 = (int) Math.round((double) teams[4]
					.getSwapBids() / numberOfRuns);

			int averageSwapAbort3 = (int) Math.round((double) teams[3]
					.getSwapAborts() / numberOfRuns);
			int averageSwapAbort4 = (int) Math.round((double) teams[4]
					.getSwapAborts() / numberOfRuns);

			if (teamScores.length > 1) {
				System.out.println(String.format("%.2f"
						+ "\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d"
						+ "\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d",
						SimulationEngine.disturbanceLevel, teamScores[0],
						averageReplan0, teamScores[1], averageHelp1,
						teamScores[2], averageHelp2, averageReplan2,
						teamScores[3], averageReplan3, averageSwap3,
						averageSwapReq3, averageSwapBid3, averageSwapAbort3,
						teamScores[4], averageHelp4, averageReplan4,
						averageSwap4, averageSwapReq4, averageSwapBid4,
						averageSwapAbort4));
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

		System.out
				.println("WREP NoHelpRepOpt RepCount Help2Opt HelpCount Help2RepOpt HelpCount "
						+ "RepCount NoHelpRepOptSwap RepCount SwapCount SwapReq SwapBid SwapAbort "
						+ "Help2RepOptSwap HelpCount RepCount SwapCount SwapReq SwapBid SwapAbort");

		Team.teamSize = 8;

		/* The experiments loop */
		for (int exp2 = 0; exp2 < 8; exp2++) {
			/* Create the teams involved in the simulation */
			Team[] teams = new Team[5];

			teams[0] = new NoHelpRepTeam();
			teams[0].setOptimumAssign(true);

			teams[1] = new AdvActionMAPTeam();
			teams[1].setOptimumAssign(true);
			((AdvActionMAPTeam) teams[1]).setUseHelp2Character(true);

			teams[2] = new AdvActionMAPRepTeam();
			teams[2].setOptimumAssign(true);
			((AdvActionMAPRepTeam) teams[2]).setUseHelp2Character(true);

			teams[3] = new NoHelpRepTeam();
			teams[3].setOptimumAssign(true);
			((NoHelpRepTeam) teams[3]).setUseSwap(true);

			teams[4] = new AdvActionMAPRepTeam();
			teams[4].setOptimumAssign(true);
			((AdvActionMAPRepTeam) teams[4]).setUseHelp2Character(true);
			((AdvActionMAPRepTeam) teams[4]).setUseSwap(true);

			/* Create the SimulationEngine */
			SimulationEngine se = new SimulationEngine(teams);

			/* Set the experiment-wide parameters: */
			/* teams-wide, SimulationEngine, etc params */

			Team.unicastCost = 1;
			Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);
			Agent.calculationCost = 1;
			Agent.planCostCoeff = 0.025;

			TeamTask.helpOverhead = 20;
			TeamTask.swapOverhead = 20;
			TeamTask.cellReward = 100;
			TeamTask.achievementReward = 2000;
			TeamTask.initResCoef = 110;

			NoHelpRepAgent.WREP = -0.5 + 0.05 * exp2;

			NoHelpRepAgent.swapBidThreshold = 50;
			NoHelpRepAgent.swapRequestThreshold = 100;
			NoHelpRepAgent.swapResourceThreshold = 100;
			NoHelpRepAgent.swapDeliberationThreshold = 0;

			AdvActionMAPAgent.WLL = -0.1;
			AdvActionMAPAgent.requestThreshold = 351;
			AdvActionMAPAgent.lowCostThreshold = 50;
			AdvActionMAPAgent.importanceVersion = 2;

			AdvActionMAPRepAgent.WLL = -0.1;

			AdvActionMAPRepAgent.WREP = -0.5 + 0.05 * exp2;

			AdvActionMAPRepAgent.requestThreshold = 351;
			AdvActionMAPRepAgent.lowCostThreshold = 50;
			AdvActionMAPRepAgent.importanceVersion = 2;

			AdvActionMAPRepAgent.swapBidThreshold = 50;
			AdvActionMAPRepAgent.swapRequestThreshold = 100;
			AdvActionMAPRepAgent.swapResourceThreshold = 100;
			AdvActionMAPRepAgent.swapDeliberationThreshold = 0;

			/* vary the disturbance: */
			SimulationEngine.disturbanceLevel = 0.1;
			SimulationEngine.pulseOccurrence = new int[] { 4 };
			SimulationEngine.pulseLevel = 0.8;

			/* Initialize and run the experiment */
			se.initializeExperiment(numberOfRuns);
			int[] teamScores = se.runExperiment();

			int averageHelp1 = (int) Math.round((double) (teams[1])
					.getSucOffersCounts() / numberOfRuns);
			int averageHelp2 = (int) Math.round((double) (teams[2])
					.getSucOffersCounts() / numberOfRuns);
			int averageHelp4 = (int) Math.round((double) (teams[4])
					.getSucOffersCounts() / numberOfRuns);

			int averageReplan0 = (int) Math
					.round((double) ((NoHelpRepTeam) teams[0])
							.getReplanCounts() / numberOfRuns);
			int averageReplan2 = (int) Math
					.round((double) ((AdvActionMAPRepTeam) teams[2])
							.getReplanCounts() / numberOfRuns);
			int averageReplan3 = (int) Math
					.round((double) ((NoHelpRepTeam) teams[3])
							.getReplanCounts() / numberOfRuns);
			int averageReplan4 = (int) Math
					.round((double) ((AdvActionMAPRepTeam) teams[4])
							.getReplanCounts() / numberOfRuns);

			int averageSwap3 = (int) Math.round((double) teams[3]
					.getSwapCounts() / numberOfRuns);
			int averageSwap4 = (int) Math.round((double) teams[4]
					.getSwapCounts() / numberOfRuns);

			int averageSwapReq3 = (int) Math.round((double) teams[3]
					.getSwapRequests() / numberOfRuns);
			int averageSwapReq4 = (int) Math.round((double) teams[4]
					.getSwapRequests() / numberOfRuns);

			int averageSwapBid3 = (int) Math.round((double) teams[3]
					.getSwapBids() / numberOfRuns);
			int averageSwapBid4 = (int) Math.round((double) teams[4]
					.getSwapBids() / numberOfRuns);

			int averageSwapAbort3 = (int) Math.round((double) teams[3]
					.getSwapAborts() / numberOfRuns);
			int averageSwapAbort4 = (int) Math.round((double) teams[4]
					.getSwapAborts() / numberOfRuns);

			if (teamScores.length > 1) {
				System.out.println(String.format("%.2f"
						+ "\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d"
						+ "\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d",
						NoHelpRepAgent.WREP, teamScores[0], averageReplan0,
						teamScores[1], averageHelp1, teamScores[2],
						averageHelp2, averageReplan2, teamScores[3],
						averageReplan3, averageSwap3, averageSwapReq3,
						averageSwapBid3, averageSwapAbort3, teamScores[4],
						averageHelp4, averageReplan4, averageSwap4,
						averageSwapReq4, averageSwapBid4, averageSwapAbort4));
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

		System.out
				.println("UnicastCost NoHelpRepOpt RepCount Help2Opt HelpCount Help2RepOpt HelpCount "
						+ "RepCount NoHelpRepOptSwap RepCount SwapCount SwapReq SwapBid SwapAbort "
						+ "Help2RepOptSwap HelpCount RepCount SwapCount SwapReq SwapBid SwapAbort");

		Team.teamSize = 8;

		/* The experiments loop */
		for (int exp3 = 0; exp3 < 11; exp3++) {
			/* Create the teams involved in the simulation */
			Team[] teams = new Team[5];

			teams[0] = new NoHelpRepTeam();
			teams[0].setOptimumAssign(true);

			teams[1] = new AdvActionMAPTeam();
			teams[1].setOptimumAssign(true);
			((AdvActionMAPTeam) teams[1]).setUseHelp2Character(true);

			teams[2] = new AdvActionMAPRepTeam();
			teams[2].setOptimumAssign(true);
			((AdvActionMAPRepTeam) teams[2]).setUseHelp2Character(true);

			teams[3] = new NoHelpRepTeam();
			teams[3].setOptimumAssign(true);
			((NoHelpRepTeam) teams[3]).setUseSwap(true);

			teams[4] = new AdvActionMAPRepTeam();
			teams[4].setOptimumAssign(true);
			((AdvActionMAPRepTeam) teams[4]).setUseHelp2Character(true);
			((AdvActionMAPRepTeam) teams[4]).setUseSwap(true);

			/* Create the SimulationEngine */
			SimulationEngine se = new SimulationEngine(teams);

			/* Set the experiment-wide parameters: */
			/* teams-wide, SimulationEngine, etc params */

			Team.unicastCost = 1 + 2 * exp3;

			Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);
			Agent.calculationCost = 1;
			Agent.planCostCoeff = 0.025;

			TeamTask.helpOverhead = 20;
			TeamTask.swapOverhead = 20;
			TeamTask.cellReward = 100;
			TeamTask.achievementReward = 2000;
			TeamTask.initResCoef = 110;

			NoHelpRepAgent.WREP = -0.25;

			NoHelpRepAgent.swapBidThreshold = 50;
			NoHelpRepAgent.swapRequestThreshold = 100;
			NoHelpRepAgent.swapResourceThreshold = 100;
			NoHelpRepAgent.swapDeliberationThreshold = 0;

			AdvActionMAPAgent.WLL = -0.1;
			AdvActionMAPAgent.requestThreshold = 351;
			AdvActionMAPAgent.lowCostThreshold = 50;
			AdvActionMAPAgent.importanceVersion = 2;

			AdvActionMAPRepAgent.WLL = -0.1;
			AdvActionMAPRepAgent.WREP = -0.25;
			AdvActionMAPRepAgent.requestThreshold = 351;
			AdvActionMAPRepAgent.lowCostThreshold = 50;
			AdvActionMAPRepAgent.importanceVersion = 2;

			AdvActionMAPRepAgent.swapBidThreshold = 50;
			AdvActionMAPRepAgent.swapRequestThreshold = 100;
			AdvActionMAPRepAgent.swapResourceThreshold = 100;
			AdvActionMAPRepAgent.swapDeliberationThreshold = 0;

			/* vary the disturbance: */
			SimulationEngine.disturbanceLevel = 0.1;
			SimulationEngine.pulseOccurrence = new int[] { 4 };
			SimulationEngine.pulseLevel = 0.8;

			/* Initialize and run the experiment */
			se.initializeExperiment(numberOfRuns);
			int[] teamScores = se.runExperiment();

			int averageHelp1 = (int) Math.round((double) (teams[1])
					.getSucOffersCounts() / numberOfRuns);
			int averageHelp2 = (int) Math.round((double) (teams[2])
					.getSucOffersCounts() / numberOfRuns);
			int averageHelp4 = (int) Math.round((double) (teams[4])
					.getSucOffersCounts() / numberOfRuns);

			int averageReplan0 = (int) Math
					.round((double) ((NoHelpRepTeam) teams[0])
							.getReplanCounts() / numberOfRuns);
			int averageReplan2 = (int) Math
					.round((double) ((AdvActionMAPRepTeam) teams[2])
							.getReplanCounts() / numberOfRuns);
			int averageReplan3 = (int) Math
					.round((double) ((NoHelpRepTeam) teams[3])
							.getReplanCounts() / numberOfRuns);
			int averageReplan4 = (int) Math
					.round((double) ((AdvActionMAPRepTeam) teams[4])
							.getReplanCounts() / numberOfRuns);

			int averageSwap3 = (int) Math.round((double) teams[3]
					.getSwapCounts() / numberOfRuns);
			int averageSwap4 = (int) Math.round((double) teams[4]
					.getSwapCounts() / numberOfRuns);

			int averageSwapReq3 = (int) Math.round((double) teams[3]
					.getSwapRequests() / numberOfRuns);
			int averageSwapReq4 = (int) Math.round((double) teams[4]
					.getSwapRequests() / numberOfRuns);

			int averageSwapBid3 = (int) Math.round((double) teams[3]
					.getSwapBids() / numberOfRuns);
			int averageSwapBid4 = (int) Math.round((double) teams[4]
					.getSwapBids() / numberOfRuns);

			int averageSwapAbort3 = (int) Math.round((double) teams[3]
					.getSwapAborts() / numberOfRuns);
			int averageSwapAbort4 = (int) Math.round((double) teams[4]
					.getSwapAborts() / numberOfRuns);

			if (teamScores.length > 1) {
				System.out.println(String.format("%d"
						+ "\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d"
						+ "\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d",
						Team.unicastCost, teamScores[0], averageReplan0,
						teamScores[1], averageHelp1, teamScores[2],
						averageHelp2, averageReplan2, teamScores[3],
						averageReplan3, averageSwap3, averageSwapReq3,
						averageSwapBid3, averageSwapAbort3, teamScores[4],
						averageHelp4, averageReplan4, averageSwap4,
						averageSwapReq4, averageSwapBid4, averageSwapAbort4));
			} else
				System.out.println("Score : 0");
		}
	}

	public static void runSimulation4(int numberOfRuns) throws Exception {

		if (numberOfRuns < 1)
			throw new Exception("numberOfRuns is invalid!");

		SimulationEngine.colorRange = new int[] { 0, 1, 2, 3, 4, 5 };
		SimulationEngine.numOfColors = SimulationEngine.colorRange.length;
		SimulationEngine.actionCostsRange = new int[] { 10, 40, 100, 150, 250,
				300, 350, 500 };

		System.out
				.println("InitResCoef NoHelpRepOpt RepCount Help2Opt HelpCount Help2RepOpt HelpCount "
						+ "RepCount NoHelpRepOptSwap RepCount SwapCount SwapReq SwapBid SwapAbort "
						+ "Help2RepOptSwap HelpCount RepCount SwapCount SwapReq SwapBid SwapAbort");

		Team.teamSize = 8;

		/* The experiments loop */
		for (int exp4 = 0; exp4 < 11; exp4++) {
			/* Create the teams involved in the simulation */
			Team[] teams = new Team[5];

			teams[0] = new NoHelpRepTeam();
			teams[0].setOptimumAssign(true);

			teams[1] = new AdvActionMAPTeam();
			teams[1].setOptimumAssign(true);
			((AdvActionMAPTeam) teams[1]).setUseHelp2Character(true);

			teams[2] = new AdvActionMAPRepTeam();
			teams[2].setOptimumAssign(true);
			((AdvActionMAPRepTeam) teams[2]).setUseHelp2Character(true);

			teams[3] = new NoHelpRepTeam();
			teams[3].setOptimumAssign(true);
			((NoHelpRepTeam) teams[3]).setUseSwap(true);

			teams[4] = new AdvActionMAPRepTeam();
			teams[4].setOptimumAssign(true);
			((AdvActionMAPRepTeam) teams[4]).setUseHelp2Character(true);
			((AdvActionMAPRepTeam) teams[4]).setUseSwap(true);

			/* Create the SimulationEngine */
			SimulationEngine se = new SimulationEngine(teams);

			/* Set the experiment-wide parameters: */
			/* teams-wide, SimulationEngine, etc params */

			Team.unicastCost = 1;
			Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);
			Agent.calculationCost = 1;
			Agent.planCostCoeff = 0.025;

			TeamTask.helpOverhead = 20;
			TeamTask.swapOverhead = 20;
			TeamTask.cellReward = 100;
			TeamTask.achievementReward = 2000;

			TeamTask.initResCoef = 50 + 10 * exp4;

			NoHelpRepAgent.WREP = -0.25;

			NoHelpRepAgent.swapBidThreshold = 50;
			NoHelpRepAgent.swapRequestThreshold = 100;
			NoHelpRepAgent.swapResourceThreshold = 100;
			NoHelpRepAgent.swapDeliberationThreshold = 0;

			AdvActionMAPAgent.WLL = -0.1;
			AdvActionMAPAgent.requestThreshold = 351;
			AdvActionMAPAgent.lowCostThreshold = 50;
			AdvActionMAPAgent.importanceVersion = 2;

			AdvActionMAPRepAgent.WLL = -0.1;
			AdvActionMAPRepAgent.WREP = -0.25;
			AdvActionMAPRepAgent.requestThreshold = 351;
			AdvActionMAPRepAgent.lowCostThreshold = 50;
			AdvActionMAPRepAgent.importanceVersion = 2;

			AdvActionMAPRepAgent.swapBidThreshold = 50;
			AdvActionMAPRepAgent.swapRequestThreshold = 100;
			AdvActionMAPRepAgent.swapResourceThreshold = 100;
			AdvActionMAPRepAgent.swapDeliberationThreshold = 0;

			/* vary the disturbance: */
			SimulationEngine.disturbanceLevel = 0.1;
			SimulationEngine.pulseOccurrence = new int[] { 4 };
			SimulationEngine.pulseLevel = 0.8;

			/* Initialize and run the experiment */
			se.initializeExperiment(numberOfRuns);
			int[] teamScores = se.runExperiment();

			int averageHelp1 = (int) Math.round((double) (teams[1])
					.getSucOffersCounts() / numberOfRuns);
			int averageHelp2 = (int) Math.round((double) (teams[2])
					.getSucOffersCounts() / numberOfRuns);
			int averageHelp4 = (int) Math.round((double) (teams[4])
					.getSucOffersCounts() / numberOfRuns);

			int averageReplan0 = (int) Math
					.round((double) ((NoHelpRepTeam) teams[0])
							.getReplanCounts() / numberOfRuns);
			int averageReplan2 = (int) Math
					.round((double) ((AdvActionMAPRepTeam) teams[2])
							.getReplanCounts() / numberOfRuns);
			int averageReplan3 = (int) Math
					.round((double) ((NoHelpRepTeam) teams[3])
							.getReplanCounts() / numberOfRuns);
			int averageReplan4 = (int) Math
					.round((double) ((AdvActionMAPRepTeam) teams[4])
							.getReplanCounts() / numberOfRuns);

			int averageSwap3 = (int) Math.round((double) teams[3]
					.getSwapCounts() / numberOfRuns);
			int averageSwap4 = (int) Math.round((double) teams[4]
					.getSwapCounts() / numberOfRuns);

			int averageSwapReq3 = (int) Math.round((double) teams[3]
					.getSwapRequests() / numberOfRuns);
			int averageSwapReq4 = (int) Math.round((double) teams[4]
					.getSwapRequests() / numberOfRuns);

			int averageSwapBid3 = (int) Math.round((double) teams[3]
					.getSwapBids() / numberOfRuns);
			int averageSwapBid4 = (int) Math.round((double) teams[4]
					.getSwapBids() / numberOfRuns);

			int averageSwapAbort3 = (int) Math.round((double) teams[3]
					.getSwapAborts() / numberOfRuns);
			int averageSwapAbort4 = (int) Math.round((double) teams[4]
					.getSwapAborts() / numberOfRuns);

			if (teamScores.length > 1) {
				System.out.println(String.format("%d"
						+ "\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d"
						+ "\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d",
						TeamTask.initResCoef, teamScores[0], averageReplan0,
						teamScores[1], averageHelp1, teamScores[2],
						averageHelp2, averageReplan2, teamScores[3],
						averageReplan3, averageSwap3, averageSwapReq3,
						averageSwapBid3, averageSwapAbort3, teamScores[4],
						averageHelp4, averageReplan4, averageSwap4,
						averageSwapReq4, averageSwapBid4, averageSwapAbort4));
			} else
				System.out.println("Score : 0");
		}
	}

	public static void runSimulation5(int numberOfRuns) throws Exception {

		if (numberOfRuns < 1)
			throw new Exception("numberOfRuns is invalid!");

		SimulationEngine.colorRange = new int[] { 0, 1, 2, 3, 4, 5 };
		SimulationEngine.numOfColors = SimulationEngine.colorRange.length;
		SimulationEngine.actionCostsRange = new int[] { 10, 40, 100, 150, 250,
				300, 350, 500 };

		System.out
				.println("SwapReqThresh NoHelpRepOpt RepCount Help2Opt HelpCount Help2RepOpt HelpCount "
						+ "RepCount NoHelpRepOptSwap RepCount SwapCount SwapReq SwapBid SwapAbort "
						+ "Help2RepOptSwap HelpCount RepCount SwapCount SwapReq SwapBid SwapAbort");

		Team.teamSize = 8;

		/* The experiments loop */
		for (int exp5 = 0; exp5 < 10; exp5++) {
			/* Create the teams involved in the simulation */
			Team[] teams = new Team[5];

			teams[0] = new NoHelpRepTeam();
			teams[0].setOptimumAssign(true);

			teams[1] = new AdvActionMAPTeam();
			teams[1].setOptimumAssign(true);
			((AdvActionMAPTeam) teams[1]).setUseHelp2Character(true);

			teams[2] = new AdvActionMAPRepTeam();
			teams[2].setOptimumAssign(true);
			((AdvActionMAPRepTeam) teams[2]).setUseHelp2Character(true);

			teams[3] = new NoHelpRepTeam();
			teams[3].setOptimumAssign(true);
			((NoHelpRepTeam) teams[3]).setUseSwap(true);

			teams[4] = new AdvActionMAPRepTeam();
			teams[4].setOptimumAssign(true);
			((AdvActionMAPRepTeam) teams[4]).setUseHelp2Character(true);
			((AdvActionMAPRepTeam) teams[4]).setUseSwap(true);

			/* Create the SimulationEngine */
			SimulationEngine se = new SimulationEngine(teams);

			/* Set the experiment-wide parameters: */
			/* teams-wide, SimulationEngine, etc params */

			Team.unicastCost = 1;
			Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);
			Agent.calculationCost = 1;
			Agent.planCostCoeff = 0.025;

			TeamTask.helpOverhead = 20;
			TeamTask.swapOverhead = 20;
			TeamTask.cellReward = 100;
			TeamTask.achievementReward = 2000;
			TeamTask.initResCoef = 110;

			NoHelpRepAgent.WREP = -0.25;

			NoHelpRepAgent.swapBidThreshold = 50;

			NoHelpRepAgent.swapRequestThreshold = 50 * exp5;

			NoHelpRepAgent.swapResourceThreshold = 100;
			NoHelpRepAgent.swapDeliberationThreshold = 0;

			AdvActionMAPAgent.WLL = -0.1;
			AdvActionMAPAgent.requestThreshold = 351;
			AdvActionMAPAgent.lowCostThreshold = 50;
			AdvActionMAPAgent.importanceVersion = 2;

			AdvActionMAPRepAgent.WLL = -0.1;
			AdvActionMAPRepAgent.WREP = -0.25;
			AdvActionMAPRepAgent.requestThreshold = 351;
			AdvActionMAPRepAgent.lowCostThreshold = 50;
			AdvActionMAPRepAgent.importanceVersion = 2;

			AdvActionMAPRepAgent.swapBidThreshold = 50;

			AdvActionMAPRepAgent.swapRequestThreshold = 50 * exp5;

			AdvActionMAPRepAgent.swapResourceThreshold = 100;
			AdvActionMAPRepAgent.swapDeliberationThreshold = 0;

			/* vary the disturbance: */
			SimulationEngine.disturbanceLevel = 0.1;
			SimulationEngine.pulseOccurrence = new int[] { 4 };
			SimulationEngine.pulseLevel = 0.8;

			/* Initialize and run the experiment */
			se.initializeExperiment(numberOfRuns);
			int[] teamScores = se.runExperiment();

			int averageHelp1 = (int) Math.round((double) (teams[1])
					.getSucOffersCounts() / numberOfRuns);
			int averageHelp2 = (int) Math.round((double) (teams[2])
					.getSucOffersCounts() / numberOfRuns);
			int averageHelp4 = (int) Math.round((double) (teams[4])
					.getSucOffersCounts() / numberOfRuns);

			int averageReplan0 = (int) Math
					.round((double) ((NoHelpRepTeam) teams[0])
							.getReplanCounts() / numberOfRuns);
			int averageReplan2 = (int) Math
					.round((double) ((AdvActionMAPRepTeam) teams[2])
							.getReplanCounts() / numberOfRuns);
			int averageReplan3 = (int) Math
					.round((double) ((NoHelpRepTeam) teams[3])
							.getReplanCounts() / numberOfRuns);
			int averageReplan4 = (int) Math
					.round((double) ((AdvActionMAPRepTeam) teams[4])
							.getReplanCounts() / numberOfRuns);

			int averageSwap3 = (int) Math.round((double) teams[3]
					.getSwapCounts() / numberOfRuns);
			int averageSwap4 = (int) Math.round((double) teams[4]
					.getSwapCounts() / numberOfRuns);

			int averageSwapReq3 = (int) Math.round((double) teams[3]
					.getSwapRequests() / numberOfRuns);
			int averageSwapReq4 = (int) Math.round((double) teams[4]
					.getSwapRequests() / numberOfRuns);

			int averageSwapBid3 = (int) Math.round((double) teams[3]
					.getSwapBids() / numberOfRuns);
			int averageSwapBid4 = (int) Math.round((double) teams[4]
					.getSwapBids() / numberOfRuns);

			int averageSwapAbort3 = (int) Math.round((double) teams[3]
					.getSwapAborts() / numberOfRuns);
			int averageSwapAbort4 = (int) Math.round((double) teams[4]
					.getSwapAborts() / numberOfRuns);

			if (teamScores.length > 1) {
				System.out.println(String.format("%d"
						+ "\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d"
						+ "\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d",
						NoHelpRepAgent.swapRequestThreshold, teamScores[0],
						averageReplan0, teamScores[1], averageHelp1,
						teamScores[2], averageHelp2, averageReplan2,
						teamScores[3], averageReplan3, averageSwap3,
						averageSwapReq3, averageSwapBid3, averageSwapAbort3,
						teamScores[4], averageHelp4, averageReplan4,
						averageSwap4, averageSwapReq4, averageSwapBid4,
						averageSwapAbort4));
			} else
				System.out.println("Score : 0");
		}
	}
}
