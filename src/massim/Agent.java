package massim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.naming.CommunicationException;

/**
 * Agent.java
 * An abstract class for all the agents to be used in the 
 * simulator
 *
 * @author Omid Alemi
 * @version 1.1 2011/10/06
 */
public abstract class Agent {

	/**
	 * Agent Status Return Code AGCODE:
	 * 
	 * OK					Normal behavior of the agent while it is moving
	 * ERR					The sign of internal error within the agent
	 * DONE					Means the agent has reached the goal, but still is active and able to help 
	 * OFF					Means the agent is not functioning anymore (no move, no communication, no help)
	 */
	public static enum AGCODE { OK, ERR, DONE, OFF }; 
	
	private int id;
	private EnvAgentInterface env;
	
	private int[] actionCosts;		
	private Board theBoard;
	private Path path;
	
	// *** The beliefs
	// Personal beliefs (mental notes)
	private int resourcePoints = 0;
	private int rewardPoints = 0;	
	// Percepts		
	private RowCol pos;
	private RowCol myGoalPos;
	private RowCol[] agPos;
	// ****
	
	
	/**
	 * The constructor
	 * 
	 */
	public Agent(int id,EnvAgentInterface env) {
		this.id = id;
		this.env = env;		
	}
	
	/** 
	 * Resets the agent's internals to prepare it for a new run
	 * 
	 * @param actionCosts				The action cost vector for this agent 
	 */
	public void reset(int[] actionCosts) {
		
		rewardPoints = 0;
		resourcePoints = 0;
		path = null;
		
		this.actionCosts = new int[Environment.numOfColors];
		for (int i=0;i<Environment.numOfColors;i++)
			this.actionCosts[i] = actionCosts[i];
	
		
		// reset the agents positions beliefs
		agPos = new RowCol[Team.teamSize];
		for (int i=0;i<Team.teamSize;i++)
			this.agPos[i] = new RowCol(-1,-1);
		
		// reset the own positions
		pos = new RowCol(-1, -1);
		
		// reset the agent's  goal
		myGoalPos = null;
		
	}
				
	/**
	 * Where agent performs its action.
	 * No defualt action.
	 * To be implemented by the customized agents.
	 *	 
	 * @return 				AGCODE status code of current step
	 */
	public AGCODE act() {

		return AGCODE.OK;
	}
	
		
    /**
     * Called by the Team in order to enable the agent to update its information 
     * about the environment.
	 * 
	 * We can pass all the information to the agent, but it can filer them
	 * so that it can have partial observability.
	 * 
     * @param board 				The current state of the board
     * @param actionCostsMatrix		The action cost vectors of all the agents
     * @param goals 				The goals for all the agents
     * @param agentsPos 			The current position of all the agents within
     *        						the team
     */
	public void perceive(Board board, int[][] actionCostsMatrix, RowCol[] goals, RowCol[] agentsPos) {

		
		// Update the action cost vector		
		for (int i=0;i<actionCostsMatrix[id].length;i++)
			this.actionCosts[i] = actionCostsMatrix[id][i];
		
		// Update the agents positions
		for (int i=0;i<agentsPos.length;i++)
			this.agPos[i] = agentsPos[i];
		
		// Update the own positions
		pos = agPos[id];
		
		// Update the agent's  goal
		myGoalPos = new RowCol(goals[id].row,goals[id].col);	
		
		theBoard = new Board(board);
	}
	
	/**
	 * Sends all the outgoing messages, if any, in the current iteration 
	 * in the team step()
	 */
	public void doSend() {
		// nothing as default
	}
	
	/**
	 * Receives all the incoming message, if any, from other agents in 
	 * the current iteration in the team cycle 
	 */
	public void doReceive() {
		// nothing as default
	}
	
		
	/**
	 * 
	 * @return 				The id of the class
	 */
	public int id() {
		return id;
	}
	
	/**
	 * Returns the amount of reward points the agent owns at the moment
	 * 
	 * @return 				The amount of points the agent owns at the moment
	 */
	public int rewardPoints() {
		return rewardPoints;
	}
	
	/**
	 * The amount of resource points that the agent owns at the moment
	 * 
	 * @return 				The amount of resource points that the agent owns at the moment
	 */
	public int resourcePoints() {
		return resourcePoints;
	}
	
	/**
	 * Increases the reward points by the specified amount
	 * 
	 * @param amount		The desired amount of points to be added
	 */
	public void incRewardPoints(int amount) {
		rewardPoints += amount;
	}
	
	/**
	 * Decreases the award points by the specified amount
	 * 
	 * @param amount		The desired amount of points to be subtracted
	 */
	public void decRewardPoints(int amount) {
		rewardPoints -= amount;
	}
	
	/**
	 * Increases the resource points by the specified amount
	 * 
	 * @param amount		The desired amount of points to be added
	 */
	public void incResourcePoints(int amount) {
		resourcePoints += amount;
	}
	
	/**
	 * Decreases the resource points by the specified amount
	 * 
	 * @param amount		The desired amount of points to be subtracted
	 */
	public void decResourcePoints(int amount) {
		resourcePoints -= amount;
	}
	
	/**
	 * Enables the customized agents to get their position
	 * 
	 * @return				The current position of the agent
	 */
	public RowCol pos() {
		return pos;
	}
	
	/**
	 * Enables the customized agents to access to the environment/agent interface
	 * of the team for communication and action
	 * 
	 * @return				The instance of the 
	 */
	public EnvAgentInterface env() {
		return env;
	}
	
	/**
	 * Enables the customized agents to get the position of their assigned goal
	 * 
	 * @return				The positon of the goal
	 */
	public RowCol goalPos() {
		return myGoalPos;
	}

	
	/**
	 * Enables the customized agents to access their action costs vector
	 * 
	 * @return				The action costs vector of the agent
	 */
	public int[] actionCosts() {
		
		return actionCosts; 
	}
	
	public int getCellCost(RowCol cell) {
		
		int [] colorRange = env().colorRange();		
		int index = 0;
		for (int i=0;i<colorRange.length;i++)
		{
			int color = theBoard.getBoard()[cell.row][cell.col];
			if (color == colorRange[i])
				index = i;			
		}
		
		return actionCosts()[index];			
	}	
	
	protected int getCellCost(RowCol cell, int[] actCost) {
		
		int [] colorRange = env().colorRange();		
		int index = 0;
		for (int i=0;i<colorRange.length;i++)
		{
			int color = theBoard.getBoard()[cell.row][cell.col];
			if (color == colorRange[i])
				index = i;			
		}
		
		return actCost[index];			
	}	

	public Board theBoard() {
		return theBoard;
	}
	
	public void findPath() {
		//log("Does not have a path, finding one ...");
		
		ArrayList<Path> paths =  Path.getShortestPaths(pos(), goalPos(), theBoard().getBoard(),10);
		
		int minCost = Integer.MAX_VALUE;
		Path minPath = new Path();
		
		for (Path pp : paths)
		{
			int cost = pp.totalPathCost(theBoard().getBoard(), actionCosts());
			
			if (cost < minCost)
				{
					minPath = pp;
					minCost = cost; 
				}
		}
		

		path = new Path(minPath);
		//log("My path will be: " + path);
	}
	
	protected Path path() {
		return path;
	}
}


