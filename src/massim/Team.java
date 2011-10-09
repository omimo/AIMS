package massim;

import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import massim.Agent.AGCODE;

/**
 * Team.java
 * 
 *
 * @author Omid Alemi
 * @version 1.1 2011/10/06
 */
public class Team {

	public static int teamSize;
	public static int calculationCost;
	public static int unicastCost;
	public static int achievementReward;
	public static int helpOverhead;
	public static int cellReward;
	public static int broadcastCost;	
	public static int costThreshold;
	
	public static int initResCoef;
	
	private Agent[] agents;
	private Environment env;
	
	private int[][] actionCostMatrix;
			
	public static enum TeamStepCode {OK, DONE, ERR}
	
	/**
	 * Default constructor
	 */
	public Team() {		
		env = new Environment();
		actionCostMatrix = new int[teamSize][Environment.numOfColors];		
	}
	
	/**
	 * Prepares the team for a new run by resetting its internal values
	 * 
	 * @param agentsPos				The array of agents positions (initial positions)
	 * @param actionCostMatrix		The matrix of action costs for all the agents
	 */
	public void reset(RowCol[] agentsPos, int[][]actionCostMatrix) {
		
		for (int i=0;i<teamSize;i++)
			env.setAgentPosition(i, agentsPos[i]);
		
		for (int i=0;i<teamSize;i++)
			for (int j=0;j<Environment.numOfColors;j++)
				this.actionCostMatrix[i][j] = actionCostMatrix[i][j];			
				
		for (int i=0;i<teamSize;i++)
			agents[i].reset(actionCostMatrix[i]);
		
		//(new java.util.Scanner(System.in)).nextLine();
	}
	
	/**
	 * Called by the simulation engine in each step of simulation
	 * 
	 * @return ENDSIM 				code if the simulation is over
	 */
	public TeamStepCode step() {
		
		// 0. Update Agents Percepts				
		
		for (int i=0;i<agents.length;i++)
		{
			int[][] probActionCostMatrix = new int[Team.teamSize][Environment.numOfColors];
			
			Random rnd1 = new Random();
			Random rnd2 = new Random();
			for (int p = 0; p < Team.teamSize; p++)
				for (int q = 0; q < Environment.numOfColors; q++)						
					if (rnd1.nextDouble() < Environment.mutualAwareness || p==i)						
						probActionCostMatrix[p][q] = actionCostMatrix[p][q];						
					else							
						probActionCostMatrix[p][q] = Environment.actionCostRange[rnd2.nextInt(Environment.actionCostRange.length)];
					                     
			agents[i].perceive(Environment.board(), probActionCostMatrix, Environment.goals(), env.agentsPosition());
		}
		
		// 1. Communication Phase

		int noMsgPass = 8;
		do {
			
//			System.out.println("---- sendings ----");
			for (int i=0;i<teamSize;i++)
				agents[i].doSend();		
			
		//	System.out.println("---- receivings ----");
			for (int i=0;i<teamSize;i++)
				agents[i].doReceive();			
												
			
			//if (env().communicationMedium().isEmpty())
				noMsgPass--;			
			
		} while(noMsgPass > 0);
			
		//while (!env().communicationMedium().isEmpty() || noMsgPass > 0);
		
		// 1. Action Phase
	
//		System.out.println("---- actions ----");
		boolean allDone = true;	// this way of checking is just temporally and for tests
		for (int i=0;i<agents.length;i++)	
		{
			AGCODE c = agents[i].act();
		
			if ( c != AGCODE.DONE)
				allDone = false;
		}	
			
		//(new java.util.Scanner(System.in)).nextLine();
		
		if (allDone)			
			return TeamStepCode.DONE;
		else 
			return TeamStepCode.OK;
	}
	
	/**
	 * Enables the customized team classes to access the environment of the team
	 * 
	 * @return				The instance of the team's environment
	 */
	public Environment env() {
		return env;
	}
	
	/**
	 * Enables the customize team classes to create and set their own agent types
	 * 
	 * @param agents		The array of customized agent objects
	 */
	public void setAgents(Agent[] agents) {
		this.agents = agents;
	}
	
	/**
	 * Enables the customized team classes to get access to individual agents of the
	 * team
	 * 
	 * @param agent			The id of the desired agent
	 * @return				The instance of the agnet object with the specified id
	 */
	public Agent agent(int agent) {
		return agents[agent];
	}
	
	/**
	 * To get the collective resource points for the team
	 * 
	 * @return				The amount of resources points that all the team's agents 
	 *						own
	 */
	public int teamResourcePoints() {
		int sum = 0;
		for (Agent a: agents)
			sum += a.resourcePoints();
		return sum;
	}

	/**
	 * To get the collective reward points for the team
	 * 
	 * @return				The amount of reward points that all the team's agents own
	 */
	public int teamRewardPoints() {
		int sum = 0;
		for (Agent a: agents)
			sum += a.rewardPoints();
		return sum;
	}
}
