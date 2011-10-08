package massim;

import java.util.HashMap;

import massim.Agent.AGCODE;

/**
 * Team.java
 * 
 *
 * @author Omid Alemi
 * @version 1.0 2011/10/01
 */
public class Team {

	public static int teamSize;
	public static int calculationCost;
	public static int communicationCost;
	public static int achievementReward;
	public static int helpOverhead;
	
	private Agent[] agents;
	private Environment env;
	
	private int[][] actionCostMatrix;
			
	public static enum TeamStepCode {OK, DONE, ERR}
	
	/**
	 * Default constructor
	 */
	public Team() {		
		env = new Environment(teamSize);
		actionCostMatrix = new int[teamSize][Environment.numOfColors];
		
	}
	
	public void reset(RowCol[] agentsPos, int[][]actionCostsMatrix) {
		
		for (int i=0;i<teamSize;i++)
			env.setAgentPosition(i, agentsPos[i]);
		
		for (int i=0;i<teamSize;i++)
			for (int j=0;j<Environment.numOfColors;j++)
				this.actionCostMatrix[i][j] = actionCostsMatrix[i][j];			
				
		for (int i=0;i<teamSize;i++)
			agents[i].reset(actionCostsMatrix[i]);
	}
	
	/**
	 * Called by the simulator in each step of simulation
	 * @return ENDSIM code if the simulation is over
	 */
	public TeamStepCode step() {
		
		// 0. Update Agents Percepts				
				
		for (int i=0;i<agents.length;i++)				
			agents[i].perceive(Environment.board(), actionCostMatrix, Environment.goals(), env.agentsPosition());
		
		
		// 1. Communication Phase

		int noMsgPass = 1;
		do {
			for (int i=0;i<teamSize;i++)
				agents[i].doSend();
			
			//System.out.println("The env after send step: "+env().toString());
			
			for (int i=0;i<teamSize;i++)
				agents[i].doReceive();
			
			//System.out.println("The env after rec step: "+env().toString());
			
			if (env().communicationMedium().isEmpty())
				noMsgPass--;			
			
		} while (!env().communicationMedium().isEmpty() || noMsgPass > 0);
		
		// 1. Action Phase
	
		boolean allDone = true;
		for (int i=0;i<agents.length;i++)	
			if (agents[i].act() != AGCODE.OFF)
				allDone = false;
			
			
		if (allDone)			
			return TeamStepCode.DONE;
		else 
			return TeamStepCode.OK;
	}
	
	public Environment env() {
		return env;
	}
	
	public void setAgents(Agent[] agents) {
		this.agents = agents;
	}
	
	public Agent agent(int agent) {
		return agents[agent];
	}
	
	public int teamResourcePoints() {
		int sum = 0;
		for (Agent a: agents)
			sum += a.resourcePoints();
		return sum;
	}

	public int teamRewardPoints() {
		int sum = 0;
		for (Agent a: agents)
			sum += a.rewardPoints();
		return sum;
	}
}
