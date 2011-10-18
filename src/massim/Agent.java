package massim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.naming.CommunicationException;

/**
 * Agent.java An abstract class for all the agents to be used in the simulator
 * 
 * @author Omid Alemi
 * @version 1.1 2011/10/06
 */
public abstract class Agent {

	/**
	 * Agent Status Return Code AGCODE:
	 * 
	 * OK Normal behavior of the agent while it is moving ERR The sign of
	 * internal error within the agent DONE Means the agent has reached the
	 * goal, but still is active and able to help OFF Means the agent is not
	 * functioning anymore (no move, no communication, no help)
	 */
	public static enum AGCODE {
		OK, ERR, DONE, OFF
	};

	private int id;
	private EnvAgentInterface env;

	private int[] actionCosts;	
	private Path path;

	// *** The beliefs
	// Personal beliefs (mental notes)
	private int resourcePoints = 0;
	private int rewardPoints = 0;
	// Percepts
	private RowCol pos;
	private RowCol myGoalPos;
	private RowCol[] agPos;
	private Board theBoard;
	// ****

	/**
	 * The constructor
	 * 
	 * @param id				The id number of the agent; chosen by the team
	 * @param env				The team's instance of the environment 
	 */
	public Agent(int id, EnvAgentInterface env) {
		this.id = id;
		this.env = env;
	}

	/**
	 * Resets the agent's internals to prepare it for a new run
	 * 
	 * @param actionCosts       The action cost vector for this agent
	 */
	protected void reset(int[] actionCosts) {

		rewardPoints = 0;
		resourcePoints = 0;
		path = null;
		theBoard = null;

		this.actionCosts = new int[Environment.numOfColors];
		for (int i = 0; i < Environment.numOfColors; i++)
			this.actionCosts[i] = actionCosts[i];

		// reset the agents positions beliefs
		agPos = new RowCol[Team.teamSize];
		for (int i = 0; i < Team.teamSize; i++)
			this.agPos[i] = new RowCol(-1, -1);

		// reset the own positions
		pos = new RowCol(-1, -1);

		// reset the agent's goal
		myGoalPos = null;

	}

	/**
	 * Where agent performs its action. No default action. To be implemented by
	 * the customized agents.
	 * 
	 * @return AGCODE 			status code of current step
	 */
	protected AGCODE act() {

		return AGCODE.OK;
	}

	/**
	 * Called by the Team in order to enable the agent to update its information
	 * about the environment.
	 * 
	 * The team can filter the information to provide the desired observability
	 * for each agent.
	 * 
	 * @param board					The current state of the board
	 * @param actionCostsMatrix     The action cost vectors of all the agents
	 * 								(based on the mutual awareness among team
	 * 								members)
	 * @param goals			        The goals for all the agents
	 * @param initAgentsPos		        The current position of all the agents 
	 * 								within the team
	 */
	protected void perceive(Board board, int[][] actionCostsMatrix,
			RowCol[] goals, RowCol[] agentsPos) {

		// Update the action cost vector
		for (int i = 0; i < actionCostsMatrix[id].length; i++)
			this.actionCosts[i] = actionCostsMatrix[id][i];

		// Update the agents positions
		for (int i = 0; i < agentsPos.length; i++)
			this.agPos[i] = agentsPos[i];

		// Update the own positions
		pos = new RowCol(agPos[id]);

		// Update the agent's goal
		myGoalPos = new RowCol(goals[id]);

		theBoard = board;// new Board(board);
	}

	/**
	 * Sends all the outgoing messages, if any, in the current iteration in the
	 * team step()
	 */
	protected void doSend() {
		// nothing as default
	}

	/**
	 * Receives all the incoming message, if any, from other agents in the
	 * current iteration in the team cycle
	 */
	protected void doReceive() {
		// nothing as default
	}

	/**
	 * 
	 * @return 				The id of the class
	 */
	protected int id() {
		return id;
	}

	/**
	 * Returns the amount of reward points the agent owns at the moment
	 * 
	 * @return 				The amount of points the agent owns at the moment
	 */
	protected int rewardPoints() {
		return rewardPoints;
	}

	/**
	 * The amount of resource points that the agent owns at the moment
	 * 
	 * @return 				The amount of resource points that the agent owns 
	 * 						at the moment
	 */
	protected int resourcePoints() {
		return resourcePoints;
	}

	/**
	 * Increases the reward points by the specified amount
	 * 
	 * @param amount        The desired amount of points to be added
	 */
	protected void incRewardPoints(int amount) {
		rewardPoints += amount;
	}

	/**
	 * Decreases the award points by the specified amount
	 * 
	 * @param amount		The desired amount of points to be subtracted
	 */
	protected void decRewardPoints(int amount) {
		rewardPoints -= amount;
	}

	/**
	 * Increases the resource points by the specified amount
	 * 
	 * @param amount        The desired amount of points to be added
	 */
	public void incResourcePoints(int amount) {
		resourcePoints += amount;
	}

	/**
	 * Decreases the resource points by the specified amount
	 * 
	 * @param amount			The desired amount of points to be subtracted
	 */
	protected void decResourcePoints(int amount) {
		resourcePoints -= amount;
	}

	/**
	 * Enables the customized agents to get their position
	 * 
	 * @return 					The current position of the agent
	 */
	protected RowCol pos() {
		return pos;
	}

	/**
	 * Enables the customized agents to access to the environment/agent
	 * interface of the team for communication and action
	 * 
	 * @return 					The instance of the
	 */
	protected EnvAgentInterface env() {
		return env;
	}

	/**
	 * Enables the customized agents to get the position of their assigned goal
	 * 
	 * @return 					The position of the goal
	 */
	protected RowCol goalPos() {
		return myGoalPos;
	}

	/**
	 * Enables the customized agents to access their action costs vector
	 * 
	 * @return 					The action costs vector of the agent
	 */
	protected int[] actionCosts() {

		return actionCosts;
	}

	/**
	 * Calculates the cost of a given cell for the agent
	 * 
	 * @param cell				The position of the cell
	 * @return					The cost associated with the color of the 
	 * 							given cell
	 */
	protected int getCellCost(RowCol cell) {

		int[] colorRange = env().colorRange();
		int index = 0;
		/*
		 * for (int i=0;i<colorRange.length;i++) { int color =
		 * theBoard.getBoard()[cell.row][cell.col]; if (color == colorRange[i])
		 * index = i; }
		 */

		return actionCosts()[theBoard.getBoard()[cell.row][cell.col] - 10];
	}

	/**
	 * Calculates the cost of a given cell for another agent based on the 
	 * action costs of that agent
	 *  
	 * @param cell				The position of the cell
	 * @param actCost			The action costs set
	 * @return					The cost associated with the color of the
	 * 							given cell
	 */
	protected int getCellCost(RowCol cell, int[] actCost) {

		int[] colorRange = env().colorRange();
		int index = 0;
		for (int i = 0; i < colorRange.length; i++) {
			int color = theBoard.getBoard()[cell.row][cell.col];
			if (color == colorRange[i])
				index = i;
		}

		return actCost[index];
	}

	/**
	 * Enables the customized agent to access to the board within the internal
	 * agent's beliefs
	 * 
	 * @return				The instance of the board
	 */
	protected Board theBoard() {
		return theBoard;
	}

	/**
	 * DEPRECATED
	 * Finds the lowest cost path among the shortest paths using a breadth-first
	 * search algorithm.
	 * 
	 * The method passes the current position of the agent as the starting point
	 * and position of the goal as the ending point of the path 
	 */
	protected void findPath_dep() {
		// log("Does not have a path, finding one ...");

		ArrayList<Path> paths = Path.getShortestPaths(pos(), goalPos(),
				theBoard().getBoard(), 50);

		int minCost = Integer.MAX_VALUE;
		Path minPath = new Path();

		for (Path pp : paths) {
			int cost = pp.totalPathCost(theBoard().getBoard(), actionCosts());

			if (cost < minCost) {
				minPath = pp;
				minCost = cost;
			}
		}

		path = new Path(minPath);
		// log("My path will be: " + path);
	}

	/**
	 * Finds the lowest cost path among shortest paths of a rectangular board
	 * based on the Polajnar's algorithm.
	 * 
	 * NOTE: only the start and end points can only be at the diagonal corners
	 * of the board
	 * 
	 */
	protected void findPath() {
		PolajnarPath pp = new PolajnarPath();
		Path shortestPath = new Path(pp.findShortestPath(
				boardToCosts(theBoard.getBoard(), actionCosts),
				theBoard.getBoard().length, theBoard.getBoard()[0].length));
		path = new Path(shortestPath);
	}

	/**
	 * Calculates the costs associated to each square on the board.
	 * This method is used by the path finding algorithm.
	 * 
	 * @param board					The board setting
	 * @param actionCosts			The action costs set
	 * @return						The 2dim array; each entry represents 
	 * 								the cost associated with the square at
	 * 								the same position of the entry
	 */
	private int[][] boardToCosts(int[][] board, int[] actionCosts) {
		int[][] costs = new int[board.length][board[0].length];

		for (int i = 0; i < costs.length; i++)
			for (int j = 0; j < costs[0].length; j++)
				costs[i][j] = actionCosts[board[i][j] - 10];

		return costs;
	}

	/**
	 * Enables the customized agents to access the selected path
	 *  
	 * @return						The instance of the path. 
	 */
	protected Path path() {
		return path;
	}
}
