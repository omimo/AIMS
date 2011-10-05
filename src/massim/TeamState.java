package massim;

/**
 * Class to hold the information about the team. All the agents' positions,
 * resources, points, etc. This class is used by the frontends for
 * representation and post-processing purposes only.
 * 
 * @author Omid Alemi
 * @version 1.0
 */
public class TeamState {

	private Agent[] agents;

	public TeamState(Agent[] agents) {
		// this.agents <- agents
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
	public RowCol[] agPositions() {
		return null;
	}

	/**
	 * Creates a clone of the object
	 */
	@Override
	public TeamState clone() {
		return null;
	}

}
