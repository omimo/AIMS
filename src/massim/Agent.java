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
	private int rewardPoints = 0;

	private RowCol pos;
	private RowCol goalPos;
	private Board theBoard;

	public Agent(int id) {
		this.id = id;
	}

	protected void initializeRun(RowCol initialPosition, RowCol goalPosition,
			int[] actionCosts) {
		this.pos = initialPosition;
		this.goalPos = goalPosition;
		System.arraycopy(actionCosts, 0, this.actionCosts, 0,
				actionCosts.length);
	}

	protected void initializeRound(Board board, int[][] actionCostsMatrix) {
		this.theBoard = board;

	}

	protected agStateCode sendCycle() {
		
		return agStateCode.DONE;
	}

	protected agStateCode receiveCycle() {

		
		return agStateCode.DONE;
	}

	protected agRoundCode finalizeRound() {
		
		return agRoundCode.READY;
	}
	
	protected int id() {
		return id;
	}

	protected int rewardPoints() {
		return rewardPoints;
	}

	protected int resourcePoints() {
		return resourcePoints;
	}

	public void incResourcePoints(int amount) {
		resourcePoints += amount;
	}

	protected void decResourcePoints(int amount) {
		resourcePoints -= amount;
	}

	protected RowCol pos() {
		return pos;
	}

	/**
	 * Enables the customized agents to get the position of their assigned goal
	 * 
	 * @return The position of the goal
	 */
	protected RowCol goalPos() {
		return goalPos;
	}

	/**
	 * Enables the customized agents to access their action costs vector
	 * 
	 * @return The action costs vector of the agent
	 */
	protected int[] actionCosts() {

		return actionCosts;
	}

	/**
	 * Calculates the cost of a given cell for the agent
	 * 
	 * @param cell
	 *            The position of the cell
	 * @return The cost associated with the color of the given cell
	 */
	protected int getCellCost(RowCol cell) {
		int color = theBoard.getBoard()[cell.row][cell.col];
		return actionCosts()[color];
	}

	/**
	 * Calculates the cost of a given cell for another agent based on the action
	 * costs of that agent
	 * 
	 * @param cell
	 *            The position of the cell
	 * @param actCost
	 *            The action costs set
	 * @return The cost associated with the color of the given cell
	 */
	protected int getCellCost(RowCol cell, int[] actCost) {

		int color = theBoard.getBoard()[cell.row][cell.col];
		return actCost[color];
	}

	/**
	 * Enables the customized agent to access to the board within the internal
	 * agent's beliefs
	 * 
	 * @return The instance of the board
	 */
	protected Board theBoard() {
		return theBoard;
	}

	/**
	 * Finds the lowest cost path among shortest paths of a rectangular board
	 * based on the Polajnar's algorithm V2.
	 * 
	 * NOTE: only the start and end points can only be at the diagonal corners
	 * of the board
	 * 
	 */
	protected void findPath() {
		PolajnarPath2 pp = new PolajnarPath2();
		Path shortestPath = new Path(pp.findShortestPath(
				boardToCosts(theBoard.getBoard(), actionCosts), pos, goalPos));
		path = new Path(shortestPath);
	}

	/**
	 * Calculates the costs associated to each square on the board. This method
	 * is used by the path finding algorithm.
	 * 
	 * @param board
	 *            The board setting
	 * @param actionCosts
	 *            The action costs set
	 * @return The 2dim array; each entry represents the cost associated with
	 *         the square at the same position of the entry
	 */
	private int[][] boardToCosts(int[][] board, int[] actionCosts) {
		int[][] costs = new int[board.length][board[0].length];

		for (int i = 0; i < costs.length; i++)
			for (int j = 0; j < costs[0].length; j++)
				costs[i][j] = actionCosts[board[i][j]];

		return costs;
	}

	/**
	 * Enables the customized agents to access the selected path
	 * 
	 * @return The instance of the path.
	 */
	protected Path path() {
		return path;
	}
	
	protected boolean move() {
		
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
