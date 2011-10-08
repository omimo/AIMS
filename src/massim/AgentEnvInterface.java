package massim;

public interface AgentEnvInterface {
	public CommMedium communicationMedium();
	public boolean move(int agent, RowCol newPos);
	public int[] actionCostRange();
	public int[] colorRange();
}
