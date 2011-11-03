package massim;

/**
 * Agent.java An abstract class for all the agents to be used in the simulator
 * 
 * @author Omid Alemi
 * @version 2.0 2011/10/31
 */
public abstract class Agent {

	public static int cellReward;
	
	protected static enum agRoundCode {
		READY, REACHED_GOAL, RESOURCE_BLOCKED, BLOCKED
	}
	
	protected static enum agStateCode {
		DONE, NEEDING_TO_SEND, NEEDING_TO_REC 
	}

	private int id;

	private int[] actionCosts;
	private Path path;

	private int resourcePoints = 0;

	private RowCol pos;
	private RowCol goalPos;
	private Board theBoard;

	
	/**
	 * The constructor.
	 * 
	 * The team will pass the id to the agent.
	 * 
	 * @param id			The id of the agent being created.
	 */
	public Agent(int id) {
		this.id = id;
		goalPos = null;
		pos = null;
		theBoard = null;
		path = null;
	}

	/**
	 * Initializes the agent for a new run.
	 * 
	 * Called by Team.initializeRun()

	 * 
	 * @param initialPosition			The initial position of this agent
	 * @param goalPosition				The goal position for this agent
	 * @param actionCosts				The agent's action costs vector
	 * @param initResourcePoints		The initial resource points given
	 * 									to the agent by its team.
	 */
	public void initializeRun(RowCol initialPosition, RowCol goalPosition,
			int[] actionCosts, int initResourcePoints) {
		this.pos = initialPosition;
		this.goalPos = goalPosition;
		
		this.actionCosts = new int[actionCosts.length];
		System.arraycopy(actionCosts, 0, this.actionCosts, 0,
				actionCosts.length);
		
		incResourcePoints(initResourcePoints);
	}

	/** 
	 * Initializes the agent for a new round of the game.
	 * 
	 * 
	 * @param board						The game board
	 * @param actionCostsMatrix			The matrix containing the action costs
	 * 									for all the agents in the team (depends
	 * 									on the level of mutual awareness in the
	 * 									team)
	 */
	protected void initializeRound(Board board, int[][] actionCostsMatrix) {
		this.theBoard = board;

	}

	/**
	 * Enables the agent to send its outgoing messages (if any)
	 * 
	 * 
	 * @return							The current state of the agent 
	 */
	protected agStateCode sendCycle() {
		
		return agStateCode.DONE;
	}

	/**
	 * Enables the agent to receive its incoming messages (if any)
	 * 
	 * 
	 * @return							The current state of the agent 
	 */
	protected agStateCode receiveCycle() {

		
		return agStateCode.DONE;
	}

	/**
	 * Enables the agent to perform any actions for this round of
	 * the game.
	 * 
	 * @return							The status of the agent after 
	 * 									this round
	 */
	protected agRoundCode finalizeRound() {
		
		return agRoundCode.READY;
	}
	
	/**
	 * Enables the agent to get their id 
	 * 
	 * @return							The id of the agent
	 */
	protected int id() {
		return id;
	}

	/**
	 * Returns the amount reward points that the agent has earned.
	 * 
	 * @return							The reward points
	 */
	public int rewardPoints() {
		
		//TODO: Calc the reward points
		
		return 0;
	}

	/**
	 * Returns the amount of resource points that the agent has earned.
	 * 
	 * @return							The resource points
	 */
	protected int resourcePoints() {
		return resourcePoints;
	}

	/**
	 * Increases the resource points by the specified amount.
	 * 
	 * @param amount					The amounts to be added
	 */
	public void incResourcePoints(int amount) {
		resourcePoints += amount;
	}

	/**
	 * Decreases the resource points by the specified amount.
	 * 
	 * @param amount					The amounts to be subtracted
	 */
	protected void decResourcePoints(int amount) {
		resourcePoints -= amount;
	}

	/**
	 * Enables the agent to access its current position.
	 * 
	 * @return							The current position
	 */
	protected RowCol pos() {
		return pos;
	}

	/**
	 * Enables the agent to access its goal position.
	 * 
	 * @return 							The position of the goal
	 */
	protected RowCol goalPos() {
		return goalPos;
	}

	/**
	 * Enables the agent to access its action costs vector.
	 * 
	 * @return 							The action costs vector 
	 * 									of the agent
	 */
	protected int[] actionCosts() {

		return actionCosts;
	}

	/**
	 * Returns the cost of a given cell for this agent
	 * 
	 * @param cell			            The position of the cell
	 * @return 							The cost associated with 
	 * 									the color of the given 
	 * 									cell
	 */
	protected int getCellCost(RowCol cell) {
		int color = theBoard.getBoard()[cell.row][cell.col];
		return actionCosts()[color];
	}

	/**
	 * Returns the cost of a given cell based on the given actions
	 * cost vector. 
	 * 
	 * @param cell			            The position of the cell
	 * @param actCost		            The action costs vector
	 * @return 							The cost associated with 
	 * 									the color of the given 
	 * 									cell
	 */
	protected int getCellCost(RowCol cell, int[] actCost) {

		int color = theBoard.getBoard()[cell.row][cell.col];
		return actCost[color];
	}

	/**
	 * Enables the agent to access to the game board 
	 * 
	 * @return 							The instance of the board
	 */
	protected Board theBoard() {
		return theBoard;
	}

	/**
	 * Finds the lowest cost path among shortest paths of a rectangular board
	 * based on the Polajnar's algorithm V2.
	 * 
	 * The method uses the agents position as the starting point and the goal
	 * position as the ending point of the path.
	 */
	protected void findPath() {
		PolajnarPath2 pp = new PolajnarPath2();
		Path shortestPath = new Path(pp.findShortestPath(
				boardToCosts(theBoard.getBoard(), actionCosts), pos, goalPos));
		path = new Path(shortestPath);
	}

	/** 
	 * Creates a two dimensional array representing the cell cost
	 * based on the given action costs vector.
	 * 
	 * This method is used by the path finding algorithm.
	 * 
	 * @param board						The game board setting
	 * @param actionCosts	            The action costs
	 * @return 							The 2dim array of costs
	 */
	private int[][] boardToCosts(int[][] board, int[] actionCosts) {
		int[][] costs = new int[board.length][board[0].length];

		for (int i = 0; i < costs.length; i++)
			for (int j = 0; j < costs[0].length; j++)
				costs[i][j] = actionCosts[board[i][j]];

		return costs;
	}

	/**
	 * Enables the agent to access its path
	 * 
	 * @return 							The instance of the path.
	 */
	protected Path path() {
		return path;
	}
	
	/**
	 * Agent's move action.
	 * 
	 * Moves the agent to the next position if possible
	 * 
	 * TODO: Needs to be extended to perform help.
	 * 
	 * @return
	 */
	protected boolean move() {
		
		if (pos.equals(path.getNextPoint(pos)))
			return false;
		
		int cost = getCellCost(pos);
		if (resourcePoints >= cost)
		{
			decResourcePoints(cost);
			pos = path.getNextPoint(pos);		
			return true;
		}
		else
			return false;
	}
}
