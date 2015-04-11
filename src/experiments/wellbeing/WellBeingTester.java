package experiments.wellbeing;

import java.io.*;
import java.util.Date;

import massim.Agent;
import massim.SimulationEngine;
import massim.Team;
import massim.TeamTask;
import massim.agents.advancedactionmap.AdvActionMAPAgent;
import massim.agents.advancedactionmap.AdvActionMAPRepAgent;
import massim.agents.advancedactionmap.AdvActionMAPTeam;
import massim.agents.basicactionmap.BasicActionMAPAgent;
import massim.agents.basicactionmap.BasicActionMAPTeam;
import massim.agents.helperinitactionmap.HelperInitActionMAPAgent;
import massim.agents.helperinitactionmap.HelperInitActionMAPTeam;
import massim.agents.nohelp.NoHelpTeam;
import massim.agents.reassignment.RAAgent;


/**
 * This is an experiment for testing various MAPs
 * over Init Resource Coeff, Disturbance, No of Runs, Unicast cost & other parameters
 * 
 *   
 * @author Denish M
 *
 */
public class WellBeingTester {

	static String strMsg = "";
	public static void main(String[] args) {
			try {
				//runSimulation1(10000, 2);
				//Check no. of arguments
				if(args.length < 2 || Integer.parseInt(args[0]) < 1)
				{
					System.out.println("Insufficient arguments or first argument is invalid. Please pass value between 1 and 4.");
					return;
				}
				//run specific experiment based on input
				if(Integer.parseInt(args[0]) == 1)
				{
					runSimulation1(Integer.parseInt(args[1]), 2);
				}
				else if(Integer.parseInt(args[0]) == 2)
				{
					runSimulation2(Integer.parseInt(args[1]), 2);
				}
				else if(Integer.parseInt(args[0]) == 3)
				{
					runSimulation3(Integer.parseInt(args[1]), 2);
				}
				else if(Integer.parseInt(args[0]) == 4)
				{
					runSimulation4(Integer.parseInt(args[1]), 2);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	/*
	 * Custom simulation number one
	 */
	public static void runSimulation1(int numberOfRuns, int impVersion) throws Exception {
		if(numberOfRuns < 1)
			throw new Exception("numberOfRuns is invalid!");
		
		SimulationEngine.colorRange = 
			new int[] {0, 1, 2, 3, 4, 5};
		SimulationEngine.numOfColors =  
			SimulationEngine.colorRange.length;
		SimulationEngine.actionCostsRange = 
		new int[] {10, 40, 150, 200, 250, 300, 350, 500};	
	
		/* Create the teams involved in the simulation */
		Team.teamSize = 8;
		Team[] teams = new Team[8];
		teams[0] = new AdvActionMAPTeam();
		teams[1] = new HelperInitActionMAPTeam();
		teams[2] = new BasicActionMAPTeam();
		teams[3] = new NoHelpTeam();

		teams[4] = new AdvActionMAPTeam();
		teams[5] = new HelperInitActionMAPTeam();
		teams[6] = new BasicActionMAPTeam();
		teams[7] = new NoHelpTeam();
		
		//set if calculate remaining resources in rewards
		teams[4].remainingResInRewards = teams[5].remainingResInRewards 
				= teams[6].remainingResInRewards = teams[7].remainingResInRewards = true;
				
		/* Create the SimulationEngine */
		SimulationEngine se = new SimulationEngine(teams);
		logText("IMPFACT,INITRES,AD-ACTION-MAP,HELPER-INIT,BASIC-ACTION,NO-HELP,AD-ACTION-MAP-RR,HELPER-INIT-RR,BASIC-ACTION-RR,NO-HELP-RR",1);
		
		//loop different values of variables
		for (int expx=1;expx<21;expx++)
		{  
			for (int expy=10;expy<21;expy++)
			{	
				Team.unicastCost = 1;
				Agent.calculationCost = 1;			
				SimulationEngine.disturbanceLevel = 0.2;
				TeamTask.initResCoef = expy * 10;
				AdvActionMAPAgent.impFactor = HelperInitActionMAPAgent.impFactor = expx * 5;
				AdvActionMAPAgent.WLL = -0.1;
				HelperInitActionMAPAgent.WHH = 0.1;
				Team.paramValues = AdvActionMAPAgent.impFactor + "," + TeamTask.initResCoef;
				Team.dbgScores = false;
				AdvActionMAPAgent.useTeamWellbeing = HelperInitActionMAPAgent.useTeamWellbeing = false;

				Team.broadcastCost = Team.unicastCost * (Team.teamSize-1);
				TeamTask.helpOverhead = 20;			
				TeamTask.cellReward = 100;
				TeamTask.achievementReward = 2000;
				TeamTask.assignmentOverhead = 0;
				
				AdvActionMAPAgent.requestThreshold = 489;
				AdvActionMAPAgent.lowCostThreshold = 50;
				AdvActionMAPAgent.importanceVersion = HelperInitActionMAPAgent.importanceVersion = impVersion;
				
				HelperInitActionMAPAgent.offerThreshold = 299;
				HelperInitActionMAPAgent.EPSILON = 0.1;
				
				BasicActionMAPAgent.requestThreshold = 489;
				
				RAAgent.EPSILON = 0.2;
				RAAgent.WREASSIGN = 0.3;
				RAAgent.WREASSIGNREQ = 1.0;

				/* Initialize and run the experiment */
				se.initializeExperiment(numberOfRuns);			
				int[] teamScores = se.runExperiment();

				/* Print the results */
				strMsg =  Team.paramValues;
				for (int i=0;i<teams.length;i++)
					strMsg += "," + teamScores[i];
				
				logText(strMsg,1);
			}
		}		
		//logText("-----------------------------------------",1);
	}
	
	/*
	 * Custom simulation number two
	 */
	public static void runSimulation2(int initResCoeff, int impVersion) throws Exception {
		int numberOfRuns = 35;
		if(numberOfRuns < 1)
			throw new Exception("numberOfRuns is invalid!");
		
		SimulationEngine.colorRange = 
			new int[] {0, 1, 2, 3, 4, 5};
		SimulationEngine.numOfColors =  
			SimulationEngine.colorRange.length;
		SimulationEngine.actionCostsRange = 
		new int[] {10, 40, 150, 200, 250, 300, 350, 500};	
	
		/* Create the teams involved in the simulation */
		Team.teamSize = 8;
		Team[] teams = new Team[8];
		teams[0] = new AdvActionMAPTeam();
		teams[1] = new HelperInitActionMAPTeam();
		teams[2] = new BasicActionMAPTeam();
		teams[3] = new NoHelpTeam();
		
		teams[4] = new AdvActionMAPTeam();
		teams[5] = new HelperInitActionMAPTeam();
		teams[6] = new BasicActionMAPTeam();
		teams[7] = new NoHelpTeam();
		teams[4].remainingResInRewards = teams[5].remainingResInRewards 
				= teams[6].remainingResInRewards = teams[7].remainingResInRewards = true;

		
		/* Create the SimulationEngine */
		SimulationEngine se = new SimulationEngine(teams);
		logText("UNICAST,INITRES,AD-ACTION-MAP,HELPER-INIT,BASIC-ACTION,NO-HELP,AD-ACTION-MAP-RES,HELPER-INIT-RES,BASIC-ACTION-RES,NO-HELP-RES",initResCoeff);
		for (int expx=0;expx<11;expx++)
		{  
			for (int expy=10;expy< 11;expy++)
			{	
				Team.unicastCost = 2 * expx +1;
				Agent.calculationCost = 1;			
				SimulationEngine.disturbanceLevel = 0.2;
				TeamTask.initResCoef = initResCoeff;//expy * 10;
				AdvActionMAPAgent.impFactor = HelperInitActionMAPAgent.impFactor = 6;
				AdvActionMAPAgent.WLL = -0.1;
				HelperInitActionMAPAgent.WHH = 0.1;
				Team.paramValues = Team.unicastCost + "";
				Team.dbgScores = true;
				AdvActionMAPAgent.useTeamWellbeing = HelperInitActionMAPAgent.useTeamWellbeing = false;
				
				Team.broadcastCost = Team.unicastCost * (Team.teamSize-1);
				TeamTask.helpOverhead = 20;			
				TeamTask.cellReward = 100;
				TeamTask.achievementReward = 2000;
				TeamTask.assignmentOverhead = 0;
				
				AdvActionMAPAgent.requestThreshold = 489;
				AdvActionMAPAgent.lowCostThreshold = 50;
				AdvActionMAPAgent.importanceVersion = HelperInitActionMAPAgent.importanceVersion = impVersion;
				
				HelperInitActionMAPAgent.offerThreshold = 299;
				HelperInitActionMAPAgent.EPSILON = 0.1;
				
				BasicActionMAPAgent.requestThreshold = 489;
				
				RAAgent.EPSILON = 0.2;
				RAAgent.WREASSIGN = 0.3;
				RAAgent.WREASSIGNREQ = 1.0;

				/* Initialize and run the experiment */
				se.initializeExperiment(numberOfRuns);			
				int[] teamScores = se.runExperiment();


				/* Print the results */
				strMsg = Team.paramValues;
				for (int i=0;i<teams.length;i++)
					strMsg += "," + teamScores[i];
				
				logText(strMsg,initResCoeff);
			}
		}
		
		//logText("-----------------------------------------",initResCoeff);
	}
	
	/*
	 * Custom simulation number three
	 */
	public static void runSimulation3(int numberOfRuns, int impVersion) throws Exception {
		if(numberOfRuns < 1)
			throw new Exception("numberOfRuns is invalid!");
		
		SimulationEngine.colorRange = 
			new int[] {0, 1, 2, 3, 4, 5};
		SimulationEngine.numOfColors =  
			SimulationEngine.colorRange.length;
		SimulationEngine.actionCostsRange = 
		new int[] {10, 40, 150, 200, 250, 300, 350, 500};	
	
		/* Create the teams involved in the simulation */
		Team.teamSize = 8;
		Team[] teams = new Team[4];
		teams[0] = new AdvActionMAPTeam();
		teams[1] = new HelperInitActionMAPTeam();
		teams[2] = new BasicActionMAPTeam();
		teams[3] = new NoHelpTeam();
		
		/* Create the SimulationEngine */
		SimulationEngine se = new SimulationEngine(teams);
		logText("WHH,AD-ACTION-MAP,HELPER-INIT,BASIC-ACTION,NO-HELP",3);
		for (int expx=1;expx<2;expx++)
		{
			for (int expy=0;expy<13;expy++)
			{	
				Team.unicastCost = 4;
				Agent.calculationCost = 2;			
				SimulationEngine.disturbanceLevel = 0.1;
				TeamTask.initResCoef = 160;
				AdvActionMAPAgent.impFactor = HelperInitActionMAPAgent.impFactor = 24;
				AdvActionMAPAgent.WLL = -0.1;
				HelperInitActionMAPAgent.WHH = expy * 0.05;
				Team.paramValues = HelperInitActionMAPAgent.WHH + "";
				Team.dbgScores = false;
				AdvActionMAPAgent.useTeamWellbeing = HelperInitActionMAPAgent.useTeamWellbeing = false;

				Team.broadcastCost = Team.unicastCost * (Team.teamSize-1);
				TeamTask.helpOverhead = 20;			
				TeamTask.cellReward = 100;
				TeamTask.achievementReward = 2000;
				TeamTask.assignmentOverhead = 0;
				
				AdvActionMAPAgent.requestThreshold = 489;
				AdvActionMAPAgent.lowCostThreshold = 50;
				AdvActionMAPAgent.importanceVersion = HelperInitActionMAPAgent.importanceVersion = impVersion;
				
				HelperInitActionMAPAgent.offerThreshold = 299;
				HelperInitActionMAPAgent.EPSILON = 0.1;
				
				BasicActionMAPAgent.requestThreshold = 489;
				
				RAAgent.EPSILON = 0.2;
				RAAgent.WREASSIGN = 0.3;
				RAAgent.WREASSIGNREQ = 1.0;

				/* Initialize and run the experiment */
				se.initializeExperiment(numberOfRuns);			
				int[] teamScores = se.runExperiment();


				/* Print the results */
				strMsg =  Team.paramValues;
				//strMsg += TeamTask.initResCoef;
				for (int i=0;i<teams.length;i++)
					strMsg += "," + teamScores[i];
				
				logText(strMsg,3);
			}
		}
		//logText("-----------------------------------------",3);
	}
	
	/*
	 * Custom simulation number four
	 */
	public static void runSimulation4(int numberOfRuns, int impVersion) throws Exception {
	//	if(wbVersion < 1 || wbVersion > 2)
		//	throw new Exception("Wellbeing value is invalid!");
		if(numberOfRuns < 1)
			throw new Exception("numberOfRuns is invalid!");
		
		SimulationEngine.colorRange = 
			new int[] {0, 1, 2, 3, 4, 5};
		SimulationEngine.numOfColors =  
			SimulationEngine.colorRange.length;
		SimulationEngine.actionCostsRange = 
		new int[] {10, 40, 150, 200, 250, 300, 350, 500};	
	
		/* Create the teams involved in the simulation */
		Team.teamSize = 8;
		Team[] teams = new Team[4];
		teams[0] = new AdvActionMAPTeam();
		teams[1] = new HelperInitActionMAPTeam();
		teams[2] = new BasicActionMAPTeam();
		teams[3] = new NoHelpTeam();
		
		/* Create the SimulationEngine */
		SimulationEngine se = new SimulationEngine(teams);
		logText("WLL,AD-ACTION-MAP,HELPER-INIT,BASIC-ACTION,NO-HELP",4);
		for (int expx=1;expx<2;expx++)
		{
			/* vary the disturbance as the x-axis: */  
			for (int expy=-10;expy<1;expy++)
			{	
				Team.unicastCost = 3;
				Agent.calculationCost = 2;			
				SimulationEngine.disturbanceLevel = 0.1;
				TeamTask.initResCoef = 160;
				AdvActionMAPAgent.impFactor = HelperInitActionMAPAgent.impFactor = 24;
				AdvActionMAPAgent.WLL = expy * 0.05;
				HelperInitActionMAPAgent.WHH = 0.1;
				Team.paramValues = AdvActionMAPAgent.WLL + "";
				Team.dbgScores = false;
				AdvActionMAPAgent.useTeamWellbeing = HelperInitActionMAPAgent.useTeamWellbeing = false;

				Team.broadcastCost = Team.unicastCost * (Team.teamSize-1);
				TeamTask.helpOverhead = 20;			
				TeamTask.cellReward = 100;
				TeamTask.achievementReward = 2000;
				TeamTask.assignmentOverhead = 0;
				
				AdvActionMAPAgent.requestThreshold = 489;
				AdvActionMAPAgent.lowCostThreshold = 50;
				AdvActionMAPAgent.importanceVersion = HelperInitActionMAPAgent.importanceVersion = impVersion;
				
				HelperInitActionMAPAgent.offerThreshold = 299;
				HelperInitActionMAPAgent.EPSILON = 0.1;
				
				BasicActionMAPAgent.requestThreshold = 489;
				
				RAAgent.EPSILON = 0.2;
				RAAgent.WREASSIGN = 0.3;
				RAAgent.WREASSIGNREQ = 1.0;

				/* Initialize and run the experiment */
				se.initializeExperiment(numberOfRuns);			
				int[] teamScores = se.runExperiment();

				/* Print the results */
				strMsg =  Team.paramValues;
				for (int i=0;i<teams.length;i++)
					strMsg += "," + teamScores[i];
				
				logText(strMsg,4);
			}
		}
		//logText("-----------------------------------------",4);
	}
	
	/*
	 * Log message in text file specific to the experiment
	 */
	public static void logText(String strMsg, int expID)
	{
		System.out.println(strMsg);
//		try {
//			System.out.println(strMsg);
//		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("outfilename" + expID + ".txt", true)));
//		    out.println(strMsg);
//		    out.close();
//		} catch (IOException e) {
//			System.err.println("Error writing file.." + strMsg);
//		}
	}
}