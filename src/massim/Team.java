package massim;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import massim.Agent.AgGameStatCode;
import massim.Agent.AgCommStatCode;
import massim.agents.advancedactionmap.AdvActionMAPAgent;

/**
 * Team.java
 * 
 * 
 * @author Omid Alemi
 * @version 1.4 2012/01/19
 */
public class Team {

	private static int nextID = 1; // for debugging purposes only
	private int id;

	public static int teamSize;

	public static double mutualAwareness;
	public static int unicastCost;
	public static int broadcastCost;
	
	private Agent[] agents;
	private CommMedium commMedium;
	private int[][] actionCostsMatrix;
	
	private RowCol[] currentPos;
	private TeamTask myTT;

	AgGameStatCode[] agentsGameStatus = new AgGameStatCode[Team.teamSize];
	AgCommStatCode[] agentsCommStatus = new AgCommStatCode[Team.teamSize];
	private static Random rnd1 = new Random();

	
	/**
	 * OK: The round executed without any problem and there is
	 *      at least one active agent.
	 *        
	 * DONE: All the agents are done.
	 * 
	 * ERR: There was a problem in the current round.
	 */
	public static enum TeamRoundCode {
		OK, DONE, ERR
	}

	private boolean debuggingInf = false;
	public int testRunCounter;

	/**
	 * Default constructor
	 */
	public Team() {
		
		id = nextID++;
		commMedium = new CommMedium(Team.teamSize);
		
		actionCostsMatrix = 
			new int[Team.teamSize][SimulationEngine.numOfColors];
	}

	/**
	 * Initializes the team and agents for a new run.
	 * 
	 * Called by the simulation engine (SimulationEngine.initializeRun())
	 * It should reset necessary variables values.
	 * 
	 * @param initAgentsPos					Array of initial agents	position
	 * @param goals							Array of initial goals position
	 * @param actionCostMatrix				Matrix of action costs
	 */
	public void initializeRun(TeamTask tt, int[][] actionCostsMatrix) {
		logInf("initilizing for a new run.");
		commMedium.clear();

		myTT = tt;
		
		for (int i = 0; i < teamSize; i++)
			for (int j = 0; j < SimulationEngine.numOfColors; j++)
				this.actionCostsMatrix[i][j] = actionCostsMatrix[i][j];
		
		for (int i = 0; i < teamSize; i++)
			agentsGameStatus[i] = AgGameStatCode.READY;		
		
		currentPos = new RowCol[Team.teamSize];
			
		// This part came from individual teams
		int[] subtaskAssignments = new int[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			subtaskAssignments[i]=i;
		
		//
		
		int pathLength = calcDistance(tt.startPos[0], tt.goalPos[0]);
		
		agent(0).initializeRun(tt,subtaskAssignments,currentPos,
				this.actionCostsMatrix[0], 
				pathLength * TeamTask.initResCoef + 2000);
		//
		
		for(int i=1;i<Team.teamSize;i++)
		{
			 pathLength = calcDistance(tt.startPos[i], tt.goalPos[i]);
			
			agent(i).initializeRun(tt,subtaskAssignments,currentPos,
					this.actionCostsMatrix[i], 
					pathLength * TeamTask.initResCoef);
		}
	}

	/**
	 * Starts a new round of the simulation for this team.
	 * 
	 * Called by the simulation engine (SimulationEngine.round()).
	 * 
	 * It is possible to implement error handling mechanisms for this method.
	 *  
	 * @param board							The current board representation
	 * @return								The proper TeamRoundCode based on
	 * 										the team's current state.
	 */
	public TeamRoundCode round(Board board) {
		logInf("********");
		logInf("starting a new round");
		
		/* Initialize round for agents */
		for (int i = 0; i < Team.teamSize; i++) {
			int[][] probActionCostMatrix = 
				new int[Team.teamSize][SimulationEngine.numOfColors];
			
			for (int p = 0; p < Team.teamSize; p++)
				for (int q = 0; q < SimulationEngine.numOfColors; q++)
					if (rnd1.nextDouble() < Team.mutualAwareness
							|| p == i)
						probActionCostMatrix[p][q] = 
							actionCostsMatrix[p][q];
					else
						probActionCostMatrix[p][q] = 
							SimulationEngine.actionCostsRange[
							 rnd1.nextInt(
									 SimulationEngine.actionCostsRange.length)];
				
		
			if (agentsGameStatus[i] != AgGameStatCode.BLOCKED)
				agents[i].initializeRound(board, probActionCostMatrix);
			
			agentsCommStatus[i] = AgCommStatCode.NEEDING_TO_SEND;
		}
		
		/* Communication Cycles */
		boolean allDoneComm = false;
		logInf("");
		while(!allDoneComm) {
			
			for (int i = 0; i < Team.teamSize; i++)
			{
				/* TODO: Double check the need of first condition */
				if (agentsGameStatus[i] != AgGameStatCode.BLOCKED &&  
						agentsCommStatus[i] != AgCommStatCode.DONE)
					agents[i].sendCycle();				
			}
			allDoneComm = true;
			
			for (int i = 0; i < Team.teamSize; i++)
			{							
				/* TODO: Double check the need of first condition */
				if (agentsGameStatus[i] != AgGameStatCode.BLOCKED &&   
						agentsCommStatus[i] != AgCommStatCode.DONE)
					agentsCommStatus[i] = agents[i].receiveCycle();
				
				if (agentsGameStatus[i] != AgGameStatCode.BLOCKED &&
					agentsCommStatus[i] != AgCommStatCode.DONE)
					allDoneComm = false;
			}
			
			commMedium.clear();
		}
		
		/* Finalize the round for agents */
		
		boolean allDone = true;
		for (int i = 0; i < Team.teamSize; i++)
		{
			
		/* If the agent were blocked before, don't call it as it doesn't have
		   enough resources to help itself nor its teammates.
		   However, call those who has reached the goal, they may help others.
		*/
	
			if (agentsGameStatus[i] != AgGameStatCode.BLOCKED)
				agentsGameStatus[i]  = agents[i].finalizeRound();
			
			if (agentsGameStatus[i] != AgGameStatCode.REACHED_GOAL && 
					agentsGameStatus[i] != AgGameStatCode.BLOCKED)
				allDone = false;
		}
				
	
		//(new Scanner(System.in)).nextLine();
		
		if (allDone)
			return TeamRoundCode.DONE;
		else 
			return TeamRoundCode.OK;
	
	}

	
	/**
	 * To get the collective reward points of the team members
	 * 
	 * @return 						The amount of reward points that all the 
	 * 								team's agents has earned
	 */
	public int teamRewardPoints() {
		int sum = 0;
		for(int s=0;s<Team.teamSize;s++)
		{
			if (currentPos[s].equals(myTT.goalPos[s]))
				sum += TeamTask.achievementReward;
			else
				sum += calcDistance(myTT.startPos[s], currentPos[s]); 
		}
		return sum;
	}

	/**
	 * Enables access to the specified agent.
	 * 
	 * @param id					The id of the agent
	 * @return						The instance of the agent
	 */
	protected Agent agent(int id) {
		return agents[id];
	}
	
	/**
	 * Sets the agents of the team.
	 * 
	 * 
	 * @param agents				The array of agents.
	 */
	protected void setAgents(Agent[] agents) {
		this.agents = agents;
	}
	
	/**
	 * Enables access to the team's communication medium.
	 * 
	 * @return						The communication medium.
	 */
	protected CommMedium commMedium() {
		return commMedium;
	}
	
	/**
	 * Prints the log message into the output if the information 
	 * debugging level is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logInf(String msg) {
		if (debuggingInf)
			System.out.println("[Team " + id + "]: " + msg);
	}
	
	/**
	 * Calculates the distance between two points in a board.
	 * 
	 * @param start					The position of the starting point
	 * @param end					The position of the ending point
	 * @return						The distance
	 */
	private int calcDistance(RowCol start, RowCol end) {
		return  Math.abs(end.row-start.row) + Math.abs(end.col-start.col) + 1;
	}

	public int getHelpReqCounts() {
		int sum = 0;
		for (Agent a: agents)
		   sum += a.numOfHelpReq;
		return sum;
	}

	public int getBidsCounts() {
		int sum = 0;
		for (Agent a: agents)
		   sum += a.numOfBids;
		return sum;
	}

	public int getSucOffersCounts() {
		int sum = 0;
		for (Agent a: agents)
		   sum += a.numOfSucOffers;
		return sum;
	}

	public int getUnSucHelpReqCounts() {
		int sum = 0;
		for (Agent a: agents)
		   sum += a.numOfUnSucHelpReq;
		return sum;
	}
}
