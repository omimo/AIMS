package massim.agents.nohelp;

import massim.RowCol;
import massim.Team;

/**
 * 
 * 
 * @author Omid Alemi
 * @version 2.0 2011/10/31
 */
public class NoHelpTeam extends Team {	
	
	/**
	 * The default constructor
	 */
	public NoHelpTeam() {
		super();		
			
		NoHelpAgent[] selfishAgents = new NoHelpAgent[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			selfishAgents[i] = new NoHelpAgent(i,commMedium());
		
		setAgents(selfishAgents);
	}	
	
	/**
	 * The overridden Team.initializeRun() method.
	 * 
	 * This calls the same method of the superclass first.
	 * 
	 * Initialized the agents, giving them their initial position, goal
	 * position, action costs, and their initial resources based on their
	 * path length.
	 */
	@Override
	public void initializeRun(
		RowCol[] initAgentsPos, RowCol[] goals, int[][]actionCostsMatrix) {
		
		super.initializeRun(initAgentsPos, goals, actionCostsMatrix);
						
		for(int i=0;i<Team.teamSize;i++)
		{
			int pathLength = calcDistance(initAgentsPos[i], goals[i]);
			
			agent(i).initializeRun(initAgentsPos[i], goals[i], 
					actionCostsMatrix[i], pathLength * initResCoef);
		}
	}
	
	/**
	 * Calculates the distance between two points in a board.
	 * 
	 * @param start					The position of the starting point
	 * @param end					The position of the ending point
	 * @return						The distance
	 */
	private int calcDistance(RowCol start, RowCol end) {
		return  Math.abs(end.row-start.row) + Math.abs(end.col-start.col) + 1;
	}	
		
}

