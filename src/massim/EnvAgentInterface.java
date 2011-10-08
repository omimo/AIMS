package massim;

/**
 * The interface of the environment that the agents can use to access the 
 * environment
 * 
 * @author Omid Alemi
 * @version 1.0 2011/10/06
 */
public interface EnvAgentInterface {
	public CommMedium communicationMedium();
	public boolean move(int agent, RowCol newPos);
	public int[] actionCostRange();
	public int[] colorRange();
}
