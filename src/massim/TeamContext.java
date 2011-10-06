package massim;

/**
 * Class to hold the information about the team. All the agents' positions,
 * resources, points, etc. This class is used by the frontends for
 * representation and post-processing purposes only.
 * 
 * Can be used to initial the team as well as to get the final state of the
 * team
 * 
 * @author Omid Alemi
 * @version 1.0
 */
public class TeamContext {

	private Agent[] agents;
	private RowCol[] agentsPosition;
	
	
	/**
	 * The constructor
	 * @param agents
	 */
	public TeamContext(Agent[] agents) {
		for (int i=0;i<agents.length;i++)
			this.agents[i] = agents[i];
	}

	/**
	 * The sum of all the team members points
	 * 
	 * @return
	 */
	public int teamPoints() {
		int teamPoints = 0;

		for (Agent a : agents)
			teamPoints += a.points();

		return teamPoints;
	}

	/**
	 * The sum of all the team members resources
	 * 
	 * @return
	 */
	public int teamResources() {
		int teamResources = 0;

		for (Agent a : agents)
			teamResources += a.resources();

		return teamResources;
	}

	/**
	 * To get the position of all the agents of the team
	 * 
	 * @return An array of agents positions
	 */
	public RowCol[] agentsPosition() {			
		
		return agentsPosition;
	}

	/**
	 * To get the agents (by the Team)
	 * @return An array of agents
	 */
	public Agent[] agents() {
		return agents;
	}
	
	/**
	 * Creates a clone of the object
	 */
	@Override
	public TeamContext clone() {
		return null;
	}

}
