package massim;

/**
 * The Environment class
 * 
 * @author Omid Alemi
 * @version 1.0
 */
public class Environment {

	private static Board mainBoard;
	private static Goal[] goals;
	
	private RowCol[] agentsPosition;
	private CommMedium communicationMedium;
	
	
	public static Board board() {
		return mainBoard;
	}
	
	public static Goal[] goals() {
		return goals;
	}
	
	public RowCol agentPosition(int agent) {
		return agentsPosition[agent];
	}
	
	public void setAgentPosition(int agent, RowCol newPos ) {
		agentsPosition[agent].col = newPos.col;
		agentsPosition[agent].row = newPos.row;
	}
	
	public CommMedium communicationMedium() {
		return communicationMedium;
	}
	
	
	
	
	
}
