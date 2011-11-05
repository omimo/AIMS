package massim;

import java.util.Random;

import massim.Agent.agRoundCode;
import massim.Agent.agStateCode;

/**
 * Team.java
 * 
 * 
 * @author Omid Alemi
 * @version 1.2 2011/10/17
 */
public class Team {

	private static int nextID = 1; // for debugging purposes only
	private int id;

	public static int teamSize;
	public static int initResCoef;
	public static double mutualAwareness;
	
	private Agent[] agents;
	private CommMedium commMedium;
	private int[][] actionCostsMatrix;

	agRoundCode[] agentsGameStatus = new agRoundCode[Team.teamSize];
	agStateCode[] agentsCommStatus = new agStateCode[Team.teamSize];
	private static Random rnd1 = new Random();

	
	/**
	 * OK: The round executed without any problem and there is
	 *      at least one active agent.
	 *        
	 * MEND: All the agents are done.
	 * 
	 * ERR: There was a problem in the current round.
	 */
	public static enum TeamRoundCode {
		OK, DONE, ERR
	}

	private boolean debuggingInf = true;
	public int testRunCounter;

	/**
	 * Default constructor
	 */
	public Team() {
		id = nextID++;
		commMedium = new CommMedium(Team.teamSize);
		
		actionCostsMatrix = new int[Team.teamSize][SimulationEngine.numOfColors];
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
	public void initializeRun(RowCol[] initAgentsPos, RowCol[] goals,
			int[][] actionCostMatrix) {
		logInf("initilizing for a new run.");
		commMedium.clear();

		for (int i = 0; i < teamSize; i++)
			for (int j = 0; j < SimulationEngine.numOfColors; j++)
				this.actionCostsMatrix[i][j] = actionCostMatrix[i][j];
		
		for (int i = 0; i < teamSize; i++)
			agentsGameStatus[i] = agRoundCode.READY;
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
				
			if (agentsGameStatus[i] != agRoundCode.BLOCKED)
				agents[i].initializeRound(board, probActionCostMatrix);
			
			agentsCommStatus[i] = agStateCode.NEEDING_TO_SEND;
		}
		
		/* Communication Cycles */
		boolean allDoneComm = false;
		logInf("");
		while(!allDoneComm) {
			
			for (int i = 0; i < Team.teamSize; i++)
			{
				/* TODO: Double check the need of first condition */
				if (agentsGameStatus[i] != agRoundCode.BLOCKED &&  
						agentsCommStatus[i] != agStateCode.DONE)
					agents[i].sendCycle();				
			}
			allDoneComm = true;
			
			for (int i = 0; i < Team.teamSize; i++)
			{							
				/* TODO: Double check the need of first condition */
				if (agentsGameStatus[i] != agRoundCode.BLOCKED &&   
						agentsCommStatus[i] != agStateCode.DONE)
					agentsCommStatus[i] = agents[i].receiveCycle();
				
				if (agentsGameStatus[i] != agRoundCode.BLOCKED &&
					agentsCommStatus[i] != agStateCode.DONE)
					allDoneComm = false;
			}			
		}
		
		/* Finalize the round for agents */
		
		boolean allDone = true;
		for (int i = 0; i < Team.teamSize; i++)
		{
			
		/* If the agent were blocked before, don't call it as it doesn't have
		   enough resources to help itself nor its teammates.
		   However, call those who has reached the goal, they may help others.
		*/
	
			if (agentsGameStatus[i] != agRoundCode.BLOCKED)
				agentsGameStatus[i]  = agents[i].finalizeRound();
			
			if (agentsGameStatus[i] != agRoundCode.REACHED_GOAL && 
					agentsGameStatus[i] != agRoundCode.BLOCKED)
				allDone = false;
		}
		
		commMedium.clear();
		
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
		for (Agent a: agents)
		   sum += a.rewardPoints();
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
	 * Prints the log message into the output if the information 
	 * debugging level is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logInf(String msg) {
		if (debuggingInf)
			System.out.println("[Team " + id + "]: " + msg);
	}
}
