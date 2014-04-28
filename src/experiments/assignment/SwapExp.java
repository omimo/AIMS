package experiments.assignment;

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
 * Experiment for Swapping
 * 
 * @author Mojtaba
 * 
 */
public class SwapExp {

	public static void main(String[] args) {
		try {
			if(args.length > 1) {
				if(Integer.parseInt(args[1]) == 1)
					runSimulation1(Integer.parseInt(args[0]));
				else if(Integer.parseInt(args[1]) == 2)
					runSimulation2(Integer.parseInt(args[0]));
				else if(Integer.parseInt(args[1]) == 3)
					runSimulation3(Integer.parseInt(args[0]));
				else if(Integer.parseInt(args[1]) == 4)
					runSimulation4(Integer.parseInt(args[0]));
				else if(Integer.parseInt(args[1]) == 5)
					runSimulation5(Integer.parseInt(args[0]));
			}
			runSimulation1(100);
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
				.println("Disturbance Help2Opt HelpCount Help2RepOpt HelpCount RepCount NoHelpRepOptSwap RepCount SwapCount SwapReq SwapBid SwapAbort "
						+ "Help2RepOptSwap HelpCount RepCount SwapCount SwapReq SwapBid SwapAbort NoHelpRepOpt RepCount SwapCount SwapReq SwapBid SwapAbort");
		
		Team.teamSize = 8;
		
		/* The experiments loop */
		for (int exp1 = 0; exp1 < 11; exp1++) {
			/* Create the teams involved in the simulation */		
			Team[] teams = new Team[7];
			
			teams[0] = new AdvActionMAPTeam();
			teams[0].setOptimumAssign(true);
			((AdvActionMAPTeam)teams[0]).setUseHelp2Character(true);
			
			teams[1] = new AdvActionMAPRepTeam();
			teams[1].setOptimumAssign(true);
			((AdvActionMAPRepTeam)teams[1]).setUseHelp2Character(true);
			
			teams[2] = new NoHelpRepTeam();
			teams[2].setOptimumAssign(true);
			((NoHelpRepTeam)teams[2]).setUseSwap(true);
			
			teams[3] = new AdvActionMAPRepTeam();
			teams[3].setOptimumAssign(true);
			((AdvActionMAPRepTeam)teams[3]).setUseHelp2Character(true);
			((AdvActionMAPRepTeam)teams[3]).setUseSwap(true);
			
			teams[4] = new NoHelpRepTeam();
			teams[4].setOptimumAssign(true);
			
			teams[5] = new NoHelpRepTeam();
			((NoHelpRepTeam)teams[2]).setUseSwap(true);
			
			teams[6] = new AdvActionMAPRepTeam();
			((AdvActionMAPRepTeam)teams[6]).setUseHelp2Character(true);
			((AdvActionMAPRepTeam)teams[6]).setUseSwap(true);
			
			
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
			
			int averageHelp0 = (int)Math.round((double)(teams[0]).getSucOffersCounts()/numberOfRuns);
			int averageHelp1 = (int)Math.round((double)(teams[1]).getSucOffersCounts()/numberOfRuns);
			int averageHelp3 = (int)Math.round((double)(teams[3]).getSucOffersCounts()/numberOfRuns);
			int averageHelp6 = (int)Math.round((double)(teams[6]).getSucOffersCounts()/numberOfRuns);
			
			int averageReplan1 = (int)Math.round((double)((AdvActionMAPRepTeam)teams[1]).getReplanCounts()/numberOfRuns);
			int averageReplan2 = (int)Math.round((double)((NoHelpRepTeam)teams[2]).getReplanCounts()/numberOfRuns);
			int averageReplan3 = (int)Math.round((double)((AdvActionMAPRepTeam)teams[3]).getReplanCounts()/numberOfRuns);
			int averageReplan4 = (int)Math.round((double)((NoHelpRepTeam)teams[4]).getReplanCounts()/numberOfRuns);
			int averageReplan5 = (int)Math.round((double)((NoHelpRepTeam)teams[5]).getReplanCounts()/numberOfRuns);
			int averageReplan6 = (int)Math.round((double)((AdvActionMAPRepTeam)teams[6]).getReplanCounts()/numberOfRuns);
			
			int averageSwap2 = (int)Math.round((double)teams[2].getSwapCounts()/numberOfRuns);
			int averageSwap3 = (int)Math.round((double)teams[3].getSwapCounts()/numberOfRuns);
			int averageSwap5 = (int)Math.round((double)teams[5].getSwapCounts()/numberOfRuns);
			int averageSwap6 = (int)Math.round((double)teams[6].getSwapCounts()/numberOfRuns);
			
			int averageSwapReq2 = (int)Math.round((double)teams[2].getSwapRequests()/numberOfRuns);
			int averageSwapReq3 = (int)Math.round((double)teams[3].getSwapRequests()/numberOfRuns);
			int averageSwapReq5 = (int)Math.round((double)teams[5].getSwapRequests()/numberOfRuns);
			int averageSwapReq6 = (int)Math.round((double)teams[6].getSwapRequests()/numberOfRuns);
			
			int averageSwapBid2 = (int)Math.round((double)teams[2].getSwapBids()/numberOfRuns);
			int averageSwapBid3 = (int)Math.round((double)teams[3].getSwapBids()/numberOfRuns);
			int averageSwapBid5 = (int)Math.round((double)teams[5].getSwapBids()/numberOfRuns);
			int averageSwapBid6 = (int)Math.round((double)teams[6].getSwapBids()/numberOfRuns);
			
			int averageSwapAbort2 = (int)Math.round((double)teams[2].getSwapAborts()/numberOfRuns);
			int averageSwapAbort3 = (int)Math.round((double)teams[3].getSwapAborts()/numberOfRuns);
			int averageSwapAbort5 = (int)Math.round((double)teams[5].getSwapAborts()/numberOfRuns);
			int averageSwapAbort6 = (int)Math.round((double)teams[6].getSwapAborts()/numberOfRuns);
			
			if (teamScores.length > 1) {
				System.out.println(String.format("%.2f" +
						"\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d" +
						"\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d",
						SimulationEngine.disturbanceLevel, 
						teamScores[0], averageHelp0, teamScores[1], averageHelp1, averageReplan1, teamScores[2], 
						averageReplan2, averageSwap2, averageSwapReq2, averageSwapBid2, averageSwapAbort2, teamScores[3], averageHelp3, averageReplan3, averageSwap3, averageSwapReq3, averageSwapBid3, averageSwapAbort3,
						teamScores[4], averageReplan4, teamScores[5], averageReplan5, averageSwap5, averageSwapReq5, averageSwapBid5, averageSwapAbort5,
						teamScores[6], averageHelp6,  averageReplan6, averageSwap6, averageSwapReq6, averageSwapBid6, averageSwapAbort6));
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

		System.out
				.println("WREP Help2Opt Help2RepOpt RepCount NoHelpRepOptSwap RepCount SwapCount Help2RepOptSwap RepCount SwapCount");
		
		Team.teamSize = 8;
		
		/* The experiments loop */
		for (int exp2 = 0; exp2 < 8; exp2++) {
			/* Create the teams involved in the simulation */		
			Team[] teams = new Team[7];
			
			teams[0] = new AdvActionMAPTeam();
			teams[0].setOptimumAssign(true);
			((AdvActionMAPTeam)teams[0]).setUseHelp2Character(true);
			
			teams[1] = new AdvActionMAPRepTeam();
			teams[1].setOptimumAssign(true);
			((AdvActionMAPRepTeam)teams[1]).setUseHelp2Character(true);
			
			teams[2] = new NoHelpRepTeam();
			teams[2].setOptimumAssign(true);
			((NoHelpRepTeam)teams[2]).setUseSwap(true);
			
			teams[3] = new AdvActionMAPRepTeam();
			teams[3].setOptimumAssign(true);
			((AdvActionMAPRepTeam)teams[3]).setUseHelp2Character(true);
			((AdvActionMAPRepTeam)teams[3]).setUseSwap(true);
			
			teams[4] = new NoHelpRepTeam();
			teams[4].setOptimumAssign(true);
			
			teams[5] = new NoHelpRepTeam();
			((NoHelpRepTeam)teams[2]).setUseSwap(true);
			
			teams[6] = new AdvActionMAPRepTeam();
			((AdvActionMAPRepTeam)teams[6]).setUseHelp2Character(true);
			((AdvActionMAPRepTeam)teams[6]).setUseSwap(true);
			
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
			SimulationEngine.disturbanceLevel = 0.2;
			SimulationEngine.pulseOccurrence = new int[] { 4 };
			SimulationEngine.pulseLevel = 0.8;

			/* Initialize and run the experiment */
			se.initializeExperiment(numberOfRuns);
			int[] teamScores = se.runExperiment();
			
			int averageHelp0 = (int)Math.round((double)(teams[0]).getSucOffersCounts()/numberOfRuns);
			int averageHelp1 = (int)Math.round((double)(teams[1]).getSucOffersCounts()/numberOfRuns);
			int averageHelp3 = (int)Math.round((double)(teams[3]).getSucOffersCounts()/numberOfRuns);
			int averageHelp6 = (int)Math.round((double)(teams[6]).getSucOffersCounts()/numberOfRuns);
			
			int averageReplan1 = (int)Math.round((double)((AdvActionMAPRepTeam)teams[1]).getReplanCounts()/numberOfRuns);
			int averageReplan2 = (int)Math.round((double)((NoHelpRepTeam)teams[2]).getReplanCounts()/numberOfRuns);
			int averageReplan3 = (int)Math.round((double)((AdvActionMAPRepTeam)teams[3]).getReplanCounts()/numberOfRuns);
			int averageReplan4 = (int)Math.round((double)((NoHelpRepTeam)teams[4]).getReplanCounts()/numberOfRuns);
			int averageReplan5 = (int)Math.round((double)((NoHelpRepTeam)teams[5]).getReplanCounts()/numberOfRuns);
			int averageReplan6 = (int)Math.round((double)((AdvActionMAPRepTeam)teams[6]).getReplanCounts()/numberOfRuns);
			
			int averageSwap2 = (int)Math.round((double)(teams[2]).getSwapCounts()/numberOfRuns);
			int averageSwap3 = (int)Math.round((double)(teams[3]).getSwapCounts()/numberOfRuns);
			int averageSwap5 = (int)Math.round((double)(teams[5]).getSwapCounts()/numberOfRuns);
			int averageSwap6 = (int)Math.round((double)(teams[6]).getSwapCounts()/numberOfRuns);
			
			if (teamScores.length > 1) {
				System.out.println(String.format("%.2f" +
						"\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d",
						NoHelpRepAgent.WREP, 
						teamScores[0], averageHelp0, teamScores[1], averageHelp1, averageReplan1, teamScores[2], 
						averageReplan2, averageSwap2, teamScores[3], averageHelp3, averageReplan3, averageSwap3,
						teamScores[4], averageReplan4, teamScores[5], averageReplan5, averageSwap5,
						teamScores[6], averageHelp6,  averageReplan6, averageSwap6));
			} else
				System.out.println("Score : 0");
		}
	}
		
		
		public static void runSimulation3(int numberOfRuns) throws Exception {

			if (numberOfRuns < 1)
				throw new Exception("numberOfRuns is invalid!");

			SimulationEngine.colorRange = new int[] { 0, 1, 2, 3, 4, 5 };
			SimulationEngine.numOfColors = SimulationEngine.colorRange.length;
			SimulationEngine.actionCostsRange = new int[] {10, 40, 100, 150, 250, 300, 350, 500};

			System.out
					.println("UnicastCost Help2Opt Help2RepOpt RepCount NoHelpRepOptSwap RepCount SwapCount Help2RepOptSwap RepCount SwapCount");
			
			Team.teamSize = 8;
			
			/* The experiments loop */
			for (int exp3 = 0; exp3 < 11; exp3++) {
				/* Create the teams involved in the simulation */		
				Team[] teams = new Team[7];
				
				teams[0] = new AdvActionMAPTeam();
				teams[0].setOptimumAssign(true);
				((AdvActionMAPTeam)teams[0]).setUseHelp2Character(true);
				
				teams[1] = new AdvActionMAPRepTeam();
				teams[1].setOptimumAssign(true);
				((AdvActionMAPRepTeam)teams[1]).setUseHelp2Character(true);
				
				teams[2] = new NoHelpRepTeam();
				teams[2].setOptimumAssign(true);
				((NoHelpRepTeam)teams[2]).setUseSwap(true);
				
				teams[3] = new AdvActionMAPRepTeam();
				teams[3].setOptimumAssign(true);
				((AdvActionMAPRepTeam)teams[3]).setUseHelp2Character(true);
				((AdvActionMAPRepTeam)teams[3]).setUseSwap(true);
				
				teams[4] = new NoHelpRepTeam();
				teams[4].setOptimumAssign(true);
				
				teams[5] = new NoHelpRepTeam();
				((NoHelpRepTeam)teams[2]).setUseSwap(true);
				
				teams[6] = new AdvActionMAPRepTeam();
				((AdvActionMAPRepTeam)teams[6]).setUseHelp2Character(true);
				((AdvActionMAPRepTeam)teams[6]).setUseSwap(true);
				
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
				SimulationEngine.disturbanceLevel = 0.2;
				SimulationEngine.pulseOccurrence = new int[] { 4 };
				SimulationEngine.pulseLevel = 0.8;

				/* Initialize and run the experiment */
				se.initializeExperiment(numberOfRuns);
				int[] teamScores = se.runExperiment();
				
				int averageHelp0 = (int)Math.round((double)(teams[0]).getSucOffersCounts()/numberOfRuns);
				int averageHelp1 = (int)Math.round((double)(teams[1]).getSucOffersCounts()/numberOfRuns);
				int averageHelp3 = (int)Math.round((double)(teams[3]).getSucOffersCounts()/numberOfRuns);
				int averageHelp6 = (int)Math.round((double)(teams[6]).getSucOffersCounts()/numberOfRuns);
				
				int averageReplan1 = (int)Math.round((double)((AdvActionMAPRepTeam)teams[1]).getReplanCounts()/numberOfRuns);
				int averageReplan2 = (int)Math.round((double)((NoHelpRepTeam)teams[2]).getReplanCounts()/numberOfRuns);
				int averageReplan3 = (int)Math.round((double)((AdvActionMAPRepTeam)teams[3]).getReplanCounts()/numberOfRuns);
				int averageReplan4 = (int)Math.round((double)((NoHelpRepTeam)teams[4]).getReplanCounts()/numberOfRuns);
				int averageReplan5 = (int)Math.round((double)((NoHelpRepTeam)teams[5]).getReplanCounts()/numberOfRuns);
				int averageReplan6 = (int)Math.round((double)((AdvActionMAPRepTeam)teams[6]).getReplanCounts()/numberOfRuns);
				
				int averageSwap2 = (int)Math.round((double)(teams[2]).getSwapCounts()/numberOfRuns);
				int averageSwap3 = (int)Math.round((double)(teams[3]).getSwapCounts()/numberOfRuns);
				int averageSwap5 = (int)Math.round((double)(teams[5]).getSwapCounts()/numberOfRuns);
				int averageSwap6 = (int)Math.round((double)(teams[6]).getSwapCounts()/numberOfRuns);
				
				if (teamScores.length > 1) {
					System.out.println(String.format("%d" +
							"\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d",
							Team.unicastCost, 
							teamScores[0], averageHelp0, teamScores[1], averageHelp1, averageReplan1, teamScores[2], 
							averageReplan2, averageSwap2, teamScores[3], averageHelp3, averageReplan3, averageSwap3,
							teamScores[4], averageReplan4, teamScores[5], averageReplan5, averageSwap5,
							teamScores[6], averageHelp6, averageReplan6, averageSwap6));
				} else
					System.out.println("Score : 0");
			}
		}

		public static void runSimulation4(int numberOfRuns) throws Exception {

			if (numberOfRuns < 1)
				throw new Exception("numberOfRuns is invalid!");

			SimulationEngine.colorRange = new int[] { 0, 1, 2, 3, 4, 5 };
			SimulationEngine.numOfColors = SimulationEngine.colorRange.length;
			SimulationEngine.actionCostsRange = new int[] {10, 40, 100, 150, 250, 300, 350, 500};

			System.out
					.println("InitResource Help2Opt Help2RepOpt RepCount NoHelpRepOptSwap RepCount SwapCount Help2RepOptSwap RepCount SwapCount");
			
			Team.teamSize = 8;
			
			/* The experiments loop */
			for (int exp4 = 0; exp4 < 11; exp4++) {
				/* Create the teams involved in the simulation */		
				Team[] teams = new Team[7];
				
				teams[0] = new AdvActionMAPTeam();
				teams[0].setOptimumAssign(true);
				((AdvActionMAPTeam)teams[0]).setUseHelp2Character(true);
				
				teams[1] = new AdvActionMAPRepTeam();
				teams[1].setOptimumAssign(true);
				((AdvActionMAPRepTeam)teams[1]).setUseHelp2Character(true);
				
				teams[2] = new NoHelpRepTeam();
				teams[2].setOptimumAssign(true);
				((NoHelpRepTeam)teams[2]).setUseSwap(true);
				
				teams[3] = new AdvActionMAPRepTeam();
				teams[3].setOptimumAssign(true);
				((AdvActionMAPRepTeam)teams[3]).setUseHelp2Character(true);
				((AdvActionMAPRepTeam)teams[3]).setUseSwap(true);
				
				teams[4] = new NoHelpRepTeam();
				teams[4].setOptimumAssign(true);
				
				teams[5] = new NoHelpRepTeam();
				((NoHelpRepTeam)teams[2]).setUseSwap(true);
				
				teams[6] = new AdvActionMAPRepTeam();
				((AdvActionMAPRepTeam)teams[6]).setUseHelp2Character(true);
				((AdvActionMAPRepTeam)teams[6]).setUseSwap(true);
				
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
				SimulationEngine.disturbanceLevel = 0.2;
				SimulationEngine.pulseOccurrence = new int[] { 4 };
				SimulationEngine.pulseLevel = 0.8;

				/* Initialize and run the experiment */
				se.initializeExperiment(numberOfRuns);
				int[] teamScores = se.runExperiment();
				
				int averageHelp0 = (int)Math.round((double)(teams[0]).getSucOffersCounts()/numberOfRuns);
				int averageHelp1 = (int)Math.round((double)(teams[1]).getSucOffersCounts()/numberOfRuns);
				int averageHelp3 = (int)Math.round((double)(teams[3]).getSucOffersCounts()/numberOfRuns);
				int averageHelp6 = (int)Math.round((double)(teams[6]).getSucOffersCounts()/numberOfRuns);
				
				int averageReplan1 = (int)Math.round((double)((AdvActionMAPRepTeam)teams[1]).getReplanCounts()/numberOfRuns);
				int averageReplan2 = (int)Math.round((double)((NoHelpRepTeam)teams[2]).getReplanCounts()/numberOfRuns);
				int averageReplan3 = (int)Math.round((double)((AdvActionMAPRepTeam)teams[3]).getReplanCounts()/numberOfRuns);
				int averageReplan4 = (int)Math.round((double)((NoHelpRepTeam)teams[4]).getReplanCounts()/numberOfRuns);
				int averageReplan5 = (int)Math.round((double)((NoHelpRepTeam)teams[5]).getReplanCounts()/numberOfRuns);
				int averageReplan6 = (int)Math.round((double)((AdvActionMAPRepTeam)teams[6]).getReplanCounts()/numberOfRuns);
				
				int averageSwap2 = (int)Math.round((double)(teams[2]).getSwapCounts()/numberOfRuns);
				int averageSwap3 = (int)Math.round((double)(teams[3]).getSwapCounts()/numberOfRuns);
				int averageSwap5 = (int)Math.round((double)(teams[5]).getSwapCounts()/numberOfRuns);
				int averageSwap6 = (int)Math.round((double)(teams[6]).getSwapCounts()/numberOfRuns);
				
				if (teamScores.length > 1) {
					System.out.println(String.format("%d" +
							"\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d",
							TeamTask.initResCoef, 
							teamScores[0], averageHelp0, teamScores[1], averageHelp1, averageReplan1, teamScores[2], 
							averageReplan2, averageSwap2, teamScores[3], averageHelp3, averageReplan3, averageSwap3,
							teamScores[4], averageReplan4, teamScores[5], averageReplan5, averageSwap5,
							teamScores[6], averageHelp6, averageReplan6, averageSwap6));
				} else
					System.out.println("Score : 0");
			}
		}

		public static void runSimulation5(int numberOfRuns) throws Exception {

			if (numberOfRuns < 1)
				throw new Exception("numberOfRuns is invalid!");

			SimulationEngine.colorRange = new int[] { 0, 1, 2, 3, 4, 5 };
			SimulationEngine.numOfColors = SimulationEngine.colorRange.length;
			SimulationEngine.actionCostsRange = new int[] {10, 40, 100, 150, 250, 300, 350, 500};

			System.out
					.println("SwapRequestThreshold Help2Opt Help2RepOpt RepCount NoHelpRepOptSwap RepCount SwapCount Help2RepOptSwap RepCount SwapCount");
			
			Team.teamSize = 8;
			
			/* The experiments loop */
			for (int exp5 = 0; exp5 < 16; exp5++) {
				/* Create the teams involved in the simulation */		
				Team[] teams = new Team[7];
				
				teams[0] = new AdvActionMAPTeam();
				teams[0].setOptimumAssign(true);
				((AdvActionMAPTeam)teams[0]).setUseHelp2Character(true);
				
				teams[1] = new AdvActionMAPRepTeam();
				teams[1].setOptimumAssign(true);
				((AdvActionMAPRepTeam)teams[1]).setUseHelp2Character(true);
				
				teams[2] = new NoHelpRepTeam();
				teams[2].setOptimumAssign(true);
				((NoHelpRepTeam)teams[2]).setUseSwap(true);
				
				teams[3] = new AdvActionMAPRepTeam();
				teams[3].setOptimumAssign(true);
				((AdvActionMAPRepTeam)teams[3]).setUseHelp2Character(true);
				((AdvActionMAPRepTeam)teams[3]).setUseSwap(true);
				
				teams[4] = new NoHelpRepTeam();
				teams[4].setOptimumAssign(true);
				
				teams[5] = new NoHelpRepTeam();
				((NoHelpRepTeam)teams[2]).setUseSwap(true);
				
				teams[6] = new AdvActionMAPRepTeam();
				((AdvActionMAPRepTeam)teams[6]).setUseHelp2Character(true);
				((AdvActionMAPRepTeam)teams[6]).setUseSwap(true);
				
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
				
				NoHelpRepAgent.swapRequestThreshold = 30 * exp5;
				
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
				
				AdvActionMAPRepAgent.swapRequestThreshold = 30 * exp5;
				
				AdvActionMAPRepAgent.swapResourceThreshold = 100;
				AdvActionMAPRepAgent.swapDeliberationThreshold = 0;

				/* vary the disturbance: */
				SimulationEngine.disturbanceLevel = 0.2;
				SimulationEngine.pulseOccurrence = new int[] { 4 };
				SimulationEngine.pulseLevel = 0.8;

				/* Initialize and run the experiment */
				se.initializeExperiment(numberOfRuns);
				int[] teamScores = se.runExperiment();
				
				int averageHelp0 = (int)Math.round((double)(teams[0]).getSucOffersCounts()/numberOfRuns);
				int averageHelp1 = (int)Math.round((double)(teams[1]).getSucOffersCounts()/numberOfRuns);
				int averageHelp3 = (int)Math.round((double)(teams[3]).getSucOffersCounts()/numberOfRuns);
				int averageHelp6 = (int)Math.round((double)(teams[6]).getSucOffersCounts()/numberOfRuns);
				
				int averageReplan1 = (int)Math.round((double)((AdvActionMAPRepTeam)teams[1]).getReplanCounts()/numberOfRuns);
				int averageReplan2 = (int)Math.round((double)((NoHelpRepTeam)teams[2]).getReplanCounts()/numberOfRuns);
				int averageReplan3 = (int)Math.round((double)((AdvActionMAPRepTeam)teams[3]).getReplanCounts()/numberOfRuns);
				int averageReplan4 = (int)Math.round((double)((NoHelpRepTeam)teams[4]).getReplanCounts()/numberOfRuns);
				int averageReplan5 = (int)Math.round((double)((NoHelpRepTeam)teams[5]).getReplanCounts()/numberOfRuns);
				int averageReplan6 = (int)Math.round((double)((AdvActionMAPRepTeam)teams[6]).getReplanCounts()/numberOfRuns);
				
				int averageSwap2 = (int)Math.round((double)(teams[2]).getSwapCounts()/numberOfRuns);
				int averageSwap3 = (int)Math.round((double)(teams[3]).getSwapCounts()/numberOfRuns);
				int averageSwap5 = (int)Math.round((double)(teams[5]).getSwapCounts()/numberOfRuns);
				int averageSwap6 = (int)Math.round((double)(teams[6]).getSwapCounts()/numberOfRuns);
				
				if (teamScores.length > 1) {
					System.out.println(String.format("%d" +
							"\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d",
							NoHelpRepAgent.swapRequestThreshold, 
							teamScores[0], averageHelp0, teamScores[1], averageHelp1, averageReplan1, teamScores[2], 
							averageReplan2, averageSwap2, teamScores[3], averageHelp3, averageReplan3, averageSwap3,
							teamScores[4], averageReplan4, teamScores[5], averageReplan5, averageSwap5,
							teamScores[6], averageHelp6, averageReplan6, averageSwap6));
				} else
					System.out.println("Score : 0");
			}
		}
}
