package massim;

/**
 * The Environment class
 * 
 * @author Omid Alemi
 * @version 1.0
 */
public class Environment implements AgentEnvInterface {

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
	
	public Environment(int teamSize) {
		communicationMedium = new CommMedium(teamSize);
		agentsPosition = new RowCol[teamSize];		
	}
	
	public static void setBoard(Board newBoard) {
		mainBoard = new Board(newBoard);
	}
	
	public static void setGoals(RowCol[] newGoals)  {
		goals = new RowCol[newGoals.length];
		
		for (int i=0;i<goals.length;i++)
			goals[i] = newGoals[i];
	}
	
	public static Board board() {
		return mainBoard;
	}
	
	public static RowCol[] goals() {
		return goals;
	}
	
	public RowCol agentPosition(int agent) {
		return agentsPosition[agent];
	}
	
	public RowCol[] agentsPosition() {
		return agentsPosition;
	}
	
	public void setAgentPosition(int agent, RowCol newPos ) {
		
		agentsPosition[agent] = new RowCol(newPos.row,newPos.col);
		
	}
	
	public CommMedium communicationMedium() {
		return communicationMedium;
	}
	
	public boolean move(int agent, RowCol newPos) {
		
		if (!RowCol.areNeighbors(agentsPosition[agent], newPos))
				return false;					
		
		agentsPosition[agent] = newPos;
					
		return true;
	}
	
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

	@Override
	public int[] actionCostRange() {
		return actionCostRange;		
	}
	
	@Override
	public int[] colorRange() {
		return colorRange;		
	}
	
	
}
