package massim;

/**
 * The Environment class
 * 
 * @author Omid Alemi
 * @version 1.0 2011/10/06
 */
public class Environment implements EnvAgentInterface {

	public static int numOfColors; // This can be derived from the colorRange, but for the sake of easier programming I'm keeping it like this!  
	public static int[] colorRange;
//	public static int minActionCost;
//	public static int maxActionCost;
	public static int[] actionCostRange;
	
	
	public static double disturbanceLevel;
	public static double awarenessProb;
	
	private static Board mainBoard;
	private static RowCol[] goals;
	
	private RowCol[] agentsPosition;
	private CommMedium communicationMedium;
	
	/**
	 * The constructor
	 *  
	 */
	public Environment() {
		communicationMedium = new CommMedium();
		agentsPosition = new RowCol[Team.teamSize];		
	}
	
	/**
	 * Sets the board to a new representation
	 * 
	 * @param newBoard				The desired new representation of the board 
	 */
	public static void setBoard(Board newBoard) {
		mainBoard = new Board(newBoard);
	}
	
	/**
	 * Sets the positions of the goals
	 * 
	 * @param newGoals				Array of goal positions
	 */
	public static void setGoals(RowCol[] newGoals)  {
		goals = new RowCol[newGoals.length];
		
		for (int i=0;i<goals.length;i++)
			goals[i] = newGoals[i];
	}
	
	/**
	 * Enables the access to the board's representation 
	 * 
	 * @return						The instance of the board
	 */
	public static Board board() {
		return mainBoard;
	}
	
	/**
	 * Enables the access to the goals positions
	 * 	
	 * @return						Array of goal positions
	 */
	public static RowCol[] goals() {
		return goals;
	}
	
	
	/**
	 * Returns the position of the specified agent
	 * 
	 * @param agent					The agent's id
	 * @return						The agent's position
	 */
	public RowCol agentPosition(int agent) {
		return agentsPosition[agent];
	}
	
	/**
	 * Returns the positions of all the agents with this environment instance
	 * 
	 * @return						Array of agents' positions
	 */
	public RowCol[] agentsPosition() {
		return agentsPosition;
	}
	
	/**
	 * Sets the position of an agent.
	 * 
	 * @param agent					The id of the agent
	 * @param newPos				The desired position of the agent
	 */
	public void setAgentPosition(int agent, RowCol newPos ) {
		
		agentsPosition[agent] = new RowCol(newPos.row,newPos.col);
		
	}
	
	/**
	 * Enables the access to the communication medium for the agent
	 * 
	 * @return						The instance of the communication medium of the environment
	 */
	public CommMedium communicationMedium() {
		return communicationMedium;
	}
	
	/**
	 * The move action of agents.
	 * It can be called by an agent for its own move or for some other agent's move (help).
	 * 
	 *  @param agent				The id of the agent to be moved.
	 *  @param newPos				The new position of the agent
	 *  @return						True if the action was successful / false otherwise
	 */
	public boolean move(int agent, RowCol newPos) {
		
		if (!RowCol.areNeighbors(agentsPosition[agent], newPos))
				return false;					
		
		agentsPosition[agent] = new RowCol(newPos.row,newPos.col);
					
		return true;
	}
	
	/**
	 * Converts current state of this instance of environment to a string
	 * 
	 * @return					The string representation of the environment
	 */
	@Override
	public String toString() {
		String s = "";
		s +=("\nThe Board\n");
		s +=(mainBoard.toString());
		s +=("\n");
		s +=("Agents Positions:\n");
		for (int i=0;i<agentsPosition.length;i++)
			s +="Agent "+i +": (" + agentsPosition[i].row +"," + agentsPosition[i].col +")\n";
		s +=("\n");
		s +=("Communication Channels:\n");
		s +=(communicationMedium.toString());
		return s;
	}

	/**
	 * Enables the agents to have the information about the range of action costs
	 * 
	 * @return					The action costs range in an array
	 */
	@Override
	public int[] actionCostRange() {
		return actionCostRange;		
	}
	
	
	/**
	 * Enables the agents to have the information about the range of colors on the board
	 * 
	 * @return					The color range in an array
	 */
	@Override
	public int[] colorRange() {
		return colorRange;		
	}
	
	
}
